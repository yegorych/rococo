package guru.qa.rococo.test.web.painting;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.container.Paintings;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.page.component.modal.PaintingModal;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
public class PaintingPageTest {

    @Test
    void paintingSearchPlaceholderShouldBeDisplayed() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .search().checkPlaceholder("Искать картины...");
    }

    @Test
    @ApiLogin
    void addPaintingButtonShouldBeDisplayedForAuthorizedUser() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkAddPaintingBtnIsDisplayed();
    }

    @Test
    void addPaintingButtonShouldNotBeDisplayedForUnauthorizedUser() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkAddPaintingBtnIsNotDisplayed();
    }

    @Test
    @ApiLogin
    void addPaintingModalShouldBeClosedByClickingOnTheCloseButton() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .closeModal(new PaintingPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    void addPaintingModalShouldHaveEmptyFields() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .checkAddPaintingModal();
    }

    @Test
    @ApiLogin
    void addPaintingModalShouldBeClosedByClickingOnEmptyArea() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .clickOnEmptyArea(new PaintingPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @Paintings(count = 20)
    void paginationShouldWorkWhenScrolling(TestData testData) {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .scrollPaintingCard(testData.paintings().size())
                .checkNumberOfPaintingsIsGreaterThanOrEqual(testData.paintings().size());
    }

    @Test
    @Painting(title = "Ggggrrrr one")
    @Painting(title = "Ggggrrrr two")
    void searchResultsShouldContainOnlyPaintingsWithTitle() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .findPainting("Ggggrrrr")
                .checkNumberOfPaintingsEqual(2);
    }

    @Test
    void messageAboutEmptySearchResultShouldBeDisplayed() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .findPainting(RandomDataUtils.randomWord(5))
                .checkMessageAboutEmptyResultShouldBeDisplayed();
    }


    @ScreenShotTest(expected = "expected-painting-photo.png", rewriteExpected = true)
    @Painting(photo = "img/painting.png")
    void paintingCardShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        PaintingJson paintingJson = testData.paintings().getFirst();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingCardWithPhoto(paintingJson, expectedImage);
    }

    @ScreenShotTest(expected = "expected-painting-without-photo.png", rewriteExpected = true)
    @Painting(title = "Without photo")
    void paintingCardWithoutPhotoShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        String title = testData.paintings().getFirst().title();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingPhotoWithName(title, expectedImage);
    }

    @Test
    @Painting
    void paintingDetailsPageShouldOpen(TestData testData) {
        String title = testData.paintings().getFirst().title();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .findPainting(title)
                .selectPaintingByTitle(title)
                .checkThatPageLoaded();
    }

    @Test
    @ApiLogin
    @Artist
    @Museum
    void paintingShouldBeCreated(TestData testData) {
        ArtistJson artist = testData.artists().getFirst();
        MuseumJson museum = testData.museums().getFirst();
        PaintingJson newPainting = PaintingJson.randomPainting()
                .addArtist(artist)
                .addMuseum(museum);

        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .setTitle(newPainting.title())
                .setDescription(newPainting.description())
                .selectArtist(newPainting.artist().name())
                .selectMuseum(newPainting.museum().title())
                .uploadPhoto("img/painting.png")
                .submit(new PaintingPage())
                .checkSnackbarText("Добавлена картина: " + newPainting.title())
                .findPainting(newPainting.title())
                .checkNumberOfPaintingsEqual(1)
                .checkPaintingCard(newPainting);
    }

    @Test
    @ApiLogin
    @Artist
    void paintingShouldBeCreatedWithoutMuseum(TestData testData) {
        PaintingJson newPainting = PaintingJson.randomPainting();

        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .setTitle(newPainting.title())
                .setDescription(newPainting.description())
                .selectAnyArtist()
                .uploadPhoto("img/painting.png")
                .submit(new PaintingPage())
                .checkSnackbarText("Добавлена картина: " + newPainting.title());
    }

    @Test
    @ApiLogin
    void titleLengthShouldBeUnder255() {
        PaintingJson newPainting = PaintingJson.randomPainting();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .setTitle(randomWord(256))
                .setDescription(newPainting.description())
                .selectAnyArtist()
                .uploadPhoto("img/painting.png")
                .submit(new PaintingModal())
                .checkValidationError("Название не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    void titleLengthShouldBeOver3() {
        PaintingJson newPainting = PaintingJson.randomPainting();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .setTitle(randomWord(2))
                .setDescription(newPainting.description())
                .selectAnyArtist()
                .uploadPhoto("img/painting.png")
                .submit(new PaintingModal())
                .checkValidationError("Название не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    void descriptionLengthShouldBeUnder2000() {
        PaintingJson newPainting = PaintingJson.randomPainting();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .setTitle(newPainting.title())
                .setDescription(randomWord(2001))
                .selectAnyArtist()
                .uploadPhoto("img/painting.png")
                .submit(new PaintingModal())
                .checkValidationError("Описание не может быть длиннее 2000 символов");
    }

    @Test
    @ApiLogin
    void descriptionLengthShouldBeOver10() {
        PaintingJson newPainting = PaintingJson.randomPainting();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .setTitle(newPainting.title())
                .setDescription(randomWord(9))
                .selectAnyArtist()
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Описание не может быть короче 10 символов");
    }

//    @Test
//    @ApiLogin
//    void paintingPhotoSizeShouldBeLessThan4Mb() {
//        PaintingJson newPainting = PaintingJson.randomPainting();
//        Selenide.open(PaintingPage.URL, PaintingPage.class)
//                .clickAddPaintingBtn()
//                .setTitle(newPainting.title())
//                .setDescription(newPainting.description())
//                .selectAnyArtist()
//                .uploadPhoto("img/5mb-photo.png")
//                .submit(new PaintingPage())
//                .checkSnackbarText("Размер фото не может превышать 4 MB");
//    }














}
