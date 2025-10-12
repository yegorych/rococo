package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class MainPageTest {

    @Test
    void loginBtnShouldBeDisplayedForUnauthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .checkLoginBtnIsVisible();
    }
}
