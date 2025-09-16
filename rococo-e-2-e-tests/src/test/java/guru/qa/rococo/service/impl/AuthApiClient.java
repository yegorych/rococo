package guru.qa.rococo.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.core.CodeInterceptor;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.utils.OAuthUtils;
import lombok.SneakyThrows;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class AuthApiClient extends RestClient {

  private static final Config CFG = Config.getInstance();
  private final AuthApi authApi;

  public AuthApiClient() {
    super(CFG.authUrl(), true, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, new CodeInterceptor());
    this.authApi = create(AuthApi.class);
  }

  @SneakyThrows
  public String login(String username, String password) {
    final String codeVerifier = OAuthUtils.generateCodeVerifier();
    final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);
    final String redirectUri = CFG.frontUrl() + "authorized";
    final String clientId = "client";

    authApi.authorize(
        "code",
        clientId,
        "openid",
        redirectUri,
        codeChallenge,
        "S256"
    ).execute();

    authApi.login(
        username,
        password,
        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
    ).execute();

    Response<JsonNode> tokenResponse = authApi.token(
        ApiLoginExtension.getCode(),
        redirectUri,
        clientId,
        codeVerifier,
        "authorization_code"
    ).execute();

    return tokenResponse.body().get("id_token").asText();
  }



    public void requestRegisterForm() {
        executeCall(authApi.requestRegisterForm());
    }


    public void register(String username, String password) {
        executeCall(authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")));
    }
}
