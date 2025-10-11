package guru.qa.rococo.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.modal.BaseModal;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class Selection<T extends BaseModal<T>> extends BaseComponent<Selection<T>>{

    private final T modal;
    private final ElementsCollection options = self.getOptions();

    public Selection(SelenideElement self, T modal) {
        super(self);
        this.modal = modal;
    }

    @Nonnull
    @Step("scroll through {0} elements")
    public T scrollElements(int countOfElements) {
        scrollElements(options, countOfElements);
        return modal;
    }

    @Nonnull
    @Step("scroll all elements")
    public T scrollAllElements() {
        scrollAllOptions();
        return modal;
    }

    @Nullable
    @Step("get selected option text")
    public String getSelectedOptionText() {
        return self.getSelectedOptionText();
    }

    @Nonnull
    @Step("select element by name {0}")
    public T selectOption(String name) {
        self.getOptions().findBy(text(name)).scrollIntoView(true).click();
        return modal;
    }

    @Nonnull
    @Step("select any option")
    public T selectAnyOption() {
        if (!options.isEmpty()){
            options.first().scrollIntoView(true).click();
            //self.selectOption(0);
        }
        return modal;
    }

    @SneakyThrows
    private void scrollElements(ElementsCollection elements, int count) {
        for (int i = 0; i < count; i++) {
            if (elements.size() >= count){
                break;
            }
            elements.last().scrollIntoView(true);
            Thread.sleep(200);
        }
    }

    @SneakyThrows
    public void scrollAllOptions()  {
        int previousSize = 0;
        int sameCountIterations = 0;
        int maxSameCountIterations = 5;

        while (true) {
            int currentSize = options.size();

            if (currentSize == previousSize) {
                sameCountIterations++;
                if (sameCountIterations >= maxSameCountIterations) {
                    break;
                }
            } else {
                sameCountIterations = 0;
            }

            previousSize = currentSize;
            options.last().scrollIntoView(true);
            Thread.sleep(200);
        }
    }

}
