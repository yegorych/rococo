package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.WebElementWrapper;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.page.component.Selection;
import guru.qa.rococo.page.component.modal.MuseumModal;
import guru.qa.rococo.page.component.modal.PaintingModal;
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

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.Selectors.byText;

@ParametersAreNonnullByDefault
public class PaintingConditions {
    public record PaintingFront(String title, String description, String artist, String museum) {
    }

    @Nonnull
    public static WebElementCondition painting(PaintingJson painting) {
        return new WebElementCondition("painting " + painting.title()) {

            final PaintingFront expectedPaintingFront = painting.toPaintingFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {

                String title = element.findElement(By.tagName("div")).getText();
                PaintingFront actualPaintingFront = new PaintingFront(title, null, null, null);

                StringBuilder stringBuilder = new StringBuilder("\n");

                compareStrings(
                        expectedPaintingFront.title,
                        actualPaintingFront.title,
                        "Painting title mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }

            @NotNull
            @Override
            public String toString() {
                return expectedPaintingFront.toString();
            }
        };
    }

    @Nonnull
    public static WebElementCondition paintingDetail(PaintingJson painting) {
        return new WebElementCondition("paintings " + painting.title()) {

            final PaintingFront expectedPaintingFront = painting.toPaintingFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {
                String title = element.findElement(By.cssSelector("header")).getText();
                String artist = element.findElement(By.cssSelector("div.text-center")).getText();
                String description = element.findElement(By.cssSelector("div.grid div.m-4")).getText();

                PaintingFront actualPaintingFront = new PaintingFront(title, description, artist, null);

                StringBuilder stringBuilder = new StringBuilder("\n");

                compareStrings(
                        expectedPaintingFront.title,
                        actualPaintingFront.title,
                        "Painting title mismatch",
                        stringBuilder);


                compareStrings(
                        expectedPaintingFront.description,
                        actualPaintingFront.description,
                        "Painting description mismatch",
                        stringBuilder);

                compareStrings(
                        expectedPaintingFront.artist,
                        actualPaintingFront.artist,
                        "Painting artist mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }



            @NotNull
            @Override
            public String toString() {
                return expectedPaintingFront.toString();
            }
        };
    }

    @Nonnull
    public static WebElementCondition paintingModal(PaintingJson painting) {
        return new WebElementCondition("painting modal " + painting.title()) {

            final PaintingFront expectedPaintingModal = painting.toPaintingFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {

                SelenideElement el = WebElementWrapper.wrap(driver, element);
                String title = el.$("input[name='title']").getValue();
                String description = el.$("textarea[name='description']").getValue();
                String artist = new Selection<>(el.$("select[name='authorId']"), new PaintingModal())
                        .getSelectedOptionText();

                String museum = new Selection<>(el.$("select[name='museumId']"), new PaintingModal())
                        .getSelectedOptionText();

                PaintingFront actualPaintingModal = new PaintingFront(title, description, artist, museum);


                StringBuilder stringBuilder = new StringBuilder("\n");
                compareStrings(
                        expectedPaintingModal.title,
                        actualPaintingModal.title,
                        "Painting title mismatch",
                        stringBuilder);

                compareStrings(
                        expectedPaintingModal.description,
                        actualPaintingModal.description,
                        "Painting description mismatch",
                        stringBuilder);

                compareStrings(
                        expectedPaintingModal.artist,
                        actualPaintingModal.artist,
                        "Painting artist mismatch",
                        stringBuilder);

                compareStrings(
                        expectedPaintingModal.museum,
                        actualPaintingModal.museum,
                        "Painting museum mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }



            @NotNull
            @Override
            public String toString() {
                return expectedPaintingModal.toString();
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

    @NotNull
    private static CheckResult getCheckResult(StringBuilder stringBuilder) {
        if (stringBuilder.toString().length() > 1) {
            return Assertions.fail(stringBuilder.toString());
        }
        return accepted();
    }



}
