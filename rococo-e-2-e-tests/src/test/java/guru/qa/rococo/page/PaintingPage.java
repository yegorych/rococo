package guru.qa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.component.Search;
import guru.qa.rococo.page.component.modal.PaintingModal;
import guru.qa.rococo.page.detailsPage.PaintingDetailsPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rococo.condition.PaintingConditions.painting;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class PaintingPage extends BasePage<PaintingPage> {
    public static final String URL = CFG.frontUrl() + "painting/";
    private final SelenideElement title = $("h2.m-4");
    private final Search search = new Search();
    private final ElementsCollection paintings = $$("main ul li");
    private final SelenideElement addPaintingBtn = $("button[type='button'].variant-filled-primary");

    @Nonnull
    @Step("select painting with title: {0}")
    public PaintingDetailsPage selectPaintingByTitle(String title) {
        findPainting(title);
        paintings.find(Condition.text(title)).click();
        return new PaintingDetailsPage();
    }

    @Nonnull
    @Step("click on the Add painting button")
    public PaintingModal clickAddPaintingBtn() {
        addPaintingBtn.click();
        return new PaintingModal();
    }

    @Nonnull
    @Step("check that Add painting button is displayed")
    public PaintingPage checkAddPaintingBtnIsDisplayed() {
        addPaintingBtn.should(visible);
        return this;
    }

    @Nonnull
    @Step("check that Add Painting button is not displayed")
    public PaintingPage checkAddPaintingBtnIsNotDisplayed() {
        addPaintingBtn.shouldNot(visible);
        return this;
    }


    @Nonnull
    @Override
    @Step("check that Painting page loaded")
    public PaintingPage checkThatPageLoaded() {
        title.shouldHave(Condition.text("Картины")).should(visible);
        return this;
    }

    @Nonnull
    @Step("find Painting with name: {0}")
    public PaintingPage findPainting(String name) {
        search.search(name);
        return this;
    }

    @Nonnull
    @Step("check painting card")
    public PaintingPage checkPaintingCardWithPhoto(PaintingJson paintingJson, BufferedImage expectedImage) {
        findPainting(paintingJson.title());
        Assertions.assertAll(
                () -> paintings.first().should(painting(paintingJson)),
                () -> checkPaintingPhoto(expectedImage)
        );
        return this;
    }

    @Nonnull
    @Step("check painting card")
    public PaintingPage checkPaintingCard(PaintingJson... paintings) {
        for (PaintingJson paintingJson : paintings) {
            findPainting(paintingJson.title());
            this.paintings.first().should(painting(paintingJson));
        }
        return this;
    }

    @Step("check painting photo with name {0}")
    public PaintingPage checkPaintingPhotoWithName(String name, BufferedImage expectedImage) {
        findPainting(name);
        checkPaintingPhoto(expectedImage);
        return this;
    }

    @Step("check painting photo")
    private void checkPaintingPhoto(BufferedImage expectedImage) {
        paintings.first().find(By.tagName("img")).should(image(expectedImage));
    }

    @Nonnull
    @Step("check that the number of paintings is not less than {0}")
    public PaintingPage checkNumberOfPaintingsIsGreaterThanOrEqual(int cardCount) {
        paintings.should(CollectionCondition.sizeGreaterThanOrEqual(cardCount));
        return this;
    }

    @Nonnull
    @Step("check that the number of paintings is {0}")
    public PaintingPage checkNumberOfPaintingsEqual(int cardCount) {
        paintings.should(CollectionCondition.size(cardCount));
        return this;
    }

    @Nonnull
    @Step("check that message about empty search result is displayed")
    public PaintingPage checkMessageAboutEmptyResultShouldBeDisplayed() {
        $(byText("Картины не найдены")).should(visible);
        $(byText("Для указанного вами фильтра мы не смогли не найти ни одной картины")).should(visible);
        return this;
    }

    @Nonnull
    @Step("scroll {0} paintings")
    public PaintingPage scrollPaintingCard(int count) {
        scrollElements(paintings, count);
        return this;
    }

    public Search search() {
        return search;
    }
}
