package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@Nonnull
public class LoginPage extends BasePage<LoginPage>{

  public static final String URL = CFG.authUrl() + "login";
  private final static String BAD_CREDENTIALS_ERROR_MESSAGE = "Неверные учетные данные пользователя";
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement registerBtn = $("a.form__link");
  private final SelenideElement passwordVisibilityBtn = $("button.form__password-button");

  @Nonnull
  public LoginPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Nonnull
  @Step("go to registration page")
  public RegisterPage navigateToRegistration() {
    registerBtn.click();
    return new RegisterPage();

  }

  @Step("check bad credentials error")
  public void assertBadCredentials(){
    $(byText(BAD_CREDENTIALS_ERROR_MESSAGE)).should(visible);
  }

  @Nonnull
  @Step("check that this is Login page")
  public LoginPage isLoginPage(){
    usernameInput.should(visible);
    passwordInput.should(visible);
    return this;
  }

  @Step("Submit login")
  @Nonnull
  public <T extends BasePage<?>> T submit(T expectedPage) {
    submitBtn.click();
    return expectedPage;
  }

  @Override
  @Step("check that login page loaded")
  public LoginPage checkThatPageLoaded() {
    submitBtn.should(visible);
    return this;
  }
}
