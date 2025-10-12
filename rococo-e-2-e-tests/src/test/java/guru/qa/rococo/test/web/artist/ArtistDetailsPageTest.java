package guru.qa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.DisabledByIssue;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.page.component.modal.ArtistModal;
import guru.qa.rococo.page.detailsPage.ArtistDetailsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.*;

@WebTest
public class ArtistDetailsPageTest {

    @ScreenShotTest(expected = "expected-artist-details-photo.png", rewriteExpected = true)
    @Artist(photo = "img/artist.png", paintings = 3)
    void artistDetailsShouldBeDisplayed(TestData testData, BufferedImage expectedImage) {
        ArtistJson artist = testData.artists().getFirst();
        String id = artist.id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkArtistDetails(artist)
                .checkArtistPhoto(expectedImage);
    }

    @Test
    @Artist
    void artistDetailWithoutPaintingsShouldBeDisplayed(TestData testData) {
        ArtistJson artist = testData.artists().getFirst();
        String id = artist.id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkArtistDetails(artist);
    }

    @Test
    @Artist
    @ApiLogin
    void artistDetailWithoutPaintingsShouldBeHavingSecondAddPaintingButtonForAuthorizedUser(TestData testData) {
        ArtistJson artist = testData.artists().getFirst();
        String id = artist.id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkSecondAddPaintingIsVisible();
    }

    @Test
    @Artist
    @ApiLogin
    void artistDetailWithPaintingsShouldBeNotHavingSecondAddPaintingButtonForAuthorizedUser(TestData testData) {
        ArtistJson artist = testData.artists().getFirst();
        String id = artist.id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkSecondAddPaintingIsNotVisible();
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistButtonShouldBeDisplayedForAuthorizedUser(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkEditArtistIsVisible();
    }

    @Test
    @ApiLogin
    @Artist
    void addPaintingButtonShouldBeDisplayedForAuthorizedUser(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkAddPaintingIsVisible();
    }

    @Test
    @ApiLogin
    @Artist
    void addPaintingButtonShouldNotBeDisplayedForUnauthorizedUser(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkAddPaintingIsNotVisible();
    }

    @Test
    @Artist
    void editArtistButtonShouldNotBeDisplayedForUnauthorizedUser(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .checkEditArtistIsNotVisible();
    }

    @Test
    @Artist
    @ApiLogin
    void editArtistModalShouldBeOpened(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .checkModalHasOpened();
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalShouldBeClosedByClickingOnTheCloseButton(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .closeModal(new ArtistPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalShouldBeClosedByClickingOnEmptyArea(TestData testData) {
        String id = testData.artists().getFirst().id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .clickOnEmptyArea(new ArtistPage())
                .checkPageNotHaveModalWindow();
    }

    @ScreenShotTest(expected = "expected-edit-artist-modal-photo.png", rewriteExpected = true)
    @ApiLogin
    @Artist(photo = "img/artist.png")
    void editArtistModalShouldContainArtistData(TestData testData, BufferedImage bufferedImage) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();
        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .checkEditArtistModalWithPhoto(artistJson, bufferedImage);
    }

    @Test
    @ApiLogin
    @Artist
    void artistShouldBeEdited(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();
        ArtistJson newArtist = ArtistJson.randomArtist();

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setName(newArtist.name())
                .setBiography(newArtist.biography())
                .submit(new ArtistDetailsPage())
                .checkSnackbarText("Обновлен художник: " + newArtist.name())
                .checkArtistDetails(newArtist);
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalShouldAllowEditingNameOnly(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();
        String newName = randomName();

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setName(newName)
                .submit(new ArtistDetailsPage())
                .checkSnackbarText("Обновлен художник: " + newName);
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalShouldAllowEditingBiographyOnly(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();
        String newBiography = randomSentence(10);

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setBiography(newBiography)
                .submit(new ArtistDetailsPage())
                .checkSnackbarText("Обновлен художник: " + artistJson.name());
    }


    @Test
    @ApiLogin
    @Artist
    void editArtistModalNameLengthShouldBeUnder255(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setName(randomWord(256))
                .submit(new ArtistModal())
                .checkValidationError("Имя не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalNameLengthShouldBeOver3(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setName(randomWord(2))
                .submit(new ArtistModal())
                .checkValidationError("Имя не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalBiographyLengthShouldBeUnder2000(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setBiography(randomWord(2001))
                .submit(new ArtistModal())
                .checkValidationError("Биография не может быть длиннее 2000 символов");
    }

    @Test
    @ApiLogin
    @Artist
    void editArtistModalBiographyLengthShouldBeOver10(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickOnEditBtn()
                .setBiography(randomWord(9))
                .submit(new ArtistModal())
                .checkValidationError("Биография не может быть короче 10 символов");
    }



    @Test
    @ApiLogin
    @Artist
    //@DisabledByIssue("4")
    void paintingFromArtistDetailShouldBeCreated(TestData testData) {
        ArtistJson artistJson = testData.artists().getFirst();
        String id = artistJson.id().toString();
        PaintingJson newPainting = PaintingJson.randomPainting().addArtist(artistJson);

        Selenide.open(ArtistDetailsPage.URL(id), ArtistDetailsPage.class)
                .clickAddPaintingBtn()
                .setTitle(newPainting.title())
                .setDescription(newPainting.description())
                .uploadPhoto("img/painting.png")
                .submit(new ArtistDetailsPage())
                .checkSnackbarText("Добавлена картина: " + newPainting.title())
                .goToPage(PaintingPage.URL, PaintingPage.class)
                .findPainting(newPainting.title())
                .checkNumberOfPaintingsEqual(1)
                .checkPaintingCard(newPainting);
    }








}
