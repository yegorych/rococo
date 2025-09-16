package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.RegisterPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

public class RegistrationTest {
    private static final Config CFG = Config.getInstance();
    RegisterPage registerPage;

    @BeforeEach
    public void before() {
        registerPage = Selenide.open(RegisterPage.URL, RegisterPage.class);
    }

    @Test
    void shouldRegisterNewUser() {
        String username = randomUsername();
        String password = "12345";
        registerPage
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .submit()
                .checkRegistrationSuccess();
    }
}
