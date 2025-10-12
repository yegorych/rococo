package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.component.Selection;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static guru.qa.rococo.condition.MuseumConditions.museumModal;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class MuseumModal extends BaseModal<MuseumModal> {
    private final SelenideElement title = self.$("input[name='title']");
    private final SelenideElement city = self.$("input[name='city']");
    private final SelenideElement description = self.$("textarea[name='description']");
    private final Selection<MuseumModal> selectionCountry = new Selection<>(
            self.$("select[name='countryId']"),
            this
    );
    private final SelenideElement photo = self.$("img");

    public MuseumModal() {
        super();
    }

    @Nonnull
    @Step("set new title")
    public MuseumModal setTitle(String title) {
        this.title.should(visible);
        this.title.clear();
        this.title.setValue(title);
        return this;
    }

    @Nonnull
    @Step("set new city")
    public MuseumModal setCity(String city) {
        this.city.should(visible);
        this.city.clear();
        this.city.setValue(city);
        return this;
    }

    @Nonnull
    @Step("set new description")
    public MuseumModal setDescription(String description) {
        this.description.should(visible);
        this.description.clear();
        this.description.setValue(description);
        return this;
    }

    public Selection<MuseumModal> countrySelection() {
        return selectionCountry;
    }


    @Nonnull
    @Step("check museum modal")
    public MuseumModal checkEditMuseumModalWithPhoto(MuseumJson museumJsons, BufferedImage expectedImage) {
        Assertions.assertAll(
                () -> self.should(museumModal(museumJsons)),
                () -> photo.should(image(expectedImage))
        );
        return this;
    }

    @Nonnull
    @Step("check museum modal")
    public MuseumModal checkEditMuseumModal(MuseumJson museumJsons) {
        self.should(museumModal(museumJsons));
        return this;
    }

    @Nonnull
    @Step("check museum modal")
    public MuseumModal checkAddMuseumModal() {
        self.should(museumModal(MuseumJson.emptyMuseum()));
        return this;
    }

    @Step("check photo in edit museum modal")
    public void checkMuseumPhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
    }

    @Step("check photo in edit museum modal")
    public MuseumModal selectCountry(String country) {
        scrollAllCountries();
        selectionCountry.selectOption(country);
        return this;
    }

    @Nonnull
    @Step("scroll through all countries")
    public MuseumModal scrollAllCountries() {
        return selectionCountry.scrollElements(195);
    }


}
