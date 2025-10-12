package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.AuthApi;
import guru.qa.rococo.api.UserApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.api.core.ThreadSafeCookieStore;
import guru.qa.rococo.model.rest.UserJson;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserApiClient extends RestClient {

  private final AuthApi authApi = new EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
  private final UserApi userApi;

  public UserApiClient() {
    super(CFG.gatewayUrl());
    this.userApi = create(UserApi.class);
  }

  @Step("Get user using GET /api/user")
  @Nonnull
  public Response<UserJson> getUser(String token) {
    return requireNonNull(execute(userApi.currentUser(token)));
  }

  @Step("Update user using PATCH /api/user")
  @Nonnull
  public Response<UserJson> updateUser(String token, UserJson userJson) {
    return requireNonNull(execute(userApi.updateUser(token, userJson)));
  }


  @Step("Create user with username '{0}' using REST API")
  @NotNull
  public UserJson createUser(String username, String password) {
    try {
      authApi.requestRegisterForm().execute();
      authApi.register(
              username,
              password,
              password,
              ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
      ).execute();
      UserJson createdUser = new UserJson(username).withPassword(password);
      return createdUser.withPassword(password);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


  
  
}
