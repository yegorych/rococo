package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl();
    private final Header header = new Header();
    private final SelenideElement paintingsLink = $("#page a[href='/painting']");
    private final SelenideElement artistsLink = $("#page a[href='/artist']");
    private final SelenideElement museumsLink = $("#page a[href='/museum']");
    private final SelenideElement slogan = $("#page p");

    @Nonnull
    @Override
    @Step("check that main page loaded")
    public MainPage checkThatPageLoaded() {
        slogan.should(visible);
        return this;
    }

    @Nonnull
    @Step("go to Login page")
    public LoginPage goToLoginPage() {
        header.clickLogin();
        return new LoginPage();
    }

    @Nonnull
    @Step("go to Painting page")
    public PaintingPage openPaintings() {
        paintingsLink.click();
        return new PaintingPage();
    }

    @Nonnull
    @Step("go to Artist page")
    public ArtistPage openArtists() {
        artistsLink.click();
        return new ArtistPage();
    }

    @Nonnull
    @Step("go to Museum page")
    public MuseumPage openMuseums() {
        museumsLink.click();
        return new MuseumPage();
    }

    @Nonnull
    @Step("go to header")
    public Header header() {
        return header;
    }




}
