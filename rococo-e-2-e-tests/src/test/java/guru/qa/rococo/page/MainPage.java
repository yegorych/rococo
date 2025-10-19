package guru.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl();
    private final Header header = new Header();
    private final SelenideElement slogan = $("#page p");
    private final SelenideElement paintingBtn = $("#page li a[href='/painting']");
    private final SelenideElement museumBtn = $("#page li a[href='/museum']");
    private final SelenideElement artistBtn = $("#page li a[href='/artist']");
    private final static String sloganText = "Ваши любимые картины и художники всегда рядом";

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
    @Step("go to header")
    public Header header() {
        return header;
    }

    @Step("check slogan")
    public void checkSlogan() {
        slogan.should(visible).should(text(sloganText));
    }

    @Nonnull
    @Step("click paintings button")
    public PaintingPage clickPaintingsButton() {
        paintingBtn.click();
        return new PaintingPage();
    }

    @Nonnull
    @Step("click artists button")
    public ArtistPage clickArtistsButton() {
        artistBtn.click();
        return new ArtistPage();
    }

    @Nonnull
    @Step("click museums button")
    public MuseumPage clickMuseumsButton() {
        museumBtn.click();
        return new MuseumPage();
    }






}
