package guru.qa.rococo.test.rest.artist;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.container.Artists;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.impl.api.ArtistApiClient;
import guru.qa.rococo.utils.ErrorMessageResolver;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static guru.qa.rococo.utils.ErrorMessageResolver.getErrorMessage;
import static guru.qa.rococo.utils.ImgBase64Utils.imageToBase64;

@RestTest
@DisplayName("rest: тесты контроллера Artist в Gateway")
public class ArtistRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final ArtistApiClient artistApiClient = new ArtistApiClient();

    @Test
    @Artist
    @DisplayName("Получение художника по ID")
    void artistShouldBeReturnedById(TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        final Response<ArtistJson> response = artistApiClient
                .getArtist(createdArtist.id().toString());

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(createdArtist, response.body());
    }

    @Test
    @DisplayName("Художник по случайному ID не найден")
    void artistShouldNotBeReturnedByRandomId() {
        String randomId = UUID.randomUUID().toString();
        final Response<ArtistJson> response = artistApiClient
                .getArtist(randomId);

        Assertions.assertEquals(404, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                String.format("Художник с ID %s не найден", randomId),
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @Artist(name = "Artist loalal 1")
    @Artist(name = "Artist loalal 2")
    @Artist(name = "Artist loalal 3")
    @DisplayName("Фильтрация художников по имени")
    void shouldReturnFilteredArtistsPageByName() {
        final Response<RestResponsePage<ArtistJson>> response = artistApiClient
                .getArtistPage("Artist loalal", 0, 10);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(3, response.body().getContent().size());
    }

    @Test
    @Artists(count = 15)
    @DisplayName("Пагинация списка художников")
    void artistsShouldBePaginated() {
        int size = 5;
        Set<ArtistJson> artists = new HashSet<>();
        int page = 0;
        while (page!= 3) {
            Response<RestResponsePage<ArtistJson>> response = artistApiClient.getArtistPage(null, page, size);
            Assertions.assertTrue(response.isSuccessful());
            Assertions.assertNotNull(response.body());
            artists.addAll(response.body().getContent());
            page++;
        }
        Assertions.assertEquals(15, artists.size());
    }

    @Test
    @ApiLogin
    @DisplayName("Создание нового художника")
    void artistShouldBeCreated(@Token String token) {
        ArtistJson artist = ArtistJson.randomArtist();
        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artist);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(artist.addId(response.body().id()), response.body());
    }

    @Test
    @DisplayName("Создание художника без токена возвращает 401")
    void artistShouldNotBeCreatedWithEmptyToken() {
        ArtistJson artist = ArtistJson.randomArtist();
        final Response<ArtistJson> response = artistApiClient
                .createArtist(null, artist);

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Требуется авторизация. Пожалуйста, выполните вход.",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @DisplayName("Создание художника с недействительным токеном возвращает 401")
    void artistShouldNotBeCreatedWithInvalidToken() {
        ArtistJson artist = ArtistJson.randomArtist();
        String invalidToken = "invalidToken";
        final Response<ArtistJson> response = artistApiClient
                .createArtist(invalidToken, artist);

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Недействительный токен доступа.",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Создание художника с ID должно завершиться ошибкой")
    void artistShouldNotBeCreatedWithNonEmptyId(@Token String token) {
        ArtistJson artistJson = ArtistJson.randomArtist().addId(UUID.randomUUID());
        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals(400, response.code());
        Assertions.assertEquals("ID не должен быть задан при создании художника", errorMessage);
    }

    @Test
    @ApiLogin
    @Artist
    @DisplayName("Создание дубликата художника должно завершиться ошибкой")
    void artistShouldNotBeCreatedWithDuplicateName(@Token String token, TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        ArtistJson artistJson = new ArtistJson(
                null,
                createdArtist.name(),
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(409, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals("Художник с таким именем уже существует", ErrorMessageResolver.getErrorMessage(response.errorBody()));
    }

    @Test
    @ApiLogin
    @DisplayName("Создание художника с пустым именем (пробелы) возвращает ошибку")
    void artistShouldNotBeCreatedWithBlankName(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                "      ",
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Имя не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание художника с пустым именем возвращает ошибку")
    void artistShouldNotBeCreatedWithEmptyName(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                "",
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Имя не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание художника с именем >255 символов возвращает ошибку")
    void artistShouldNotBeCreatedWithLongName(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                RandomDataUtils.randomWord(300),
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Имя не может быть длиннее 255 символов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание художника с биографией >2000 символов возвращает ошибку")
    void artistShouldNotBeCreatedWithLongBiography(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomWord(2002),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Биография не может быть длиннее 2000 символов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание художника с фото >1MB возвращает ошибку")
    void artistShouldNotBeCreatedWithLargeImage(@Token String token){
        String image = imageToBase64("img/1_1mb_photo.png");
        ArtistJson artistJson = new ArtistJson(
                null,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomWord(30),
                image
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Размер фото не может превышать 1 MB", errorMessage);
    }


    @Test
    @ApiLogin
    @Artist
    @DisplayName("Обновление данных художника")
    void artistShouldBeUpdated(@Token String token, TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        ArtistJson expectedArtist = ArtistJson.randomArtist().addId(createdArtist.id());
        final Response<ArtistJson> response = artistApiClient
                .updateArtist(token, expectedArtist);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(expectedArtist, response.body());
    }

    @Test
    @Artist
    @DisplayName("Обновление художника без токена возвращает 401")
    void artistShouldNotBeUpdatedWithoutToken(TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        final Response<ArtistJson> response = artistApiClient
                .updateArtist(null, createdArtist);

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Требуется авторизация. Пожалуйста, выполните вход.",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление несуществующего художника возвращает 404")
    void artistShouldNotBeUpdatedWithRandomId(@Token String token) {
        UUID randomId = UUID.randomUUID();
        ArtistJson artistJson = ArtistJson.randomArtist().addId(randomId);
        final Response<ArtistJson> response = artistApiClient
                .updateArtist(token, artistJson);

        Assertions.assertEquals(404, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                String.format("Художник с ID %s не найден", randomId),
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @Artists(count = 2)
    @DisplayName("Обновление с дублирующим именем художника возвращает 409")
    void shouldNotUpdatedArtistWithDuplicateName(@Token String token, TestData testData) {
        ArtistJson firstArtist = testData.artists().getFirst();
        ArtistJson secondArtist = testData.artists().getLast();

        ArtistJson artistJson = new ArtistJson(
                firstArtist.id(),
                secondArtist.name(),
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .updateArtist(token, artistJson);

        Assertions.assertEquals(409, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals("Художник с таким именем уже существует", ErrorMessageResolver.getErrorMessage(response.errorBody()));
    }
}
