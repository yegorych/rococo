package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.LoginPage;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("web: тесты страницы логина")
public class LoginPageTest {

    private static final String fakeCredential = "fake";

    @Test
    @User
    @DisplayName("Главная страница отображается после успешного входа")
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(user.username(), user.password())
                .submit(new MainPage())
                .checkThatPageLoaded();
    }


    @Test
    @ApiLogin
    @DisplayName("Главная страница отображается после API-входа")
    void mainPageShouldBeDisplayedAfterSuccessApiLogin() {
        Selenide.open(MainPage.URL, MainPage.class)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Ошибка отображается при вводе некорректных данных")
    void errorShouldBeDisplayedAfterLoginIncorrectCredentials(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, fakeCredential)
                .submit(new LoginPage())
                .isLoginPage()
                .checkBadCredentialsError();
    }

    @Test
    @DisplayName("Поле имени пользователя обязательно для входа")
    void usernameFieldShouldBeRequired(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .submit(new LoginPage())
                .isLoginPage();
    }
    @Test
    @DisplayName("Поле пароля обязательно для входа")
    void passwordFieldShouldBeRequired(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, null)
                .submit(new LoginPage())
                .isLoginPage();
    }

    @Test
    @DisplayName("Пароль скрыт по умолчанию")
    void passwordShouldBeNotVisible(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, fakeCredential)
                .passwordIsNotVisible();
    }

    @Test
    @DisplayName("Пароль отображается после нажатия на иконку глаза")
    void passwordShouldBeVisibleAfterClickingOnEyeBtn(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .doLogin(fakeCredential, fakeCredential)
                .clickEyeBtn()
                .passwordIsVisible();
    }

    @Test
    @DisplayName("Страница регистрации отображается после нажатия на кнопку")
    void registrationPageShouldBeDisplayedAfterRegistrationButton(){
        Selenide.open(MainPage.URL, MainPage.class)
                .goToLoginPage()
                .navigateToRegistration()
                .isRegistrationPage();
    }
}
