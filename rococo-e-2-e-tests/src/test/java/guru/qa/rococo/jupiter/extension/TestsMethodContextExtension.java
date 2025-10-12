package guru.qa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TestsMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

  private static final ThreadLocal<ExtensionContext> store = new ThreadLocal<>();

  @Override
  public void beforeEach(ExtensionContext context){
    store.set(context);
  }

  @Override
  public void afterEach(ExtensionContext context){
    store.remove();
  }

  public static ExtensionContext context() {
    return store.get();
  }
}
