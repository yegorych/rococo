package guru.qa.rococo.page;

import com.codeborne.selenide.*;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.component.Search;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.page.detailsPage.MuseumDetailsPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rococo.condition.MuseumConditions.museum;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class MuseumPage extends BasePage<MuseumPage> {
    public static final String URL = CFG.frontUrl() + "museum/";
    private final SelenideElement title = $("h2.m-4");
    private final Search search = new Search();
    private final ElementsCollection museums = $$("main ul li");
    private final SelenideElement addMuseumBtn = $("button[type='button'].variant-filled-primary");

    @Nonnull
    @Step("select museum with title: {0}")
    public MuseumDetailsPage selectMuseumByTitle(String title) {
        findMuseum(title);
        museums.find(Condition.text(title)).click();
        return new MuseumDetailsPage();
    }

    @Nonnull
    @Step("click on the Add Museum button")
    public MuseumModal clickAddMuseumBtn() {
        addMuseumBtn.click();
        return new MuseumModal();
    }

    @Nonnull
    @Step("check that Add Museum button is displayed")
    public MuseumPage checkAddMuseumBtnIsDisplayed() {
        addMuseumBtn.should(visible);
        return this;
    }

    @Nonnull
    @Step("check that Add Museum button is not displayed")
    public MuseumPage checkAddMuseumBtnIsNotDisplayed() {
        addMuseumBtn.shouldNot(visible);
        return this;
    }


    @Nonnull
    @Override
    @Step("check that Museum page loaded")
    public MuseumPage checkThatPageLoaded() {
        title.shouldHave(Condition.text("Музеи")).should(visible);
        return this;
    }

    @Nonnull
    @Step("find Museum with title: {0}")
    public MuseumPage findMuseum(String title) {
        search.search(title);
        return this;
    }

    @Nonnull
    @Step("check museum card")
    public MuseumPage checkMuseumCardWithPhoto(MuseumJson museumJsons, BufferedImage expectedImage) {
        findMuseum(museumJsons.title());
        Assertions.assertAll(
                () -> museums.first().should(museum(museumJsons)),
                () -> checkMuseumPhoto(expectedImage),
                () -> Assertions.assertTrue(true)
        );
        return this;
    }

    @Nonnull
    @Step("check museum card")
    public MuseumPage checkMuseumCard(MuseumJson museumJsons) {
        findMuseum(museumJsons.title());
        museums.first().should(museum(museumJsons));
        return this;
    }

    @Step("check museum photo")
    public MuseumPage checkMuseumPhotoWithTitle(String title, BufferedImage expectedImage) {
        findMuseum(title);
        checkMuseumPhoto(expectedImage);
        return this;
    }

    @Step("check museum photo")
    private void checkMuseumPhoto(BufferedImage expectedImage) {
        museums.first().find(By.tagName("img")).should(image(expectedImage));
    }

    @Nonnull
    @Step("check that the number of museums is not less than {0}")
    public MuseumPage checkNumberOfMuseumsIsGreaterThanOrEqual(int cardCount) {
        museums.should(CollectionCondition.sizeGreaterThanOrEqual(cardCount));
        return this;
    }

    @Nonnull
    @Step("check that the number of museums is {0}")
    public MuseumPage checkNumberOfMuseumsEqual(int cardCount) {
        museums.should(CollectionCondition.size(cardCount));
        return this;
    }

    @Nonnull
    @Step("check that message about empty search result is displayed")
    public MuseumPage checkMessageAboutEmptyResultShouldBeDisplayed() {
        $(byText("Музеи не найдены")).should(visible);
        $(byText("Для указанного вами фильтра мы не смогли не найти ни одного музея")).should(visible);
        return this;
    }

    @Nonnull
    @Step("scroll {0} museums")
    public MuseumPage scrollMuseumCard(int count) {
        scrollElements(museums, count);
        return this;
    }

    public Search search() {
        return search;
    }
}
