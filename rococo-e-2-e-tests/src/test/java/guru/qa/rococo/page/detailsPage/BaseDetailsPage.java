package guru.qa.rococo.page.detailsPage;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rococo.page.BasePage;

import static com.codeborne.selenide.Selenide.$;

public abstract class BaseDetailsPage<T extends BaseDetailsPage<T>> extends BasePage<T> {
    protected final SelenideElement self;

    protected BaseDetailsPage(SelenideElement self) {
        this.self = self;
    }

    protected BaseDetailsPage() {
        this.self = $("article");
    }

}
