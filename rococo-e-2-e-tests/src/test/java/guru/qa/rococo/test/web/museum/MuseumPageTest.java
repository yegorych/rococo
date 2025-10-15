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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("web: тесты страницы с списком музеев")
public class MuseumPageTest {

    @Test
    @ApiLogin
    @DisplayName("Кнопка добавления музея отображается для авторизованного пользователя")
    void addMuseumButtonShouldBeDisplayedForAuthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkAddMuseumBtnIsDisplayed();
    }

    @Test
    @DisplayName("Кнопка добавления музея не отображается для неавторизованного пользователя")
    void addMuseumButtonShouldNotBeDisplayedForUnauthorizedUser() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkAddMuseumBtnIsNotDisplayed();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления музея закрывается по кнопке закрытия")
    void addMuseumModalShouldBeClosedByClickingOnTheCloseButton() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .closeModal(new MuseumPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления музея содержит пустые поля")
    void addMuseumModalShouldHaveEmptyFields() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .checkAddMuseumModal();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления музея закрывается по клику вне области")
    void addMuseumModalShouldBeClosedByClickingOnEmptyArea() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .clickAddMuseumBtn()
                .clickOnEmptyArea(new MuseumPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @Museums(count = 20)
    @DisplayName("Пагинация работает при прокрутке списка музеев")
    void paginationShouldWorkWhenScrolling(TestData testData) {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .scrollMuseumCard(testData.museums().size())
                .checkNumberOfMuseumsIsGreaterThanOrEqual(testData.museums().size());
    }

    @Test
    @Museum(title = "jojoooo one")
    @Museum(title = "jojoooo two")
    @DisplayName("Результаты поиска содержат только музеи с заданным названием")
    void searchResultsShouldContainOnlyMuseumsWithTitle() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .findMuseum("jojoooo")
                .checkNumberOfMuseumsEqual(2);
    }

    @Test
    @DisplayName("Отображается сообщение об отсутствии результатов поиска")
    void messageAboutEmptySearchResultShouldBeDisplayed() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .findMuseum(RandomDataUtils.randomWord(5))
                .checkMessageAboutEmptyResultShouldBeDisplayed();
    }

    @Test
    @DisplayName("Плейсхолдер поиска музеев отображается")
    void museumSearchPlaceholderShouldBeDisplayed() {
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .search().checkPlaceholder("Искать музей...");
    }


    @ScreenShotTest(expected = "expected-museum-photo.png", rewriteExpected = true)
    @Museum(photo = "img/museumPhoto.png")
    @DisplayName("Карточка музея с фото отображается корректно")
    void museumCardShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        MuseumJson museumJson = testData.museums().getFirst();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumCardWithPhoto(museumJson, expectedImage);
    }

    @ScreenShotTest(expected = "expected-museum-without-photo.png", rewriteExpected = true)
    @Museum(title = "Without photo")
    @DisplayName("Карточка музея без фото отображается корректно")
    void museumCardWithoutPhotoShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        String title = testData.museums().getFirst().title();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .checkMuseumPhotoWithTitle(title, expectedImage);
    }

    @Test
    @Museum
    @DisplayName("Переход на страницу музея работает")
    void museumDetailsPageShouldOpen(TestData testData) {
        String title = testData.museums().getFirst().title();
        Selenide.open(MuseumPage.URL, MuseumPage.class)
                .findMuseum(title)
                .selectMuseumByTitle(title)
                .checkThatPageLoaded();
    }

    @Test
    @ApiLogin
    @DisplayName("Создание нового музея")
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
    @DisplayName("При создании музея Название музея не должно превышать 255 символов")
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
    @DisplayName("При создании музея Название должно быть не короче 3 символов")
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
    @DisplayName("При создании музея Город не должен превышать 255 символов")
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
    @DisplayName("При создании музея Город должен быть не короче 3 символов")
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
    @DisplayName("При создании музея описание не должно превышать 2000 символов")
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
    @DisplayName("При создании музея описание должно быть не короче 10 символов")
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
