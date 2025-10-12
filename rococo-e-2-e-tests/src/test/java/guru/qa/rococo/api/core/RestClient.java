package guru.qa.rococo.api.core;

import guru.qa.rococo.api.logging.CompactImageHttpLogger;
import guru.qa.rococo.config.Config;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public abstract class RestClient {

  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient okHttpClient;
  private final Retrofit retrofit;
  private final static HttpLoggingInterceptor safeInterceptor = new HttpLoggingInterceptor(new CompactImageHttpLogger());
    static {
      safeInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

  public RestClient(String baseUrl) {
    this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.NONE, safeInterceptor);
  }

  public RestClient(String baseUrl, HttpLoggingInterceptor.Level level) {
    this(baseUrl, false, JacksonConverterFactory.create(), level);
  }

  public RestClient(String baseUrl, boolean followRedirect) {
    this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
  }

  public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
    this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY, interceptors);
  }

  public RestClient(String baseUrl, Converter.Factory factory) {
    this(baseUrl, false, factory, HttpLoggingInterceptor.Level.BODY);
  }

  public RestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .followRedirects(followRedirect);

    if (ArrayUtils.isNotEmpty(interceptors)) {
      for (Interceptor interceptor : requireNonNull(interceptors)) {
        builder.addNetworkInterceptor(interceptor);
      }
    }

    builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
    builder.cookieJar(
        new JavaNetCookieJar(
            new CookieManager(
                ThreadSafeCookieStore.INSTANCE,
                CookiePolicy.ACCEPT_ALL
            )
        )
    );
    this.okHttpClient = builder.build();
    this.retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(factory)
        .build();
  }

  public  <T> T attempt(int retries, Supplier<T> action) {
    for (int i = 0; i < retries; i++) {
      try {
        return action.get();
      } catch (Exception e) {
        if (i == retries - 1) throw e;
      }
    }
    throw new RuntimeException("Should not reach here");
  }

  public <T> T create(final Class<T> service) {
    return this.retrofit.create(service);
  }

  @Nullable
  protected  <T> T executeAndExtractBody(Call<T> call){
    final Response<T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertTrue(response.isSuccessful() || (response.code() >= 300 && response.code() < 400));
    return response.body();
  }
  

  @Nullable
  protected <T> Response<T> execute(Call<T> call){
    final Response<T> response;
    try {
      response = call.execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    Assertions.assertNotNull(response);
    return response;
  }

  public final class EmtyRestClient extends RestClient {
    public EmtyRestClient(String baseUrl) {
      super(baseUrl);
    }

    public EmtyRestClient(String baseUrl, boolean followRedirect) {
      super(baseUrl, followRedirect);
    }

    public EmtyRestClient(String baseUrl, Converter.Factory factory) {
      super(baseUrl, factory);
    }

    public EmtyRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
      super(baseUrl, followRedirect, factory, level, interceptors);
    }
  }
}
