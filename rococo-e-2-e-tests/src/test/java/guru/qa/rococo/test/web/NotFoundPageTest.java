package guru.qa.rococo.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.page.NotFoundPage;
import guru.qa.rococo.page.detailsPage.ArtistDetailsPage;
import guru.qa.rococo.page.detailsPage.MuseumDetailsPage;
import guru.qa.rococo.page.detailsPage.PaintingDetailsPage;
import org.junit.jupiter.api.Test;

public class NotFoundPageTest {

    @Test
    void notFoundPageShouldContainHomeNavigationButton() {
        Selenide.open(NotFoundPage.URL, NotFoundPage.class)
                .checkThatPageLoaded()
                .checkMainPageBtnVisible()
                .clickMainPageBtn()
                .checkThatPageLoaded();
    }

    @Test
    void notFoundPageShouldBeDisplayedForUnknownArtist() {
        Selenide.open(ArtistDetailsPage.URL("123"), NotFoundPage.class)
                .checkThatPageLoaded();
    }

    @Test
    void notFoundPageShouldBeDisplayedForUnknownPainting() {
        Selenide.open(PaintingDetailsPage.URL("123"), NotFoundPage.class)
                .checkThatPageLoaded();
    }

    @Test
    void notFoundPageShouldBeDisplayedForUnknownMuseum() {
        Selenide.open(MuseumDetailsPage.URL("123"), NotFoundPage.class)
                .checkThatPageLoaded();
    }
}
