package guru.qa.rococo.page.component.modal;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.visible;
import static guru.qa.rococo.condition.ScreenshotConditions.image;

public class ProfileModal extends BaseModal<ProfileModal> {
    private final SelenideElement logoutBtn = self.$("button.variant-ghost");
    private final SelenideElement photo = self.$("img.avatar-image");
    private final SelenideElement firstname = self.$("input[name='firstname']");
    private final SelenideElement surname = self.$("input[name='surname']");
    private final SelenideElement username = self.$("h4.text-center");


    public ProfileModal() {
        super();
    }

    @Nonnull
    @Step("click Log out button")
    public <T extends BasePage<?>> T logout(T expectedPage) {
        logoutBtn.click();
        return expectedPage;
    }

    @Nonnull
    @Step("set new firstname")
    public ProfileModal setFirstname(String fistname) {
        this.firstname.setValue(fistname);
        return this;
    }

    @Nonnull
    @Step("set new surname")
    public ProfileModal setSurname(String surname) {
        this.surname.setValue(surname);
        return this;
    }

    @Step("check profile photo")
    public void checkProfilePhoto(BufferedImage expectedImage) {
        photo.should(image(expectedImage));
    }

    @Nonnull
    @Step("check firstname")
    public ProfileModal checkFirstname(String expectedName) {
        this.firstname.should(Condition.value(expectedName));
        return this;
    }

    @Nonnull
    @Step("check surname")
    public ProfileModal checkSurname(String expectedName) {
        this.surname.should(Condition.value(expectedName));
        return this;
    }

    @Nonnull
    @Step("check username")
    public ProfileModal checkUsername(String expectedName) {
        this.username.should(Condition.text(expectedName));
        return this;
    }



























}
