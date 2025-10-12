package guru.qa.rococo.test.web.museum;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.container.Museums;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
public class MuseumPageTest {

    @Test
    @ApiLogin
    void addMuseumButtonShouldBeDisplayedForAuthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkAddMuseumBtnIsDisplayed();
    }

    @Test
    void addMuseumButtonShouldNotBeDisplayedForUnauthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkAddMuseumBtnIsNotDisplayed();
    }

    @Test
    @ApiLogin
    void addMuseumModalShouldBeClosedByClickingOnTheCloseButton() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .closeModal(new MuseumPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    void addMuseumModalShouldHaveEmptyFields() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .checkAddMuseumModal();
    }

    @Test
    @ApiLogin
    void addMuseumModalShouldBeClosedByClickingOnEmptyArea() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .clickOnEmptyArea(new MuseumPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @Museums(count = 20)
    void paginationShouldWorkWhenScrolling(TestData testData) {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .scrollMuseumCard(testData.museums().size())
                .checkNumberOfMuseumsIsGreaterThanOrEqual(testData.museums().size());
    }

    @Test
    @Museum(title = "jojoooo one")
    @Museum(title = "jojoooo two")
    void searchResultsShouldContainOnlyMuseumsWithTitle() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .findMuseum("jojoooo")
                .checkNumberOfMuseumsEqual(2);
    }

    @Test
    void messageAboutEmptySearchResultShouldBeDisplayed() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .findMuseum(RandomDataUtils.randomWord(5))
                .checkMessageAboutEmptyResultShouldBeDisplayed();
    }

    @Test
    void museumSearchPlaceholderShouldBeDisplayed() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .search().checkPlaceholder("Искать музей...");
    }


    @ScreenShotTest(expected = "expected-museum-photo.png", rewriteExpected = true)
    @Museum(photo = "img/museumPhoto.png")
    void museumCardShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        MuseumJson museumJson = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumCardWithPhoto(museumJson, expectedImage);
    }

    @ScreenShotTest(expected = "expected-museum-without-photo.png", rewriteExpected = true)
    @Museum(title = "Without photo")
    void museumCardWithoutPhotoShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        String title = testData.museums().getFirst().title();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumPhotoWithTitle(title, expectedImage);
    }

    @Test
    @Museum
    void museumDetailsPageShouldOpen(TestData testData) {
        String title = testData.museums().getFirst().title();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .findMuseum(title)
                .selectMuseumByTitle(title)
                .checkThatPageLoaded();
    }

    @Test
    @ApiLogin
    void museumShouldBeCreated() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(newMuseum.title())
                .setCity(newMuseum.geo().city())
                .setDescription(newMuseum.description())
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumPage())
                .checkSnackbarText("Добавлен музей: " + newMuseum.title())
                .findMuseum(newMuseum.title())
                .checkNumberOfMuseumsEqual(1)
                .checkMuseumCard(newMuseum);
    }

    @Test
    @ApiLogin
    void titleLengthShouldBeUnder255() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(randomWord(256))
                .setCity(newMuseum.geo().city())
                .setDescription(newMuseum.description())
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Название не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    void titleLengthShouldBeOver3() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(randomWord(2))
                .setCity(newMuseum.geo().city())
                .setDescription(newMuseum.description())
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Название не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    void cityLengthShouldBeUnder255() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();

        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(newMuseum.title())
                .setCity(randomWord(256))
                .setDescription(newMuseum.description())
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Город не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    void cityLengthShouldBeOver3() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(newMuseum.title())
                .setCity(randomWord(1))
                .setDescription(newMuseum.description())
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Город не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    void descriptionLengthShouldBeUnder2000() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(newMuseum.title())
                .setCity(newMuseum.geo().city())
                .setDescription(randomWord(2001))
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Описание не может быть длиннее 2000 символов");
    }

    @Test
    @ApiLogin
    void descriptionLengthShouldBeOver10() {
        MuseumJson newMuseum = MuseumJson.randomMuseum();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .setTitle(newMuseum.title())
                .setCity(newMuseum.geo().city())
                .setDescription(randomWord(9))
                .selectCountry(newMuseum.geo().country().name().getCountryName())
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Описание не может быть короче 10 символов");
    }

}
