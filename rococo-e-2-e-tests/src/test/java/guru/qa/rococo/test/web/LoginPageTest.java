package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginPageTest {

    private static final String fakeCredential = "fake";

    @Test
    @User
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(user.username(), user.password())
                .submit(new MainPage())
                .checkThatPageLoaded();
    }


    @Test
    @ApiLogin
    void mainPageShouldBeDisplayedAfterSuccessApiLogin() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded();
    }

    @Test
    void errorShouldBeDisplayedAfterLoginIncorrectCredentials(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, fakeCredential)
                .submit(new LoginPage())
                .isLoginPage()
                .checkBadCredentialsError();
    }

    @Test
    void usernameFieldShouldBeRequired(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .submit(new LoginPage())
                .isLoginPage();
    }
    @Test
    void passwordFieldShouldBeRequired(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, null)
                .submit(new LoginPage())
                .isLoginPage();
    }

    @Test
    void passwordShouldBeNotVisible(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, fakeCredential)
                .passwordIsNotVisible();
    }

    @Test
    void passwordShouldBeVisibleAfterClickingOnEyeBtn(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, fakeCredential)
                .clickEyeBtn()
                .passwordIsVisible();
    }

    @Test
    void registrationPageShouldBeDisplayedAfterRegistrationButton(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .navigateToRegistration()
                .isRegistrationPage();
    }










}
