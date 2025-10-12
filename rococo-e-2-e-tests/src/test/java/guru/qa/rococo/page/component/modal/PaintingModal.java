package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.component.Selection;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static guru.qa.rococo.condition.PaintingConditions.paintingModal;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class PaintingModal extends BaseModal<PaintingModal> {
    private final SelenideElement title = self.$("input[name='title']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final SelenideElement photo = self.$("img");
    private final Selection<PaintingModal> selectionArtist = new Selection<>(
            self.$("select[name='authorId']"),
            this
    );
    private final Selection<PaintingModal> selectionMuseum = new Selection<>(
            self.$("select[name='museumId']"),
            this
    );

    public PaintingModal() {
        super();
    }

    @Nonnull
    @Step("set new title")
    public PaintingModal setTitle(String title) {
        this.title.clear();
        this.title.setValue(title);
        return this;
    }

    @Nonnull
    @Step("set new description")
    public PaintingModal setDescription(String description) {
        this.description.clear();
        this.description.setValue(description);
        return this;
    }

    public Selection<PaintingModal> selectionArtist() {
        return selectionArtist;
    }

    public Selection<PaintingModal> selectionMuseum() {
        return selectionMuseum;
    }


    @Nonnull
    @Step("check painting modal")
    public PaintingModal checkEditPaintingModalWithPhoto(PaintingJson paintingJsons, BufferedImage expectedImage) {
        scrollAllArtists();
        scrollAllMuseum();
        Assertions.assertAll(
                () -> self.should(paintingModal(paintingJsons)),
                () -> photo.should(image(expectedImage))
        );
        return this;
    }

    @Nonnull
    @Step("check painting modal")
    public PaintingModal checkEditPaintingModal(PaintingJson paintingJsons) {
        self.should(paintingModal(paintingJsons));
        return this;
    }

    @Nonnull
    @Step("check painting modal")
    public PaintingModal checkAddPaintingModal() {
        self.should(paintingModal(PaintingJson.emptyPainting()));
        return this;
    }

    @Step("check photo in edit painting modal")
    public void checkPaintingPhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
    }

    @Step("select artist")
    public PaintingModal selectArtist(String artist) {
        selectionArtist.scrollAllOptions();
        selectionArtist.selectOption(artist);
        return this;
    }

    @Step("select any artist")
    public PaintingModal selectAnyArtist() {
        selectionArtist.selectAnyOption();
        return this;
    }

    @Nonnull
    @Step("scroll through all artists")
    public PaintingModal scrollAllArtists() {
        return selectionArtist.scrollAllElements();
    }

    @Step("select museum")
    public PaintingModal selectMuseum(String museum) {
        selectionMuseum.scrollAllOptions();
        selectionMuseum.selectOption(museum);
        return this;
    }

    @Step("select any museum")
    public PaintingModal selectAnyMuseum() {
        selectionMuseum.selectAnyOption();
        return this;
    }

    @Nonnull
    @Step("scroll through all museum")
    public PaintingModal scrollAllMuseum() {
        return selectionMuseum.scrollAllElements();
    }



}
