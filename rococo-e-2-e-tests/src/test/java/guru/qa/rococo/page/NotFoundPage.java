package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class NotFoundPage extends BasePage<NotFoundPage>{
    public static final String URL = CFG.frontUrl() + "not-found";
    private final SelenideElement title = $("#page section p");
    private final SelenideElement toMainPageBtn = $("#page section .btn");

    @Override
    public NotFoundPage checkThatPageLoaded() {
        title.should(visible).should(text("Страница не найдена"));
        return this;
    }

    @Nonnull
    @Step("click main page btn")
    public MainPage clickMainPageBtn() {
        toMainPageBtn.click();
        return new MainPage();
    }

    @Nonnull
    @Step("check that main page btn is visible")
    public NotFoundPage checkMainPageBtnVisible() {
        toMainPageBtn.should(visible);
        return this;
    }
}
