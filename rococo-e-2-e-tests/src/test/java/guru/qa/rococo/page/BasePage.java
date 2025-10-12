package guru.qa.rococo.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.page.component.Snackbar;
import guru.qa.rococo.page.component.modal.BaseModal;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;


@Nonnull
@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();
    private final Snackbar snackbar = new Snackbar();

    @Nonnull
    public <T extends BasePage<?>> T goToPage(String url, Class<T> pageClass) {
        return Selenide.open(url, pageClass);
    }

    public abstract T checkThatPageLoaded();

    @Step("check that page not have modal window")
    public void checkPageNotHaveModalWindow() {
        $(".modal .card").shouldNot(Condition.exist);
    }

    @Step("check snackbar has text '{0}'")
    @SuppressWarnings("unchecked")
    public T checkSnackbarText(String text) {
        snackbar.checkSnackbarHasText(text);
        return (T) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Step("scroll through {1} elements")
    @SneakyThrows
    public <T extends BasePage<T>> T scrollElements(ElementsCollection elements, int count) {
        for (int i = 0; i < count; i++) {
            if (elements.size() >= count){
                break;
            }
            elements.last().scrollIntoView(true);
            Thread.sleep(100);
        }
        return (T) this;
    }


    public Snackbar snackbar() {
        return snackbar;
    }
}
