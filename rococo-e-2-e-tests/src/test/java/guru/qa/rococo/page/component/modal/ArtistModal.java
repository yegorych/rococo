package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.component.Selection;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static guru.qa.rococo.condition.ArtistConditions.artistModal;
import static guru.qa.rococo.condition.MuseumConditions.museumModal;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class ArtistModal extends BaseModal<ArtistModal> {
    private final SelenideElement name = self.$("input[name='name']");
    private final SelenideElement biography = self.$("textarea[name='biography']");
    private final SelenideElement photo = self.$("img");

    public ArtistModal() {
        super();
    }

    @Nonnull
    @Step("set new name")
    public ArtistModal setName(String name) {
        this.name.clear();
        this.name.setValue(name);
        return this;
    }

    @Nonnull
    @Step("set new biography")
    public ArtistModal setBiography(String biography) {
        this.biography.clear();
        this.biography.setValue(biography);
        return this;
    }

    @Nonnull
    @Step("check artist modal")
    public ArtistModal checkEditArtistModalWithPhoto(ArtistJson artistJson, BufferedImage expectedImage) {
        Assertions.assertAll(
                () -> self.should(artistModal(artistJson)),
                () -> photo.should(image(expectedImage))
        );
        return this;
    }

    @Nonnull
    @Step("check artist modal")
    public ArtistModal checkEditArtistModal(ArtistJson artistJson) {
        self.should(artistModal(artistJson));
        return this;
    }

    @Nonnull
    @Step("check artist modal")
    public ArtistModal checkAddArtistModal() {
        self.should(artistModal(ArtistJson.emptyArtist()));
        return this;
    }

    @Step("check photo in edit artist modal")
    public void checkArtistPhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
    }


}
