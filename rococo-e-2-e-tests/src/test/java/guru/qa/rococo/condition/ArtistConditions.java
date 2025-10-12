package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.WebElementWrapper;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.page.component.Selection;
import guru.qa.rococo.page.component.modal.MuseumModal;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.Selectors.byText;

@ParametersAreNonnullByDefault
public class ArtistConditions {
    public record ArtistFront(String name, String biography, List<String > paintings) {
    }

    @Nonnull
    public static WebElementCondition artist(ArtistJson artist) {
        return new WebElementCondition("artists " + artist.name()) {

            final ArtistFront expectedArtistFront = artist.toArtistFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {

                String name = element.findElement(By.tagName("span")).getText();
                ArtistFront actualArtistFront = new ArtistFront(name, null, null);

                StringBuilder stringBuilder = new StringBuilder("\n");
                compareStrings(
                        expectedArtistFront.name,
                        actualArtistFront.name,
                        "Artist name mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }

            @NotNull
            @Override
            public String toString() {
                return expectedArtistFront.toString();
            }
        };
    }

    @Nonnull
    public static WebElementCondition artistDetail(ArtistJson artist) {
        return new WebElementCondition("artists " + artist.name()) {

            final ArtistFront expectedArtistFront = artist.toArtistFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {
                String name = element.findElement(By.tagName("header")).getText();
                String biography = element.findElement(By.tagName("p")).getText();
                List<String> paintings = element.findElements(By.tagName("li")).stream().map(WebElement::getText).toList();
                ArtistFront actualArtistFront = new ArtistFront(name, biography, paintings);

                StringBuilder stringBuilder = new StringBuilder("\n");

                compareStrings(
                        expectedArtistFront.name,
                        actualArtistFront.name,
                        "Artist name mismatch",
                        stringBuilder);


                compareStrings(
                        expectedArtistFront.biography,
                        actualArtistFront.biography,
                        "Artist biography mismatch",
                        stringBuilder);

                if (expectedArtistFront.paintings.isEmpty() && actualArtistFront.paintings.isEmpty()) {
                    Assertions.assertTrue(
                            element.findElement(byText("Пока что список картин этого художника пуст."))
                            .isDisplayed()
                    );
                } else {
                    compareLists(
                            expectedArtistFront.paintings,
                            actualArtistFront.paintings,
                            "Artist's paintings mismatch",
                            stringBuilder);
                }

                return getCheckResult(stringBuilder);
            }



            @NotNull
            @Override
            public String toString() {
                return expectedArtistFront.toString();
            }
        };
    }

    @Nonnull
    public static WebElementCondition artistModal(ArtistJson artist) {
        return new WebElementCondition("artist modal " + artist.name()) {

            final ArtistFront expectedArtistModal = artist.toArtistFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {

                SelenideElement el = WebElementWrapper.wrap(driver, element);
                String name = el.$("input[name='name']").getValue();
                String biography = el.$("textarea[name='biography']").getValue();
                ArtistFront actualArtistModal = new ArtistFront(name, biography, new ArrayList<>());

                StringBuilder stringBuilder = new StringBuilder("\n");
                compareStrings(
                        expectedArtistModal.name,
                        actualArtistModal.name,
                        "Artist name mismatch",
                        stringBuilder);

                compareStrings(
                        expectedArtistModal.biography,
                        actualArtistModal.biography,
                        "Artist biography mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }



            @NotNull
            @Override
            public String toString() {
                return expectedArtistModal.toString();
            }
        };
    }


    private static void compareStrings(String expected, String actual, String message, StringBuilder stringBuilder) {
        if (!actual.equals(expected)) {
            stringBuilder.append(
                    String.format(
                            message + " (expected: %s, actual: %s)\n",
                            expected,
                            actual
                    )
            );
        }
    }

    private static void compareLists(List<String> expected, List<String> actual, String message, StringBuilder stringBuilder) {
        Set<String> expectedSet = new HashSet<>(expected);
        Set<String> actualSet = new HashSet<>(actual);
        if (!actualSet.equals(expectedSet)) {
            stringBuilder.append(
                    String.format(
                            message + " (expected: %s, actual: %s)\n",
                            expected,
                            actual
                    )
            );
        }
    }

    @NotNull
    private static CheckResult getCheckResult(StringBuilder stringBuilder) {
        if (stringBuilder.toString().length() > 1) {
            return Assertions.fail(stringBuilder.toString());
        }
        return accepted();
    }



}
