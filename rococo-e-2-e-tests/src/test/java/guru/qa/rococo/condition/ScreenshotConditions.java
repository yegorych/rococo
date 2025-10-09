package guru.qa.rococo.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import guru.qa.rococo.jupiter.extension.ScreenShotTestExtension;
import guru.qa.rococo.utils.ScreenDiffResult;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.impl.Plugins.inject;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class ScreenshotConditions {
    @Nonnull
    public static WebElementCondition image(BufferedImage expectedImage) {
        return new WebElementCondition("Screenshot comparison") {

            private static final ScreenShotLaboratory screenshots = inject();

            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                ScreenDiffResult screenDiffResult = new ScreenDiffResult(
                        expectedImage,
                        chartScreenshot(driver, element)
                );
                return new CheckResult(
                        !screenDiffResult.getAsBoolean(),
                        ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
                );
            }

            @SneakyThrows
            private BufferedImage chartScreenshot(Driver driver, WebElement element) {
                return ImageIO.read(requireNonNull(
                        screenshots.takeScreenshot(
                                driver,
                                element
                        )
                ));
            }
        };
    }
}
