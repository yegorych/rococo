package guru.qa.rococo.api.core;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

@ParametersAreNonnullByDefault
public enum ThreadSafeCookieStore implements CookieStore {
  INSTANCE;

  private final ThreadLocal<CookieStore> threadLocalCookieStore = ThreadLocal.withInitial(
      this::inMemoryCookieStore
  );

  private CookieStore inMemoryCookieStore() {
    return new CookieManager().getCookieStore();
  }

  @Override
  public void add(URI uri, HttpCookie cookie) {
    getStore().add(uri, cookie);
  }

  @Nonnull
  @Override
  public List<HttpCookie> get(URI uri) {
    return getStore().get(uri);
  }

  @Nonnull
  @Override
  public List<HttpCookie> getCookies() {
    return getStore().getCookies();
  }

  @Nonnull
  @Override
  public List<URI> getURIs() {
    return getStore().getURIs();
  }

  @Override
  public boolean remove(URI uri, HttpCookie cookie) {
    return getStore().remove(uri, cookie);
  }

  @Override
  public boolean removeAll() {
    return getStore().removeAll();
  }

  @Nonnull
  public String cookieValue(String name) {
    return getCookies().stream()
        .filter(c -> c.getName().equals(name))
        .map(HttpCookie::getValue)
        .findFirst()
        .orElseThrow();
  }

  private CookieStore getStore() {
    return threadLocalCookieStore.get();
  }
}
