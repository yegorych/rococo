package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
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
    void usernameShouldNotBeBlank() {
        registerPage
                .setUsername(" ")
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkUsernameCannotBeBlank();
    }

    @Test
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
    void usernameShouldBeRequired() {
        registerPage
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .isRegistrationPage();
    }

    @Test
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
    void passwordsShouldBeEqual() {
        registerPage
                .setUsername(randomUsername())
                .setPassword("123")
                .setPasswordSubmit("321")
                .submit()
                .checkPasswordEqual();
    }

    @Test
    void passwordShouldNotBeBlank() {
        registerPage
                .setUsername(randomUsername())
                .setPassword(" ")
                .setPasswordSubmit(" ")
                .submit()
                .checkPasswordCannotBeBlank();
    }




}
