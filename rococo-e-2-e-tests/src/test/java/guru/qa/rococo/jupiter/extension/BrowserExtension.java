package guru.qa.rococo.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import guru.qa.rococo.jupiter.annotation.UseProxy;
import guru.qa.rococo.page.MainPage;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.util.Map;

@ParametersAreNonnullByDefault
public class BrowserExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    TestExecutionExceptionHandler,
    LifecycleMethodExecutionExceptionHandler {


  static {
    String browser = System.getProperty("browser");
    Configuration.browser = browser == null || browser.isBlank()
            ? "chrome"
            : browser;

    Configuration.timeout = 8000;
    Configuration.pageLoadStrategy = "eager";
    Configuration.savePageSource = false;
    Configuration.screenshots = false;

    ChromeOptions chromeOptions = new ChromeOptions();
    FirefoxOptions firefoxOptions = new FirefoxOptions();

    if ("chrome".equalsIgnoreCase(Configuration.browser)) {
      chromeOptions.addArguments("--force-dark-mode");
      chromeOptions.addArguments("--lang=ru-RU");
      chromeOptions.setExperimentalOption("prefs", Map.of(
              "intl.accept_languages", "ru-RU",
              "webkit.webprefs.preferredColorScheme", 2
      ));
      Configuration.browserCapabilities = chromeOptions;
    }
    else if ("firefox".equalsIgnoreCase(Configuration.browser)) {
      firefoxOptions.addPreference("ui.systemUsesDarkTheme", 1);
      firefoxOptions.addPreference("intl.accept_languages", "ru-RU");
      firefoxOptions.addPreference("ui.systemUsesDarkTheme", 1);
      Configuration.browserCapabilities = firefoxOptions;
    }

    if ("docker".equals(System.getProperty("test.env"))) {
      Configuration.remote = "http://selenoid:4444/wd/hub";
      if ("firefox".equalsIgnoreCase(Configuration.browser)) {
        Configuration.browserVersion = "125.0";
      } else {
        chromeOptions.addArguments("--no-sandbox", "--disable-dev-shm-usage");
        Configuration.browserCapabilities = chromeOptions;
        Configuration.browserVersion = "127.0";
      }
    }
  }

  @Override
  public void afterEach(ExtensionContext context){
    if (WebDriverRunner.hasWebDriverStarted()) {
      Selenide.closeWebDriver();
    }
  }

  @Override
  public void beforeEach(ExtensionContext context){
    Selenide.open(MainPage.URL, MainPage.class);//костыль для открытия страницы всегда в темной теме
    SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
        .savePageSource(false)
        .screenshots(false)
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  private static void doScreenshot() {
    if (WebDriverRunner.hasWebDriverStarted()) {
      Allure.addAttachment(
          "Screen on fail",
          new ByteArrayInputStream(
              ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
          )
      );
    }
  }
}
