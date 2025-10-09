package guru.qa.rococo.page.detailsPage;

import com.codeborne.selenide.*;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.component.modal.MuseumModal;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static guru.qa.rococo.condition.MuseumConditions.museumDetail;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class MuseumDetailsPage extends BaseDetailsPage<MuseumDetailsPage> {
    private final SelenideElement title = self.$("header.card-header");
    private final SelenideElement geo = self.$("div.text-center");
    private final SelenideElement description = self.$x("//div[not(@*)]");
    private final SelenideElement editMuseumBtn = self.$("button");
    private final SelenideElement photo = self.$("article img");

    public MuseumDetailsPage() {
        super();
    }

    public static String URL(String id){
        return MuseumPage.URL + id;
    }

    @Nonnull
    @Step("click on the Edit button")
    public MuseumModal clickOnEditBtn() {
        editMuseumBtn.click();
        return new MuseumModal();
    }


    @Nonnull
    @Step("check museum details")
    public MuseumDetailsPage checkMuseumDetails(MuseumJson museumJsons) {
        self.should(museumDetail(museumJsons));
        return this;
    }

    @Step("check museum details photo")
    public MuseumDetailsPage checkMuseumPhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
        return this;
    }

    @Step("check that edit museum button is visible")
    public MuseumDetailsPage checkEditMuseumIsVisible() {
        editMuseumBtn.should(visible);
        return this;
    }

    @Step("check that edit museum button is not visible")
    public MuseumDetailsPage checkEditMuseumIsNotVisible() {
        editMuseumBtn.shouldNot(visible);
        return this;
    }

    @Step("check that museum details page loaded")
    public MuseumDetailsPage checkThatPageLoaded() {
        self.should(visible);
        return this;
    }
}
