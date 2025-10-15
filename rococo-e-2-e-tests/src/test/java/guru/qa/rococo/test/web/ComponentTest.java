package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.model.Color;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
@DisplayName("web: тесты компонентов")
public class ComponentTest {
    @Test
    @ApiLogin
    @DisplayName("Snackbar закрывается по кнопке закрытия")
    void snackbarShouldBeClosedByCloseBtn() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname("   ")
                .submit(new MainPage())
                .snackbar().closeSnackbar(new MainPage())
                .snackbar().checkSnackbarIsNotVisible(new MainPage());
    }

    @Test
    @ApiLogin
    @DisplayName("Snackbar исчезает автоматически")
    void snackbarShouldBeDisappeared() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname("   ")
                .submit(new MainPage())
                .snackbar().checkSnackbarDisappears(new MainPage());
    }

    @Test
    @ApiLogin
    @DisplayName("Snackbar с ошибкой красного цвета в темной теме")
    void snackbarShouldBeRedInBlackTheme() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname("   ")
                .submit(new MainPage())
                .snackbar().checkSnackbarColor(Color.red);
    }

    @Test
    @ApiLogin
    @DisplayName("Snackbar об успехе желтого цвета в темной теме")
    void snackbarShouldBeYellowInBlackTheme() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname("name")
                .submit(new MainPage())
                .snackbar().checkSnackbarColor(Color.yellow);
    }
}
