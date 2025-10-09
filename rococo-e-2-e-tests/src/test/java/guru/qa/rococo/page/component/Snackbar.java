package guru.qa.rococo.page.component;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$;

public class Snackbar extends BaseComponent<Snackbar> {
    private final SelenideElement logoutBtn = self.$("button.variant-ghost");
    private final SelenideElement closeBtn = self.$("button[aria-label='Dismiss toast']");


    public Snackbar() {
        super($(".snackbar"));
    }

    @Nonnull
    @Step("close snackbar")
    public <T extends BasePage<?>> T closeSnackbar(T expectedPage) {
        closeBtn.click();
        return expectedPage;
    }


    @Step("check that snackbar has text: {0}")
    public void checkSnackbarHasText(String text) {
        self.should(Condition.text(text));
    }



















}
