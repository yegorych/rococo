package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class ComponentTest {
    @Test
    @ApiLogin
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
    void snackbarShouldBeDisappeared() {
        Selenide.open(MainPage.URL, MainPage.class)
                .header()
                .clickProfile()
                .setSurname("   ")
                .submit(new MainPage())
                .snackbar().checkSnackbarDisappears(new MainPage());
    }
}
