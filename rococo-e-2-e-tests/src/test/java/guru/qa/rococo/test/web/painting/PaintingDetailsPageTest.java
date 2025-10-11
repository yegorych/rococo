package guru.qa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.page.component.modal.PaintingModal;
import guru.qa.rococo.page.detailsPage.PaintingDetailsPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.*;

@WebTest
public class PaintingDetailsPageTest {

    @ScreenShotTest(expected = "expected-painting-details-photo.png", rewriteExpected = true)
    @Painting(photo = "img/painting.png")
    void paintingDetailsShouldBeDisplayed(TestData testData, BufferedImage expectedImage) {
        PaintingJson painting = testData.paintings().getFirst();
        String id = painting.id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .checkPaintingDetails(painting)
                .checkPaintingPhoto(expectedImage);
    }

    @Test
    @ApiLogin
    @Painting
    void editPaintingButtonShouldBeDisplayedForAuthorizedUser(TestData testData) {
        String id = testData.paintings().getFirst().id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .checkEditPaintingIsVisible();
    }

    @Test
    @Painting
    void editPaintingButtonShouldNotBeDisplayedForUnauthorizedUser(TestData testData) {
        String id = testData.paintings().getFirst().id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .checkEditPaintingIsNotVisible();
    }

    @Test
    @Painting
    @ApiLogin
    void editPaintingModalShouldBeOpened(TestData testData) {
        String id = testData.paintings().getFirst().id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .checkModalHasOpened();
    }

    @Test
    @ApiLogin
    @Painting
    void editPaintingModalShouldBeClosedByClickingOnTheCloseButton(TestData testData) {
        PaintingJson paintingJson = testData.paintings().getFirst();
        String id = paintingJson.id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .closeModal(new PaintingPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @Painting
    void editPaintingModalShouldBeClosedByClickingOnEmptyArea(TestData testData) {
        PaintingJson paintingJson = testData.paintings().getFirst();
        String id = paintingJson.id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .clickOnEmptyArea(new PaintingPage())
                .checkPageNotHaveModalWindow();
    }

    @ScreenShotTest(expected = "expected-edit-painting-modal-photo.png", rewriteExpected = true)
    @ApiLogin
    @Painting(photo = "img/painting.png")
    void editPaintingModalShouldContainPaintingData(PaintingJson[] paintings, BufferedImage bufferedImage) {
        PaintingJson paintingJson = paintings[0];
        String id = paintingJson.id().toString();
        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .checkEditPaintingModalWithPhoto(paintingJson, bufferedImage);
    }

    @Test
    @ApiLogin
    @Painting
    @Artist
    @Museum
    void paintingShouldBeUpdated(TestData testData) {
        PaintingJson createdPainting = testData.paintings().getFirst();
        String id = createdPainting.id().toString();
        ArtistJson createdArtist = testData.artists().getFirst();
        MuseumJson createdMuseum = testData.museums().getFirst();
        PaintingJson newPainting = PaintingJson.randomPainting().addArtist(createdArtist).addMuseum(createdMuseum);

        Selenide.open(PaintingDetailsPage.URL(id), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .setTitle(newPainting.title())
                .setDescription(newPainting.description())
                .selectArtist(createdArtist.name())
                .selectMuseum(createdMuseum.title())
                .uploadPhoto("img/painting.png")
                .submit(new PaintingDetailsPage())
                .checkSnackbarText("Обновлена картина: " + newPainting.title())
                .checkPaintingDetails(newPainting);
    }


    @Test
    @ApiLogin
    @Painting
    void editPaintingModalTitleLengthShouldBeUnder255(PaintingJson[] paintings) {
        PaintingJson painting = paintings[0];

        Selenide.open(PaintingDetailsPage.URL(painting.id().toString()), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .setTitle(randomWord(256))
                .selectAnyArtist()
                .submit(new PaintingModal())
                .checkValidationError("Название не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    @Painting
    void editPaintingModalTitleLengthShouldBeOver3(TestData testData) {
        PaintingJson painting = testData.paintings().getFirst();

        Selenide.open(PaintingDetailsPage.URL(painting.id().toString()), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .setTitle(randomWord(2))
                .selectAnyArtist()
                .submit(new PaintingModal())
                .checkValidationError("Название не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    @Painting
    void editPaintingModalDescriptionLengthShouldBeUnder2000(TestData testData) {
        PaintingJson painting = testData.paintings().getFirst();
        Selenide.open(PaintingDetailsPage.URL(painting.id().toString()), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .setDescription(randomWord(2001))
                .selectAnyArtist()
                .submit(new PaintingModal())
                .checkValidationError("Описание не может быть длиннее 2000 символов");
    }

    @Test
    @ApiLogin
    @Painting
    void editPaintingModalDescriptionLengthShouldBeOver10(TestData testData) {
        PaintingJson painting = testData.paintings().getFirst();
        Selenide.open(PaintingDetailsPage.URL(painting.id().toString()), PaintingDetailsPage.class)
                .clickOnEditBtn()
                .setDescription(randomWord(9))
                .selectAnyArtist()
                .submit(new PaintingModal())
                .checkValidationError("Описание не может быть короче 10 символов");
    }
//
//    @Test
//    @ApiLogin
//    @Painting
//    @Museum
//    void editPaintingModalPhotoSizeShouldBeLessThan4Mb(TestData testData) {
//        PaintingJson painting = testData.paintings().getFirst();
//
//        Selenide.open(PaintingDetailsPage.URL(painting.id().toString()), PaintingDetailsPage.class)
//                .clickOnEditBtn()
//                .selectAnyArtist()
//                .uploadPhoto("img/5mb-photo.png")
//                .submit(new PaintingDetailsPage())
//                .checkSnackbarText("Размер фото не может превышать 4 MB");
//    }






}
