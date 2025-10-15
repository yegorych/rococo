package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
@DisplayName("web: тесты страницы регистрации")
public class RegistrationTest {
    private final String password = "12345";
    private RegisterPage registerPage;
    private final String longString = "12345678901234567890123456789012381290381903819038109381098390218390183019381092388492190481094819048104";
    private final String shortString = "1";

    @BeforeEach
    public void before() {
        registerPage = Selenide.open(RegisterPage.URL, RegisterPage.class);
    }

    @Test
    @DisplayName("Новый пользователь успешно регистрируется")
    void newUserShouldBeRegistered() {
        String username = randomUsername();
        registerPage
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkRegistrationSuccess();
    }

    @Test
    @DisplayName("Имя пользователя не может быть пустым")
    void usernameShouldNotBeBlank() {
        registerPage
                .setUsername(" ")
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkUsernameCannotBeBlank();
    }

    @Test
    @DisplayName("Имя пользователя должно быть от 3 до 255 символов")
    void usernameShouldBeValidLength() {
        registerPage
                .setUsername(shortString)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkUsernameLength()
                .setUsername(longString)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkUsernameLength();
    }

    @Test
    @DisplayName("Имя пользователя обязательно для заполнения")
    void usernameShouldBeRequired() {
        registerPage
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .isRegistrationPage();
    }

    @Test
    @DisplayName("Пользователь с уже существующим именем не может быть зарегистрирован повторно")
    void userWithExistingNameShouldNotBeRegistered() {
        String username = randomUsername();
        registerPage
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkRegistrationSuccess();

        Selenide.open(RegisterPage.URL, RegisterPage.class)
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkUserExists(username);
    }

    @Test
    @DisplayName("Пароль должен быть от 3 до 255 символов")
    void passwordShouldBeValidLength() {
        registerPage
                .setUsername(randomUsername())
                .setPassword(shortString)
                .setPasswordSubmit(shortString)
                .submit()
                .checkPasswordLength()
                .setUsername(randomUsername())
                .setPassword(longString)
                .setPasswordSubmit(longString)
                .submit()
                .checkPasswordLength();
    }

    @Test
    @DisplayName("Пароли должны совпадать")
    void passwordsShouldBeEqual() {
        registerPage
                .setUsername(randomUsername())
                .setPassword("123")
                .setPasswordSubmit("321")
                .submit()
                .checkPasswordEqual();
    }

    @Test
    @DisplayName("Пароль не может быть пустым")
    void passwordShouldNotBeBlank() {
        registerPage
                .setUsername(randomUsername())
                .setPassword(" ")
                .setPasswordSubmit(" ")
                .submit()
                .checkPasswordCannotBeBlank();
    }

}
