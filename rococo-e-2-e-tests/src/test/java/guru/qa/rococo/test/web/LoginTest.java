package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.*;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

    @Test
    @User
    void profileIconShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(user.username(), user.password())
                .submit(new MainPage())
                .checkThatPageLoaded();
    }


    @Test
    @ApiLogin(user = @User)
    void profileIconShouldBeDisplayedAfterSuccessApiLogin() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded();
    }

    @Test
    @Painting(title = "dasd")
    void museumTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded();
    }

    @Test
    @ApiLogin(user = @User(username = "duck", password = "12345"))
    void currentUserLoginTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded();
    }
}
