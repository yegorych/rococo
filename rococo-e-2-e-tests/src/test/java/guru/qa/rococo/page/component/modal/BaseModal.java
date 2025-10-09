package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.rococo.page.BasePage;
import guru.qa.rococo.page.component.BaseComponent;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;

import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.actions;


public abstract class BaseModal<T extends BaseModal<T>> extends BaseComponent<T> {
    private final SelenideElement closeBtn = self.$("button.variant-ringed");
    private final SelenideElement submitBtn = self.$("button[type='submit']");
    private final SelenideElement fileInput = self.$("input[type='file']");

    protected BaseModal() {
        super($(".modal .card"));
    }

    @Nonnull
    @Step("click close button")
    public <T extends BasePage<?>> T closeModal(T expectedPage) {
        closeBtn.click();
        return expectedPage;
    }


    @Nonnull
    @Step("click submit button")
    public <T> T submit(T expectedPage) {
        submitBtn.click();
        return expectedPage;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Step("upload new photo")
    public <T extends BaseModal<T>> T uploadPhoto(String pathToImage) {
        fileInput.uploadFromClasspath(pathToImage);
        return (T) this;
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    @Step("scroll through {1} elements")
    public <T extends BaseModal<T>> T scrollElements(ElementsCollection elements, int count) throws InterruptedException {
        for (int i = 0; i < count; i++) {
            if (elements.size() >= count){
                break;
            }
            elements.last().scrollIntoView(true);
            Thread.sleep(100);
        }
        return (T) this;
    }

    @Nonnull
    @Step("click on empty area")
    public <T extends BasePage<?>> T clickOnEmptyArea(T expectedPage) {
        actions().moveByOffset(10, 10).click().perform();
        return expectedPage;
    }

    @Nonnull
    @Step("check that modal has opened")
    public <T extends BaseModal<T>> T checkModalHasOpened(){
        self.should(visible);
        return (T) this;
    }


}
