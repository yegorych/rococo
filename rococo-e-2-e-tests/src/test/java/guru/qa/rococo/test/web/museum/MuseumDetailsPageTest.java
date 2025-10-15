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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.*;
import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("web: тесты страницы деталей музея")
public class MuseumDetailsPageTest {

    @ScreenShotTest(expected = "expected-museum-details-photo.png", rewriteExpected = true)
    @Museum(photo = "img/avatar.png")
    @DisplayName("Отображение страницы музея с фото")
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
    @DisplayName("Кнопка редактирования музея отображается для авторизованного пользователя")
    void editMuseumButtonShouldBeDisplayedForAuthorizedUser(TestData testData) {
        String id = testData.museums().getFirst().id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .checkEditMuseumIsVisible();
    }

    @Test
    @Museum
    @DisplayName("Кнопка редактирования музея не отображается для неавторизованного пользователя")
    void editMuseumButtonShouldNotBeDisplayedForUnauthorizedUser(TestData testData) {
        String id = testData.museums().getFirst().id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .checkEditMuseumIsNotVisible();
    }

    @Test
    @Museum
    @ApiLogin
    @DisplayName("Модальное окно редактирования музея открывается")
    void editMuseumModalShouldBeOpened(TestData testData) {
        String id = testData.museums().getFirst().id().toString();
        Selenide.open(MuseumDetailsPage.URL(id), MuseumDetailsPage.class)
                .clickOnEditBtn()
                .checkModalHasOpened();
    }

    @Test
    @ApiLogin
    @Museum
    @DisplayName("Модальное окно редактирования закрывается по кнопке закрытия")
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
    @DisplayName("Модальное окно редактирования закрывается по клику вне области")
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
    @DisplayName("Модальное окно редактирования содержит данные музея")
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
    @DisplayName("Редактирование всех полей музея")
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
    @DisplayName("Редактирование только названия музея")
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
    @DisplayName("Редактирование только страны музея")
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
    @DisplayName("Редактирование только города музея")
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
    @DisplayName("Редактирование только описания музея")
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
    @DisplayName("Название музея не должно превышать 255 символов")
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
    @DisplayName("Название музея должно быть не короче 3 символов")
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
    @DisplayName("Город музея не должен превышать 255 символов")
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
    @DisplayName("Город музея должен быть не короче 3 символов")
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
    @DisplayName("Описание музея не должно превышать 2000 символов")
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
    @DisplayName("Описание музея должно быть не короче 10 символов")
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
