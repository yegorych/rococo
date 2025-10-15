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
}
