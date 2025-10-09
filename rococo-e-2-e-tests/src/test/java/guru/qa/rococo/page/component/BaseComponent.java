package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BaseComponent<T extends BaseComponent<T>> {

  protected final SelenideElement self;
  private final ElementsCollection validationErrors = $$(".text-error-400");

  protected BaseComponent(SelenideElement self) {
    this.self = self;
  }

  @Nonnull
  @SuppressWarnings("unchecked")
  @Step("check that validation error has text: {0} ")
  public <T> T checkValidationError(String text) {
    validationErrors.find(Condition.text(text)).should(Condition.exist);
    return (T) this;
  }
}
