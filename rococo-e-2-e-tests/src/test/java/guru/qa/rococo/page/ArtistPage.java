package guru.qa.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.page.component.Header;
import guru.qa.rococo.page.component.Search;
import guru.qa.rococo.page.component.modal.ArtistModal;
import guru.qa.rococo.page.detailsPage.ArtistDetailsPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rococo.condition.ArtistConditions.artist;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class ArtistPage extends BasePage<ArtistPage> {
    public static final String URL = CFG.frontUrl() + "artist/";
    private final SelenideElement title = $("h2.m-4");
    private final Search search = new Search();
    private final Header header = new Header();
    private final ElementsCollection artists = $$("main ul li");
    private final SelenideElement addArtistBtn = $("button[type='button'].variant-filled-primary");

    @Nonnull
    @Step("select artist with name: {0}")
    public ArtistDetailsPage selectArtistByName(String name) {
        findArtist(name);
        artists.find(Condition.text(name)).click();
        return new ArtistDetailsPage();
    }

    @Nonnull
    @Step("click on the Add artist button")
    public ArtistModal clickAddArtistBtn() {
        addArtistBtn.click();
        return new ArtistModal();
    }

    @Step("check that Add artist button is displayed")
    public void checkAddArtistBtnIsDisplayed() {
        addArtistBtn.should(visible);
    }

    @Nonnull
    @Step("check that Add Artist button is not displayed")
    public void checkAddArtistBtnIsNotDisplayed() {
        addArtistBtn.shouldNot(visible);
    }


    @Nonnull
    @Override
    @Step("check that Artist page loaded")
    public ArtistPage checkThatPageLoaded() {
        title.shouldHave(Condition.text("Художники")).should(visible);
        return this;
    }

    @Nonnull
    @Step("find Artist with name: {0}")
    public ArtistPage findArtist(String name) {
        search.search(name);
        return this;
    }

    @Nonnull
    @Step("check artist card")
    public ArtistPage checkArtistCardWithPhoto(ArtistJson artistJson, BufferedImage expectedImage) {
        findArtist(artistJson.name());
        Assertions.assertAll(
                () -> artists.first().should(artist(artistJson)),
                () -> checkArtistPhoto(expectedImage)
        );
        return this;
    }

    @Nonnull
    @Step("check artist card")
    public ArtistPage checkArtistCard(ArtistJson artistJson) {
        findArtist(artistJson.name());
        artists.first().should(artist(artistJson));
        return this;
    }

    @Step("check artist photo with name {0}")
    public ArtistPage checkArtistPhotoWithName(String name, BufferedImage expectedImage) {
        findArtist(name);
        checkArtistPhoto(expectedImage);
        return this;
    }

    @Step("check artist photo")
    private void checkArtistPhoto(BufferedImage expectedImage) {
        artists.first().find(By.tagName("figure")).should(image(expectedImage));
    }

    @Nonnull
    @Step("check that the number of artists is not less than {0}")
    public ArtistPage checkNumberOfArtistsIsGreaterThanOrEqual(int cardCount) {
        artists.should(CollectionCondition.sizeGreaterThanOrEqual(cardCount));
        return this;
    }

    @Nonnull
    @Step("check that the number of artists is {0}")
    public ArtistPage checkNumberOfArtistsEqual(int cardCount) {
        artists.should(CollectionCondition.size(cardCount));
        return this;
    }

    @Nonnull
    @Step("check that message about empty search result is displayed")
    public ArtistPage checkMessageAboutEmptyResultShouldBeDisplayed() {
        $(byText("Художники не найдены")).should(visible);
        $(byText("Для указанного вами фильтра мы не смогли найти художников")).should(visible);
        return this;
    }

    @Nonnull
    @Step("scroll {0} artists")
    public ArtistPage scrollArtistCard(int count) {
        scrollElements(artists, count);
        return this;
    }

    public Search search() {
        return search;
    }

    public Header header() {
        return header;
    }
}
