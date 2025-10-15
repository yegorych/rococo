package guru.qa.rococo.test.rest.painting;

import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.container.Paintings;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.service.impl.api.PaintingApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static guru.qa.rococo.utils.ErrorMessageResolver.getErrorMessage;
import static guru.qa.rococo.utils.ImgBase64Utils.imageToBase64;
import static guru.qa.rococo.utils.RandomDataUtils.*;

@RestTest
public class PaintingRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final PaintingApiClient paintingApiClient = new PaintingApiClient();


    @Test
    @Artist(paintings = 3)
    void paintingsByAuthorShouldBeReturned(TestData testData) {
        UUID artistId = testData.artists().getFirst().id();
        final Response<RestResponsePage<PaintingJson>>response = paintingApiClient
                .getPaintingsByAuthorId(artistId.toString(), 0, 10);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(3, response.body().getContent().size());
    }

    @Test
    void paintingShouldNotBeReturnedByRandomId() {
        String randomId = UUID.randomUUID().toString();
        final Response<PaintingJson> response = paintingApiClient
                .getPainting(randomId);

        Assertions.assertEquals(404, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                String.format("Картина с ID %s не найдена", randomId),
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @Painting(title = "Painting dasd 1")
    @Painting(title = "Painting dasd 2")
    @Painting(title = "Painting dasd 3")
    void shouldReturnFilteredPaintingsPageByTitle() {
        final Response<RestResponsePage<PaintingJson>> response = paintingApiClient
                .getPaintingPage("Painting dasd", 0, 10);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(3, response.body().getContent().size());
    }

    @Test
    @Paintings(count = 9)
    void paintingsShouldBePaginated() {
        final int size = 3;
        Set<PaintingJson> paintings = new HashSet<>();
        int page = 0;
        while (page!= 3) {
            Response<RestResponsePage<PaintingJson>> response = paintingApiClient.getPaintingPage(null, page, size);
            Assertions.assertTrue(response.isSuccessful());
            Assertions.assertNotNull(response.body());
            paintings.addAll(response.body().getContent());
            page++;
        }
        Assertions.assertEquals(9, paintings.size());
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithNonEmptyId(@Token String token, TestData testData) {
        PaintingJson paintingJson = PaintingJson.randomPainting().addId(UUID.randomUUID());
        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals(400, response.code());
        Assertions.assertEquals("ID не должен быть задан при создании картины", errorMessage);
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    void paintingShouldBeCreated(@Token String token, TestData testData) {
        ArtistJson artist = testData.artists().getFirst();
        MuseumJson museum = testData.museums().getFirst();
        PaintingJson paintingJson = new PaintingJson(
                null,
                randomName(),
                randomSentence(20),
                new MuseumJson(museum.id()),
                new ArtistJson(artist.id()),
                ""
        );
        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(paintingJson.addId(response.body().id()), response.body());
    }

    @Test
    @ApiLogin
    @Artist
    void paintingShouldBeCreatedWithoutMuseum(@Token String token, TestData testData) {
        ArtistJson artist = testData.artists().getFirst();
        PaintingJson paintingJson = new PaintingJson(
                null,
                randomName(),
                randomSentence(20),
                MuseumJson.emptyMuseum(),
                artist,
                ""
        );
        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
    }

    @Test
    void paintingShouldNotBeCreatedWithoutToken() {
        final Response<PaintingJson> response = paintingApiClient
                .createPainting(null, PaintingJson.randomPainting());

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Требуется авторизация. Пожалуйста, выполните вход.",
                getErrorMessage(response.errorBody())
        );
    }



    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithBlankTitle(@Token String token) {
        PaintingJson paintingJson = new PaintingJson(
                null,
                "   ",
                randomSentence(20),
                MuseumJson.randomMuseum(),
                ArtistJson.randomArtist(),
                null
        );
        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Название не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithEmptyTitle(@Token String token) {
        PaintingJson paintingJson = new PaintingJson(
                null,
                "",
                randomSentence(20),
                MuseumJson.randomMuseum(),
                ArtistJson.randomArtist(),
                null
        );

        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Название не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithLongTitle(@Token String token) {
        PaintingJson paintingJson = new PaintingJson(
                null,
                randomWord(256),
                randomSentence(20),
                MuseumJson.randomMuseum(),
                ArtistJson.randomArtist(),
                null
        );
        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Название не может быть длиннее 255 символов", errorMessage);
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithLongDescription(@Token String token) {
        PaintingJson paintingJson = new PaintingJson(
                null,
                randomName(),
                randomWord(1001),
                MuseumJson.randomMuseum(),
                ArtistJson.randomArtist(),
                null
        );

        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Описание не может быть длиннее 1000 символов", errorMessage);
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithLargeImage(@Token String token) {
        String image = imageToBase64("img/1_1mb_photo.png");
        PaintingJson paintingJson = new PaintingJson(
                null,
                randomName(),
                randomSentence(20),
                MuseumJson.randomMuseum(),
                ArtistJson.randomArtist(),
                image
        );

        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Размер фото не может превышать 1 MB", errorMessage);
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeCreatedWithoutArtist(@Token String token) {
        PaintingJson paintingJson = new PaintingJson(
                null,
                randomName(),
                randomSentence(20),
                MuseumJson.randomMuseum(),
                null,
                ""
        );

        final Response<PaintingJson> response = paintingApiClient
                .createPainting(token, paintingJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Художник должен быть задан", errorMessage);
    }

    @Test
    @ApiLogin
    @Painting(museum = @Museum)
    void paintingShouldBeUpdated(@Token String token, TestData testData) {
        PaintingJson createdPainting = testData.paintings().getFirst();
        PaintingJson expectedPainting = new PaintingJson(
                createdPainting.id(),
                randomName(),
                randomSentence(20),
                new MuseumJson(createdPainting.id()),
                createdPainting.artist(),
                ""
        );

        final Response<PaintingJson> response = paintingApiClient
                .updatePainting(token, expectedPainting);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(expectedPainting, response.body());
    }

    @Test
    @ApiLogin
    void paintingShouldNotBeUpdatedWithRandomId(@Token String token) {
        UUID randomId = UUID.randomUUID();
        PaintingJson paintingJson = PaintingJson.randomPainting().addId(randomId);

        final Response<PaintingJson> response = paintingApiClient
                .updatePainting(token, paintingJson);

        Assertions.assertEquals(404, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                String.format("Картина с ID %s не найдена", randomId),
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    void paintingShouldNotBeUpdatedWithoutToken() {
        final Response<PaintingJson> response = paintingApiClient
                .updatePainting(null, PaintingJson.randomPainting());

        Assertions.assertEquals(401, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Требуется авторизация. Пожалуйста, выполните вход.",
                getErrorMessage(response.errorBody())
        );
    }
}
