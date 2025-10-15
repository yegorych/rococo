package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.jupiter.annotation.meta.WebTest;
import guru.qa.rococo.page.NotFoundPage;
import guru.qa.rococo.page.detailsPage.ArtistDetailsPage;
import guru.qa.rococo.page.detailsPage.MuseumDetailsPage;
import guru.qa.rococo.page.detailsPage.PaintingDetailsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("web: тесты страницы Not found")
@WebTest
public class NotFoundPageTest {

    @Test
    @DisplayName("Страница 404 содержит кнопку перехода на главную")
    void notFoundPageShouldContainHomeNavigationButton() {
        Selenide.open(NotFoundPage.URL, NotFoundPage.class)
                .checkThatPageLoaded()
                .checkMainPageBtnVisible()
                .clickMainPageBtn()
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Страница 404 отображается для несуществующего художника")
    void notFoundPageShouldBeDisplayedForUnknownArtist() {
        Selenide.open(ArtistDetailsPage.URL("123"), NotFoundPage.class)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Страница 404 отображается для несуществующей картины")
    void notFoundPageShouldBeDisplayedForUnknownPainting() {
        Selenide.open(PaintingDetailsPage.URL("123"), NotFoundPage.class)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Страница 404 отображается для несуществующего музея")
    void notFoundPageShouldBeDisplayedForUnknownMuseum() {
        Selenide.open(MuseumDetailsPage.URL("123"), NotFoundPage.class)
                .checkThatPageLoaded();
    }
}
