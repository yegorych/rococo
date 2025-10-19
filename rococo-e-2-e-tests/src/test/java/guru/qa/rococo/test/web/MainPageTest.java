package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("web: тесты главной страницы")
public class MainPageTest {

    @Test
    @DisplayName("Для неавторизованного пользователя на главной отображается кнопка Войти")
    void loginBtnShouldBeDisplayedForUnauthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .checkLoginBtnIsVisible();
    }

    @Test
    @DisplayName("На главной странице отображается слоган")
    void sloganOnMainPageShouldBeDisplayed() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkSlogan();
    }

    @Test
    @DisplayName("Можно перейти на страницу с художниками с главной страницы")
    void artistPageShouldBeOpenedFromMainPage() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickArtistsButton()
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Можно перейти на страницу с картинами с главной страницы")
    void paintingPageShouldBeOpenedFromMainPage() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickPaintingsButton()
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Можно перейти на страницу с музеями с главной страницы")
    void museumPageShouldBeOpenedFromMainPage() {
        Selenide.open(MainPage.URL, MainPage.class)
                .clickMuseumsButton()
                .checkThatPageLoaded();
    }


}
