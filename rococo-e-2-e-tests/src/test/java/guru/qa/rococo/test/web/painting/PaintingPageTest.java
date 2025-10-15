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
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.WebDriverRunner.getSelenideProxy;
import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("web: тесты страницы с списком картин")
public class PaintingPageTest {

    @Test
    @DisplayName("Плейсхолдер поиска картин отображается")
    void paintingSearchPlaceholderShouldBeDisplayed() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .search().checkPlaceholder("Искать картины...");
    }

    @Test
    @ApiLogin
    @DisplayName("Кнопка добавления картины отображается для авторизованного пользователя")
    void addPaintingButtonShouldBeDisplayedForAuthorizedUser() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkAddPaintingBtnIsDisplayed();
    }

    @Test
    @DisplayName("Кнопка добавления картины не отображается для неавторизованного пользователя")
    void addPaintingButtonShouldNotBeDisplayedForUnauthorizedUser() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkAddPaintingBtnIsNotDisplayed();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления картины закрывается по кнопке закрытия")
    void addPaintingModalShouldBeClosedByClickingOnTheCloseButton() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .closeModal(new PaintingPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления картины содержит пустые поля")
    void addPaintingModalShouldHaveEmptyFields() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .checkAddPaintingModal();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно добавления картины закрывается по клику вне области")
    void addPaintingModalShouldBeClosedByClickingOnEmptyArea() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .clickAddPaintingBtn()
                .clickOnEmptyArea(new PaintingPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @Paintings(count = 20)
    @DisplayName("Пагинация работает при прокрутке списка картин")
    void paginationShouldWorkWhenScrolling(TestData testData) {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .scrollPaintingCard(testData.paintings().size())
                .checkNumberOfPaintingsIsGreaterThanOrEqual(testData.paintings().size());
    }

    @Test
    @Painting(title = "Ggggrrrr one")
    @Painting(title = "Ggggrrrr two")
    @DisplayName("Результаты поиска содержат только картины с заданным названием")
    void searchResultsShouldContainOnlyPaintingsWithTitle() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .findPainting("Ggggrrrr")
                .checkNumberOfPaintingsEqual(2);
    }

    @Test
    @DisplayName("Отображается сообщение об отсутствии результатов поиска")
    void messageAboutEmptySearchResultShouldBeDisplayed() {
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .findPainting(RandomDataUtils.randomWord(5))
                .checkMessageAboutEmptyResultShouldBeDisplayed();
    }


    @ScreenShotTest(expected = "expected-painting-photo.png", rewriteExpected = true)
    @Painting(photo = "img/painting.png")
    @DisplayName("Карточка картины с фото отображается корректно")
    void paintingCardShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        PaintingJson paintingJson = testData.paintings().getFirst();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingCardWithPhoto(paintingJson, expectedImage);
    }

    @ScreenShotTest(expected = "expected-painting-without-photo.png", rewriteExpected = true)
    @Painting(title = "Without photo")
    @DisplayName("Карточка картины без фото отображается корректно")
    void paintingCardWithoutPhotoShouldBeDisplayedCorrectly(TestData testData, BufferedImage expectedImage) {
        String title = testData.paintings().getFirst().title();
        Selenide.open(PaintingPage.URL, PaintingPage.class)
                .checkPaintingPhotoWithName(title, expectedImage);
    }

    @Test
    @Painting
    @DisplayName("Переход на страницу картины работает")
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
    @DisplayName("Создание новой картины")
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
    @DisplayName("Создание картины без музея")
    void paintingShouldBeCreatedWithoutMuseum() {
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
    @DisplayName("Название картины не должно превышать 255 символов")
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
    @DisplayName("Название картины должно быть не короче 3 символов")
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
    @DisplayName("Описание картины не должно превышать 2000 символов")
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
    @DisplayName("Описание картины должно быть не короче 10 символов")
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

    @Test
    @UseProxy
    @Disabled
    @DisplayName("Заглушка отображается при отсутствии картин")
    void blankAboutMissingPaintingsShouldBeDisplayed() {
        getSelenideProxy().addResponseFilter("response filter", (httpResponse, httpMessageContents, httpMessageInfo) -> {
            if (httpMessageInfo.getUrl().endsWith("/api/painting?size=9&page=0")){
                String response = """
                        {
                            "content": [],
                            "pageable": {
                                "pageNumber": 0,
                                "pageSize": 9,
                                "sort": {
                                    "empty": true,
                                    "sorted": false,
                                    "unsorted": true
                                },
                                "offset": 0,
                                "paged": true,
                                "unpaged": false
                            },
                            "last": false,
                            "totalPages": 21,
                            "totalElements": 188,
                            "first": true,
                            "size": 9,
                            "number": 0,
                            "sort": {
                                "empty": true,
                                "sorted": false,
                                "unsorted": true
                            },
                            "numberOfElements": 9,
                            "empty": false
                        }
                        """;
                httpMessageContents.setTextContents(response);
                httpResponse.headers().set("Content-Type", "application/json");
                httpResponse.setStatus(HttpResponseStatus.OK);
            }
        });
        Selenide.open(PaintingPage.URL, PaintingPage.class);
    }
}
