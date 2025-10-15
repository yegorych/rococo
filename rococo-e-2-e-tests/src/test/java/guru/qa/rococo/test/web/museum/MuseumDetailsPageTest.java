package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.page.detailsPage.MuseumDetailsPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.*;
import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
public class MuseumDetailsPageTest {

    @ScreenShotTest(expected = "expected-museum-details-photo.png", rewriteExpected = true)
    @Museum(photo = "img/avatar.png")
    void museumDetailsShouldBeDisplayed(TestData testData, BufferedImage expectedImage) {
        MuseumJson museum = testData.museums().getFirst();
        String id = museum.id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .checkMuseumDetails(museum)
                .checkMuseumPhoto(expectedImage);
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumButtonShouldBeDisplayedForAuthorizedUser(TestData testData) {
        String id = testData.museums().getFirst().id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .checkEditMuseumIsVisible();
    }

    @Test
    @Museum
    void editMuseumButtonShouldNotBeDisplayedForUnauthorizedUser(TestData testData) {
        String id = testData.museums().getFirst().id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .checkEditMuseumIsNotVisible();
    }

    @Test
    @Museum
    @ApiLogin
    void editMuseumModalShouldBeOpened(TestData testData) {
        String id = testData.museums().getFirst().id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .clickOnEditBtn()
                .checkModalHasOpened();
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalShouldBeClosedByClickingOnTheCloseButton(TestData testData) {
        MuseumJson museumJson = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museumJson.title())
                .clickOnEditBtn()
                .closeModal(new MuseumPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalShouldBeClosedByClickingOnEmptyArea(TestData testData) {
        MuseumJson museumJson = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museumJson.title())
                .clickOnEditBtn()
                .clickOnEmptyArea(new MuseumPage())
                .checkPageNotHaveModalWindow();
    }

    @ScreenShotTest(expected = "expected-edit-museum-modal-photo.png", rewriteExpected = true)
    @ApiLogin
    @Museum(photo = "img/museumPhoto.png")
    void editMuseumModalShouldContainMuseumData(TestData testData, BufferedImage bufferedImage) {
        MuseumJson museumJson = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museumJson.title())
                .clickOnEditBtn()
                .checkEditMuseumModalWithPhoto(museumJson, bufferedImage);
    }

    @Test
    @ApiLogin
    @Museum
    void museumShouldBeUpdated(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        MuseumJson newMuseum = MuseumJson.randomMuseum();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(createdMuseum.title())
                .clickOnEditBtn()
                .setTitle(newMuseum.title())
                .setCity(newMuseum.geo().city())
                .setDescription(newMuseum.description())
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .submit(new MuseumDetailsPage())
                .checkSnackbarText("Обновлен музей: " + newMuseum.title())
                .checkMuseumDetails(newMuseum);
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalShouldAllowEditingTitleOnly(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        String newTitle = RandomDataUtils.randomMuseumTitle();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(createdMuseum.title())
                .clickOnEditBtn()
                .setTitle(newTitle)
                .submit(new MuseumDetailsPage())
                .checkSnackbarText("Обновлен музей: " + newTitle);
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalShouldAllowEditingCountryOnly(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();
        String newCountry = CountryEnum.randomCountry().getCountryName();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(createdMuseum.title())
                .clickOnEditBtn()
                .selectCountry(newCountry)
                .submit(new MuseumDetailsPage())
                .checkSnackbarText("Обновлен музей: " + createdMuseum.title());
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalShouldAllowEditingCityOnly(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(createdMuseum.title())
                .clickOnEditBtn()
                .setCity(randomCity())
                .submit(new MuseumDetailsPage())
                .checkSnackbarText("Обновлен музей: " + createdMuseum.title());
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalShouldAllowEditingDescriptionOnly(TestData testData) {
        MuseumJson createdMuseum = testData.museums().getFirst();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(createdMuseum.title())
                .clickOnEditBtn()
                .setDescription(randomSentence(30))
                .submit(new MuseumDetailsPage())
                .checkSnackbarText("Обновлен музей: " + createdMuseum.title());
    }


    @Test
    @ApiLogin
    @Museum
    void editMuseumModalTitleLengthShouldBeUnder255(TestData testData) {
        MuseumJson museum = testData.museums().getFirst();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museum.title())
                .clickOnEditBtn()
                .setTitle(randomWord(256))
                .submit(new MuseumModal())
                .checkValidationError("Название не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalTitleLengthShouldBeOver3(TestData testData) {
        MuseumJson museum = testData.museums().getFirst();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museum.title())
                .clickOnEditBtn()
                .setTitle(randomWord(2))
                .submit(new MuseumModal())
                .checkValidationError("Название не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalCityLengthShouldBeUnder255(TestData testData) {
        MuseumJson museum = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museum.title())
                .clickOnEditBtn()
                .setCity(randomWord(256))
                .submit(new MuseumModal())
                .checkValidationError("Город не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalCityLengthShouldBeOver3(TestData testData) {
        MuseumJson museum = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museum.title())
                .clickOnEditBtn()
                .setCity(randomWord(2))
                .submit(new MuseumModal())
                .checkValidationError("Город не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalDescriptionLengthShouldBeUnder2000(TestData testData) {
        MuseumJson museum = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museum.title())
                .clickOnEditBtn()
                .setDescription(randomWord(2001))
                .submit(new MuseumModal())
                .checkValidationError("Описание не может быть длиннее 2000 символов");
    }

    @Test
    @ApiLogin
    @Museum
    void editMuseumModalDescriptionLengthShouldBeOver10(TestData testData) {
        MuseumJson museum = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .selectMuseumByTitle(museum.title())
                .clickOnEditBtn()
                .setDescription(randomWord(9))
                .submit(new MuseumModal())
                .checkValidationError("Описание не может быть короче 10 символов");
    }
}
