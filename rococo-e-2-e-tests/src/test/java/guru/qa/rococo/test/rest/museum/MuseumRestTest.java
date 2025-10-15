package guru.qa.rococo.test.rest.museum;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.container.Museums;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.GeoJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.impl.api.MuseumApiClient;
import guru.qa.rococo.service.impl.db.CountryDbClient;
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
@DisplayName("rest: тесты контроллера Museum в Gateway")
public class MuseumRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final MuseumApiClient museumApiClient = new MuseumApiClient();
    private final CountryDbClient countryDbClient = new CountryDbClient();


    @Test
    @Museum
    @DisplayName("Получение музея по ID")
    void museumShouldBeReturnedById(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        final Response<MuseumJson> response = museumApiClient
                .getMuseum(createdMuseum.id().toString());

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(createdMuseum, response.body());
    }

    @Test
    @DisplayName("Музей по случайному ID не найден")
    void museumShouldNotBeReturnedByRandomId() {
        String randomId = UUID.randomUUID().toString();
        final Response<MuseumJson> response = museumApiClient
                .getMuseum(randomId);

        Assertions.assertEquals(404, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                String.format("Музей с ID %s не найден", randomId),
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @Museum(title = "Museum bnvkfg 1")
    @Museum(title = "Museum bnvkfg 2")
    @Museum(title = "Museum bnvkfg 3")
    @DisplayName("Фильтрация музеев по названию")
    void shouldReturnFilteredMuseumsPageByTitle() {
        final Response<RestResponsePage<MuseumJson>> response = museumApiClient
                .getMuseumPage("Museum bnvkfg", 0, 10);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(3, response.body().getContent().size());
    }

    @Test
    @Museums(count = 15)
    @DisplayName("Пагинация списка музеев")
    void museumsShouldBePaginated() {
        int size = 5;
        Set<MuseumJson> museums = new HashSet<>();
        int page = 0;
        while (page!= 3) {
            Response<RestResponsePage<MuseumJson>> response = museumApiClient.getMuseumPage(null, page, size);
            Assertions.assertTrue(response.isSuccessful());
            Assertions.assertNotNull(response.body());
            museums.addAll(response.body().getContent());
            page++;
        }
        Assertions.assertEquals(15, museums.size());
    }

    @Test
    @ApiLogin
    @DisplayName("Создание нового музея")
    void museumShouldBeCreated(@Token String token) {
        CountryJson country = countryDbClient.findAll().getFirst();
        MuseumJson museum = MuseumJson.randomMuseum().addCountry(country);

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museum);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(museum.title(), response.body().title());
    }

    @Test
    @DisplayName("Создание музея без токена возвращает 401")
    void museumShouldNotBeCreatedWithoutToken() {
        CountryJson country = countryDbClient.findAll().getFirst();
        MuseumJson museum = MuseumJson.randomMuseum().addCountry(country);

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(null, museum);

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Требуется авторизация. Пожалуйста, выполните вход.",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Создание музея с ID должно завершиться ошибкой")
    void museumShouldNotBeCreatedWithNonEmptyId(@Token String token, TestData testData) {
        MuseumJson museumJson = new MuseumJson(
                UUID.randomUUID(),
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomSentence(20),
                testData.museums().getFirst().geo(),
                null
        );


        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals(400, response.code());
        Assertions.assertEquals("ID не должен быть задан при создании музея", errorMessage);
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Создание дубликата музея должно завершиться ошибкой")
    void shouldNotCreateMuseumWithDuplicateTitle(@Token String token, TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        MuseumJson museumJson = new MuseumJson(
                null,
                createdMuseum.title(),
                RandomDataUtils.randomSentence(20),
                createdMuseum.geo(),
                null
        );

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertEquals(409, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals("Музей с таким названием уже существует", ErrorMessageResolver.getErrorMessage(response.errorBody()));
    }

    @Test
    @ApiLogin
    @DisplayName("Создание музея с пустым названием (пробелы) возвращает ошибку")
    void museumShouldNotBeCreatedWithBlankTitle(@Token String token) {
        MuseumJson museumJson = new MuseumJson(
                null,
                "   ",
                RandomDataUtils.randomSentence(20),
                new GeoJson("Минск", new CountryJson(null, CountryEnum.ALBANIA)),
                null
        );

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Название не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание музея с пустым названием возвращает ошибку")
    void museumShouldNotBeCreatedWithEmptyTitle(@Token String token) {
        MuseumJson museumJson = new MuseumJson(
                null,
                "",
                RandomDataUtils.randomSentence(20),
                new GeoJson("Минск", new CountryJson(null, CountryEnum.ALBANIA)),
                null
        );

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Название не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание музея с названием >255 символов возвращает ошибку")
    void museumShouldNotBeCreatedWithLongTitle(@Token String token) {
        MuseumJson museumJson = new MuseumJson(
                null,
                RandomDataUtils.randomWord(256),
                RandomDataUtils.randomSentence(20),
                new GeoJson("Минск", new CountryJson(null, CountryEnum.ALBANIA)),
                null
        );

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Название не может быть длиннее 255 символов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание музея с описанием >1000 символов возвращает ошибку")
    void museumShouldNotBeCreatedWithLongDescription(@Token String token) {
        MuseumJson museumJson = new MuseumJson(
                null,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomWord(1001),
                new GeoJson("Минск", new CountryJson(null, CountryEnum.ALBANIA)),
                null
        );

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Описание не может быть длиннее 1000 символов", errorMessage);
    }

    @Test
    @ApiLogin
    @DisplayName("Создание музея с фото >1MB возвращает ошибку")
    void museumShouldNotBeCreatedWithLargeImage(@Token String token) {
        String image = imageToBase64("img/1_1mb_photo.png");
        MuseumJson museumJson = new MuseumJson(
                null,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomSentence(20),
                new GeoJson("Минск", new CountryJson(null, CountryEnum.ALBANIA)),
                image
        );

        final Response<MuseumJson> response = museumApiClient
                .createMuseum(token, museumJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Размер фото не может превышать 1 MB", errorMessage);
    }



    @Test
    @ApiLogin
    @Museum
    @DisplayName("Обновление данных музея")
    void museumShouldBeUpdated(@Token String token, TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        MuseumJson expectedMuseum = new MuseumJson(
                createdMuseum.id(),
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomSentence(20),
                createdMuseum.geo(),
                ""
        );

                MuseumJson.randomMuseum().addId(createdMuseum.id());
        final Response<MuseumJson> response = museumApiClient
                .updateMuseum(token, expectedMuseum);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(expectedMuseum, response.body());
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Обновление несуществующего музея возвращает 404")
    void museumShouldNotBeUpdatedWithRandomId(@Token String token, TestData testData) {
        UUID randomId = UUID.randomUUID();
        MuseumJson createdMuseum = testData.museums().getFirst();
        MuseumJson museumJson = new MuseumJson(
                randomId,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomSentence(20),
                createdMuseum.geo(),
                ""
        );

        final Response<MuseumJson> response = museumApiClient
                .updateMuseum(token, museumJson);

        Assertions.assertEquals(404, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                String.format("Музей с ID %s не найден", randomId),
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @Museums(count = 2)
    @DisplayName("Обновление с дублирующим названием музея возвращает 409")
    void shouldNotUpdatedMuseumWithDuplicateTitle(@Token String token, TestData testData) {
        MuseumJson firstMuseum = testData.museums().getFirst();
        MuseumJson secondMuseum = testData.museums().getLast();


        final Response<MuseumJson> response = museumApiClient
                .updateMuseum(token, secondMuseum.addId(firstMuseum.id()));

        Assertions.assertEquals(409, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals("Музей с таким названием уже существует", ErrorMessageResolver.getErrorMessage(response.errorBody()));
    }

    @Test
    @Museum
    @DisplayName("Обновление музея без токена возвращает 401")
    void museumShouldNotBeUpdatedWithoutToken(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();

        final Response<MuseumJson> response = museumApiClient
                .updateMuseum(null, createdMuseum);

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Требуется авторизация. Пожалуйста, выполните вход.",
                getErrorMessage(response.errorBody())
        );
    }
}
