package guru.qa.rococo.page;

import com.codeborne.selenide.Selenide;
import guru.qa.rococo.config.Config;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;



@Nonnull
@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();

    @Nonnull
    public <T extends BasePage<?>> T goToPage(String url, Class<T> pageClass) {
        return Selenide.open(url, pageClass);
    }

    public abstract T checkThatPageLoaded();


}
