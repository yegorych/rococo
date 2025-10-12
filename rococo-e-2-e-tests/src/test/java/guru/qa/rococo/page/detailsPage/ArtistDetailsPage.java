package guru.qa.rococo.page.detailsPage;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.ArtistPage;
import guru.qa.rococo.page.MuseumPage;
import guru.qa.rococo.page.component.modal.ArtistModal;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.page.component.modal.PaintingModal;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rococo.condition.ArtistConditions.artistDetail;
import static guru.qa.rococo.condition.MuseumConditions.museumDetail;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class ArtistDetailsPage extends BaseDetailsPage<ArtistDetailsPage> {
    private final SelenideElement name = self.$("header");
    private final SelenideElement description = self.$("p");
    private final SelenideElement editArtistBtn = self.$("button[data-testid='edit-artist']");
    private final SelenideElement addPaintingBtn = self.$("button.variant-filled-primary");
    private final SelenideElement secondAddPaintingBtn = self.$("button.ml-4");
    private final ElementsCollection paintings = self.$$("li");
    private final SelenideElement photo = self.$("figure img");

    public ArtistDetailsPage() {
        super();
    }

    public static String URL(String id){
        return ArtistPage.URL + id;
    }

    @Nonnull
    @Step("click on the Edit button")
    public ArtistModal clickOnEditBtn() {
        editArtistBtn.click();
        return new ArtistModal();
    }

    @Nonnull
    @Step("click on the second add painting button")
    public PaintingModal clickSecondAddPaintingBtn() {
        secondAddPaintingBtn.click();
        return new PaintingModal();
    }

    @Nonnull
    @Step("click on the add painting button")
    public PaintingModal clickAddPaintingBtn() {
        addPaintingBtn.click();
        return new PaintingModal();
    }

    @Nonnull
    @Step("check artist details")
    public ArtistDetailsPage checkArtistDetails(ArtistJson artistJson) {
        if (!artistJson.paintings().isEmpty()){
            scrollPaintings(artistJson.paintings().size());
        }
        self.should(artistDetail(artistJson));
        return this;
    }

    @Step("check artist details photo")
    public ArtistDetailsPage checkArtistPhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
        return this;
    }

    @Step("check that edit artist button is visible")
    public ArtistDetailsPage checkEditArtistIsVisible() {
        editArtistBtn.should(visible);
        return this;
    }

    @Step("check that edit artist button is not visible")
    public ArtistDetailsPage checkEditArtistIsNotVisible() {
        editArtistBtn.shouldNot(visible);
        return this;
    }

    @Step("check that artist details page loaded")
    public ArtistDetailsPage checkThatPageLoaded() {
        self.should(visible);
        return this;
    }

    @Nonnull
    @Step("scroll {0} paintings")
    public ArtistDetailsPage scrollPaintings(int count) {
        paintings.first().should(visible);
        scrollElements(paintings, count);
        return this;
    }

    @Step("check that add painting button is visible")
    public ArtistDetailsPage checkAddPaintingIsVisible() {
        addPaintingBtn.should(visible);
        return this;
    }

    @Step("check that add painting button is not visible")
    public ArtistDetailsPage checkAddPaintingIsNotVisible() {
        addPaintingBtn.shouldNot(visible);
        return this;
    }

    @Step("check that second add painting button is visible")
    public ArtistDetailsPage checkSecondAddPaintingIsVisible() {
        secondAddPaintingBtn.should(visible);
        return this;
    }

    @Step("check that second add painting button is not visible")
    public ArtistDetailsPage checkSecondAddPaintingIsNotVisible() {
        secondAddPaintingBtn.shouldNot(visible);
        return this;
    }




}
