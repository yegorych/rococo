package guru.qa.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideConfig;
import guru.qa.rococo.jupiter.annotation.UseProxy;
import guru.qa.rococo.jupiter.annotation.User;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.Method;

@Disabled
public class UseProxyExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        AnnotationSupport.findAnnotation(extensionContext.getRequiredTestMethod(), UseProxy.class)
                .ifPresent(proxy->
                        Selenide.open("about:blank", new SelenideConfig().proxyEnabled(true)));
    }
}

