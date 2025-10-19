package guru.qa.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.model.Color;
import guru.qa.rococo.page.*;
import guru.qa.rococo.page.component.modal.ProfileModal;
import io.qameta.allure.Step;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {
    private final SelenideElement loginBtn = self.$("button.btn");
    private final SelenideElement profileIcon = $("button.btn-icon");
    private final SelenideElement paintingBtn = self.$("a[href='/painting']");
    private final SelenideElement artistBtn = self.$("a[href='/artist']");
    private final SelenideElement museumBtn = self.$("a[href='/museum']");
    private final SelenideElement rococoBtn = self.$("a[href='/']");
    private final SelenideElement lightswitch = self.$(".lightswitch-track");

    public Header() {
        super($("header#shell-header"));
    }

    @Nonnull
    @Step("check that the login button is visible to the unauthorized user")
    public Header checkLoginBtnIsVisible() {
        loginBtn.shouldBe(visible);
        profileIcon.shouldNotBe(visible);
        return this;
    }

    @Nonnull
    @Step("check that the profile button is visible to authorized user")
    public Header checkProfileBtnIsVisible() {
        loginBtn.shouldNotBe(visible);
        profileIcon.should(visible);
        return this;
    }

    @Nonnull
    @Step("go to Login page")
    public LoginPage clickLogin() {
        loginBtn.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("go to Profile page")
    public ProfileModal clickProfile() {
        profileIcon.click();
        return new ProfileModal();
    }

    @Nonnull
    @Step("go to Artist page")
    public ArtistPage toArtistPage() {
        artistBtn.click();
        return new ArtistPage();
    }

    @Nonnull
    @Step("go to Museum page")
    public MuseumPage toMuseumPage() {
        museumBtn.click();
        return new MuseumPage();
    }

    @Nonnull
    @Step("go to Painting page")
    public PaintingPage toPaintingPage() {
        paintingBtn.click();
        return new PaintingPage();
    }

    @Nonnull
    @Step("click rococo button")
    public MainPage clickRococoBtn() {
        rococoBtn.click();
        return new MainPage();
    }

    @Nonnull
    @Step("click lightswitch")
    public Header clickLightswitch() {
        lightswitch.click();
        return this;
    }

    @Nonnull
    @Step("check small profile photo")
    public Header checkSmallProfilePhoto(BufferedImage expectedPhoto) {
        profileIcon.shouldBe(image(expectedPhoto));
        return this;
    }

    @Step("check buttons color")
    public void checkButtonsColor(Color expectedColor) {
        String rgbaLoginBtn = loginBtn.getCssValue("background-color");
        Assertions.assertEquals(expectedColor.rgb, rgbaLoginBtn);
    }





}
