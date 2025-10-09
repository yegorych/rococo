package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Search extends BaseComponent<Search> {
    private final SelenideElement searchInput = self.$("input[type='search']");
    private final SelenideElement searchBtn = self.$("button");


    public Search() {
    super($("#page div.justify-center"));
    }


    @Step("enter text \"{0}\" into search input")
    public Search enterText(String text) {
        searchInput.setValue(text);
        return this;
    }

    @Step("Click search icon")
    public Search clearSearch() {
        searchInput.clear();
        return this;
    }

    @Step("Click search icon")
    public Search clickSearchBtn() {
        searchBtn.click();
        return this;
    }

    @Step("Click search input placeholder")
    public Search checkPlaceholder(String text) {
        searchInput.should(attribute("placeholder", text));
        return this;
    }


    @Step("search by text: {0}")
    public Search search(String text) {
        return clearSearch()
                .enterText(text)
                .clickSearchBtn();
    }

}
