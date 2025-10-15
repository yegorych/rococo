package guru.qa.rococo.test.web.artist;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.container.Artists;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.component.modal.ArtistModal;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("web: тесты страницы с списком художников")
public class ArtistPageTest {

    @Test
    @DisplayName("Плейсхолдер поиска художников отображается")
    void artistSearchPlaceholderShouldBeDisplayed() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .search().checkPlaceholder("Искать художников...");
    }

    @Test
    @ApiLogin
    @DisplayName("Кнопка добавления художника отображается для авторизованного пользователя")
    void addArtistButtonShouldBeDisplayedForAuthorizedUser() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkAddArtistBtnIsDisplayed();
    }

    @Test
    @DisplayName("Кнопка добавления художника не отображается для неавторизованного пользователя")
    void addArtistButtonShouldNotBeDisplayedForUnauthorizedUser() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkAddArtistBtnIsNotDisplayed();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления художника закрывается по кнопке закрытия")
    void addArtistModalShouldBeClosedByClickingOnTheCloseButton() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .closeModal(new ArtistPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления художника содержит пустые поля")
    void addArtistModalShouldHaveEmptyFields() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .checkAddArtistModal();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления художника закрывается по клику вне области")
    void addArtistModalShouldBeClosedByClickingOnEmptyArea() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .clickOnEmptyArea(new ArtistPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @Artists(count = 20)
    @DisplayName("Пагинация работает при прокрутке списка художников")
    void paginationShouldWorkWhenScrolling(TestData testData) {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .scrollArtistCard(testData.artists().size())
                .checkNumberOfArtistsIsGreaterThanOrEqual(testData.artists().size());
    }

    @Test
    @Artist(name = "Ggggrrrr one")
    @Artist(name = "Ggggrrrr two")
    @DisplayName("Результаты поиска содержат только художников с заданным именем")
    void searchResultsShouldContainOnlyArtistsWithName() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .findArtist("Ggggrrrr")
                .checkNumberOfArtistsEqual(2);
    }

    @Test
    @DisplayName("Отображается сообщение об отсутствии результатов поиска")
    void messageAboutEmptySearchResultShouldBeDisplayed() {
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .findArtist(RandomDataUtils.randomWord(5))
                .checkMessageAboutEmptyResultShouldBeDisplayed();
    }


    @ScreenShotTest(expected = "expected-artist-photo.png", rewriteExpected = true)
    @Artist(photo = "img/avatar.png")
    @DisplayName("Карточка художника с фото отображается корректно")
    void artistCardShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        ArtistJson artistJson = testData.artists().getFirst();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkArtistCardWithPhoto(artistJson, expectedImage);
    }

    @ScreenShotTest(expected = "expected-artist-without-photo.png", rewriteExpected = true)
    @Artist
    @DisplayName("Карточка художника без фото отображается корректно")
    void artistCardWithoutPhotoShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        String name = testData.artists().getFirst().name();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .checkArtistPhotoWithName(name, expectedImage);
    }

    @Test
    @Artist
    @DisplayName("Переход на страницу художника работает")
    void artistDetailsPageShouldOpen(TestData testData) {
        String name = testData.artists().getFirst().name();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .findArtist(name)
                .selectArtistByName(name)
                .checkThatPageLoaded();
    }

    @Test
    @ApiLogin
    @DisplayName("Создание нового художника")
    void artistShouldBeCreated() {
        ArtistJson newArtist = ArtistJson.randomArtist();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .setName(newArtist.name())
                .setBiography(newArtist.biography())
                .uploadPhoto("img/artist.png")
                .submit(new ArtistPage())
                .checkSnackbarText("Добавлен художник: " + newArtist.name())
                .findArtist(newArtist.name())
                .checkNumberOfArtistsEqual(1)
                .checkArtistCard(newArtist);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя художника не должно превышать 255 символов")
    void nameLengthShouldBeUnder255() {
        ArtistJson newArtist = ArtistJson.randomArtist();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .setName(randomWord(256))
                .setBiography(newArtist.biography())
                .uploadPhoto("img/artist.png")
                .submit(new ArtistModal())
                .checkValidationError("Имя не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    @DisplayName("Имя художника должно быть не короче 3 символов")
    void nameLengthShouldBeOver3() {
        ArtistJson newArtist = ArtistJson.randomArtist();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .setName(randomWord(2))
                .setBiography(newArtist.biography())
                .uploadPhoto("img/artist.png")
                .submit(new ArtistModal())
                .checkValidationError("Имя не может быть короче 3 символов");
    }

    @Test
    @ApiLogin
    @DisplayName("Биография художника не должна превышать 2000 символов")
    void biographyLengthShouldBeUnder2000() {
        ArtistJson newArtist = ArtistJson.randomArtist();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .setName(newArtist.name())
                .setBiography(randomWord(2001))
                .uploadPhoto("img/artist.png")
                .submit(new ArtistModal())
                .checkValidationError("Биография не может быть длиннее 2000 символов");
    }

    @Test
    @ApiLogin
    @DisplayName("Биография художника должна быть не короче 10 символов")
    void biographyLengthShouldBeOver10() {
        ArtistJson newArtist = ArtistJson.randomArtist();
        Selenide.open(ArtistPage.URL, ArtistPage.class)
                .clickAddArtistBtn()
                .setName(newArtist.name())
                .setBiography(randomWord(9))
                .uploadPhoto("img/museumPhoto.png")
                .submit(new MuseumModal())
                .checkValidationError("Биография не может быть короче 10 символов");
    }
}
