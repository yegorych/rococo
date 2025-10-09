package guru.qa.rococo.condition;

import com.codeborne.selenide.*;
import com.codeborne.selenide.impl.WebElementWrapper;
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

import static com.codeborne.selenide.CheckResult.accepted;

@ParametersAreNonnullByDefault
public class MuseumConditions {
    public record MuseumFront(String title, String description, String city, String country) {
    }

    @Nonnull
    public static WebElementCondition museum(MuseumJson museum) {
        return new WebElementCondition("museum" + museum.title()) {

            final MuseumFront expectedMuseumsFront = museum.toMuseumFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {


                String title = element.findElements(By.tagName("div")).getFirst().getText();
                String geo = element.findElements(By.tagName("div")).getLast().getText();
                String city = StringUtils.substringBefore(geo, ",");
                String country = StringUtils.substringAfter(geo, ",").trim();
                MuseumFront actualMuseumsFront = new MuseumFront(title, null, city, country);

                StringBuilder stringBuilder = new StringBuilder("\n");
                compareStrings(
                        expectedMuseumsFront.title,
                        actualMuseumsFront.title,
                        "Museum title mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumsFront.city,
                        actualMuseumsFront.city,
                        "Museum city mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumsFront.country,
                        actualMuseumsFront.country,
                        "Museum country mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }

            @NotNull
            @Override
            public String toString() {
                return expectedMuseumsFront.toString();
            }
        };
    }

    @Nonnull
    public static WebElementCondition museumDetail(MuseumJson museum) {
        return new WebElementCondition("museum" + museum.title()) {

            final MuseumFront expectedMuseumsFront = museum.toMuseumFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {
                String title = element.findElement(By.cssSelector("header.card-header")).getText();
                String geo = element.findElement(By.cssSelector("div.text-center")).getText();
                String country = StringUtils.substringBefore(geo, ",");
                String city = StringUtils.substringAfter(geo, ",").trim();
                String description = element.findElements(By.tagName("div")).getLast().getText();
                MuseumFront actualMuseumsFront = new MuseumFront(title, description, city, country);

                StringBuilder stringBuilder = new StringBuilder("\n");

                compareStrings(
                        expectedMuseumsFront.title,
                        actualMuseumsFront.title,
                        "Museum title mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumsFront.city,
                        actualMuseumsFront.city,
                        "Museum city mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumsFront.country,
                        actualMuseumsFront.country,
                        "Museum country mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumsFront.description,
                        actualMuseumsFront.description,
                        "Museum description mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }



            @NotNull
            @Override
            public String toString() {
                return expectedMuseumsFront.toString();
            }
        };
    }

    @Nonnull
    public static WebElementCondition museumModal(MuseumJson museum) {
        return new WebElementCondition("museum modal" + museum.title()) {

            final MuseumFront expectedMuseumModal = museum.toMuseumFront();

            @NotNull
            @Override
            public CheckResult check(@NotNull Driver driver, @NotNull WebElement element) {

                SelenideElement el = WebElementWrapper.wrap(driver, element);
                String title = el.$("input[name='title']").getValue();
                String city = el.$("input[name='city']").getValue();
                String description = el.$("textarea[name='description']").getValue();
                String country = new Selection<>(el.$("select[name='countryId']"), new MuseumModal()).getSelectedOptionText();
                MuseumFront actualMuseumModal = new MuseumFront(title, description, city, country);

                StringBuilder stringBuilder = new StringBuilder("\n");
                compareStrings(
                        expectedMuseumModal.title,
                        actualMuseumModal.title,
                        "Museum title mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumModal.city,
                        actualMuseumModal.city,
                        "Museum city mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumModal.country,
                        actualMuseumModal.country,
                        "Museum country mismatch",
                        stringBuilder);

                compareStrings(
                        expectedMuseumModal.description,
                        actualMuseumModal.description,
                        "Museum description mismatch",
                        stringBuilder);

                return getCheckResult(stringBuilder);
            }



            @NotNull
            @Override
            public String toString() {
                return expectedMuseumModal.toString();
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
