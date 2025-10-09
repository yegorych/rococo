package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.apache.commons.lang.NotImplementedException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {
    private static final String USERNAME_LENGTH_ERROR_MESSAGE = "Allowed username length should be from 3 to 50 characters";
    private static final String PASSWORD_LENGTH_ERROR_MESSAGE = "Allowed password length should be from 3 to 12 characters";
    private static final String USERNAME_EXISTS_ERROR_MESSAGE = "Username `%s` already exists";
    private static final String BLANK_USERNAME_ERROR_MESSAGE = "Username can not be blank";
    private static final String BLANK_PASSWORD_ERROR_MESSAGE = "Password can not be blank";
    private static final String PASSWORD_EQUAL_ERROR_MESSAGE = "Passwords should be equal";

    public static final String URL = CFG.authUrl() + "register";
    private final SelenideElement username = $("input#username");
    private final SelenideElement password = $("input#password");
    private final SelenideElement passwordSubmit = $("input#passwordSubmit");
    private final SelenideElement submitBtn = $("button.form__submit");
    private final SelenideElement loginBtnOnSignup = $("a.form__link");
    private final SelenideElement loginBtnAfterSignup = $("a.form__submit");
    private final SelenideElement registerForm = $("form[action='/register']");


    @Nonnull
    @Step("set username")
    public RegisterPage setUsername(String username) {
        this.username.sendKeys(username);
        return this;
    }
    @Nonnull
    @Step("set password")
    public RegisterPage setPassword(String password) {
        this.password.sendKeys(password);
        return this;
    }
    @Nonnull
    @Step("set password submit")
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        this.passwordSubmit.sendKeys(passwordSubmit);
        return this;
    }
    @Nonnull
    @Step("submit registration")
    public RegisterPage submit() {
        submitBtn.click();
        return this;
    }
    @Nonnull
    @Step("go to login page")
    public LoginPage goToLoginPage() {
        loginBtnOnSignup.click();
        return new LoginPage();
    }

    @Step("check that registration was successful")
    public void checkRegistrationSuccess(){
        loginBtnAfterSignup.should(Condition.visible);
    }

    @Nonnull
    @Step("check that this is Registration page")
    public RegisterPage isRegistrationPage(){
        registerForm.should(visible);
        return this;
    }

    @Nonnull
    @Step("check username length")
    public RegisterPage checkUsernameLength() {
        $(byText(USERNAME_LENGTH_ERROR_MESSAGE)).should(Condition.visible);
        return this;
    }

    @Step("check that user already exists")
    public void checkUserExists(String username){
        $(byText(USERNAME_EXISTS_ERROR_MESSAGE.formatted(username))).should(Condition.visible);
    }

    @Step("check that the username cannot be blank")
    public void checkUsernameCannotBeBlank(){
        $(byText(BLANK_USERNAME_ERROR_MESSAGE)).should(Condition.visible);
    }

    @Step("check that the password cannot be blank")
    public void checkPasswordCannotBeBlank(){
        $(byText(BLANK_PASSWORD_ERROR_MESSAGE)).should(Condition.visible);
    }


    @Step("check password length")
    public RegisterPage checkPasswordLength(){
        $(byText(PASSWORD_LENGTH_ERROR_MESSAGE)).should(Condition.visible);
        return this;
    }

    @Step("check error about password mismatch is visible")
    public void checkPasswordEqual(){
        $(byText(PASSWORD_EQUAL_ERROR_MESSAGE)).should(Condition.visible);
    }


    @Override
    public RegisterPage checkThatPageLoaded() {
        registerForm.should(visible);
        return this;
    }
}
