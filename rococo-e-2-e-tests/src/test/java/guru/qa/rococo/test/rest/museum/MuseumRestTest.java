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
import guru.qa.rococo.service.impl.api.GeoApiClient;
import guru.qa.rococo.service.impl.api.MuseumApiClient;
import guru.qa.rococo.service.impl.db.CountryDbClient;
import guru.qa.rococo.utils.ErrorMessageResolver;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static guru.qa.rococo.utils.ErrorMessageResolver.getErrorMessage;
import static guru.qa.rococo.utils.ImgBase64Utils.imageToBase64;

@RestTest
public class MuseumRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final MuseumApiClient museumApiClient = new MuseumApiClient();
    private final CountryDbClient countryDbClient = new CountryDbClient();


    @Test
    @Museum
    void museumShouldBeReturnedById(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        final Response<MuseumJson> response = museumApiClient
                .getMuseum(createdMuseum.id().toString());

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(createdMuseum, response.body());
    }

    @Test
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
    void shouldReturnFilteredMuseumsPageByTitle() {
        final Response<RestResponsePage<MuseumJson>> response = museumApiClient
                .getMuseumPage("Museum bnvkfg", 0, 10);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(3, response.body().getContent().size());
    }

    @Test
    @Museums(count = 15)
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
    void museumShouldNotBeCreatedWithLargeImage(@Token String token) throws IOException {
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
