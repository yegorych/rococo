package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.ScreenShotTest;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.page.MainPage;
import guru.qa.rococo.page.component.modal.ProfileModal;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static guru.qa.rococo.utils.RandomDataUtils.randomWord;

@WebTest
@DisplayName("web: тесты профиля")
public class ProfileTest {

    @Test
    @ApiLogin
    @DisplayName("Иконка профиля отображается для авторизованного пользователя")
    void profileIconShouldBeDisplayedForAuthorizedUser() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .checkProfileBtnIsVisible();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно профиля открывается")
    void profileModalShouldBeDisplayed() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .checkModalHasOpened();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно профиля закрывается по кнопке закрытия")
    void profileModalShouldBeClosedByClickingOnTheCloseButton() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .closeModal(new MainPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @DisplayName("Модальное окно профиля закрывается по клику вне области")
    void profileModalShouldBeClosedByClickingOnEmptyArea() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .clickOnEmptyArea(new MainPage())
                .checkPageNotHaveModalWindow();
    }

    @Test
    @ApiLogin
    @DisplayName("Имя пользователя отображается в профиле")
    void usernameShouldBeDisplayedInProfile(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .checkUsername(user.username());
    }

    @Test
    @ApiLogin
    @DisplayName("Имя сохраняется и отображается в профиле")
    void firstnameShouldBeSavedAndDisplayed() {
        String firstname = RandomDataUtils.randomName();
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setFirstname(firstname)
                .submit(new MainPage())
                .checkSnackbarText("Профиль обновлен")
                .header()
                .clickProfile()
                .checkFirstname(firstname);
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия сохраняется и отображается в профиле")
    void surnameShouldBeSavedAndDisplayed() {
        String surname = RandomDataUtils.randomSurname();
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setFirstname(surname)
                .submit(new MainPage())
                .checkSnackbarText("Профиль обновлен")
                .header()
                .clickProfile()
                .checkFirstname(surname);
    }

    @ApiLogin
    @ScreenShotTest(expected = "expected-small-avatar.png", rewriteExpected = true)
    @DisplayName("Маленькое фото профиля сохраняется и отображается")
    void smallProfilePhotoShouldBeSavedAndDisplayed(BufferedImage expectedAvatar) {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .uploadPhoto("img/avatar.png")
                .submit(new MainPage())
                .checkSnackbarText("Профиль обновлен")
                .header()
                .checkSmallProfilePhoto(expectedAvatar);
    }

    @ApiLogin
    @ScreenShotTest(expected = "expected-avatar.png", rewriteExpected = true)
    @DisplayName("Фото профиля сохраняется и отображается")
    void profilePhotoShouldBeSavedAndDisplayed(BufferedImage expectedAvatar) {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .uploadPhoto("img/avatar.png")
                .submit(new MainPage())
                .checkSnackbarText("Профиль обновлен")
                .header()
                .clickProfile()
                .checkProfilePhoto(expectedAvatar);
    }

    @Test
    @ApiLogin
    @DisplayName("Имя не должно превышать 255 символов")
    void firstnameLengthShouldBeUnder255() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setFirstname(randomWord(256))
                .submit(new ProfileModal())
                .checkValidationError("Имя не может быть длиннее 255 символов");

    }

    @Test
    @ApiLogin
    @DisplayName("Имя не должно состоять только из пробелов")
    void firstnameShouldNotConsistOfSpaces() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setFirstname("   ")
                .submit(new MainPage())
                .checkSnackbarText("Имя и фамилия не могут состоять из пробелов");
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия не должна состоять только из пробелов")
    void surnameShouldNotConsistOfSpaces() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname("   ")
                .submit(new MainPage())
                .checkSnackbarText("Имя и фамилия не могут состоять из пробелов");
    }

    @Test
    @ApiLogin
    @DisplayName("Фамилия не должна превышать 255 символов")
    void surnameLengthShouldBeUnder255() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname(randomWord(256))
                .submit(new ProfileModal())
                .checkValidationError("Фамилия не может быть длиннее 255 символов");
    }

    @Test
    @ApiLogin
    @DisplayName("Пользователь выходит из профиля")
    void userShouldBeLoggedOut() {
        MainPage page = Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .logout(new MainPage())
                .checkSnackbarText("Сессия завершена");
        Selenide.refresh();
        page.header().checkLoginBtnIsVisible();
    }
}
