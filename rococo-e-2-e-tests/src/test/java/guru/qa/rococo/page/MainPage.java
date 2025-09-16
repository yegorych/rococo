package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
  public static final String URL = CFG.frontUrl();
  private final SelenideElement profileIcon = $("button.btn-icon");
  private final SelenideElement loginBtn = $("button.btn");
  private final SelenideElement headerBar = $(".app-bar-row-main");

  @Nonnull
  @Override
  @Step("check that main page loaded")
  public MainPage checkThatPageLoaded() {
    headerBar.should(visible);
    return this;
  }

  @Nonnull
  @Step("go to Login page")
  public LoginPage goToLoginPage() {
    loginBtn.click();
    return new LoginPage();
  }
}
