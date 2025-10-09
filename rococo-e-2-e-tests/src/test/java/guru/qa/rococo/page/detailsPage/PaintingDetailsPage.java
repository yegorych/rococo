package guru.qa.rococo.page.detailsPage;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.PaintingPage;
import guru.qa.rococo.page.component.modal.PaintingModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rococo.condition.PaintingConditions.paintingDetail;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class PaintingDetailsPage extends BaseDetailsPage<PaintingDetailsPage> {
    private final SelenideElement title = self.$("header.card-header");
    private final SelenideElement description = self.$("div  div.m-4");
    private final SelenideElement artist = self.$("div.text-center");
    private final SelenideElement editPaintingBtn = self.$("button");
    private final SelenideElement photo = self.$("img");

    public PaintingDetailsPage() {
        super();
    }

    public static String URL(String id){
        return PaintingPage.URL + id;
    }

    @Nonnull
    @Step("click on the Edit button")
    public PaintingModal clickOnEditBtn() {
        editPaintingBtn.click();
        return new PaintingModal();
    }


    @Nonnull
    @Step("check painting details")
    public PaintingDetailsPage checkPaintingDetails(PaintingJson paintingJsons) {
        self.should(paintingDetail(paintingJsons));
        return this;
    }

    @Step("check painting details photo")
    public PaintingDetailsPage checkPaintingPhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
        return this;
    }

    @Step("check that edit painting button is visible")
    public PaintingDetailsPage checkEditPaintingIsVisible() {
        editPaintingBtn.should(visible);
        return this;
    }

    @Step("check that edit painting button is not visible")
    public PaintingDetailsPage checkEditPaintingIsNotVisible() {
        editPaintingBtn.shouldNot(visible);
        return this;
    }

    @Step("check that painting details page loaded")
    public PaintingDetailsPage checkThatPageLoaded() {
        self.should(visible);
        return this;
    }
}
