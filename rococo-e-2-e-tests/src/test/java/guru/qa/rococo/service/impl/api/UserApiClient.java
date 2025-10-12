package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.UserApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.rest.UserJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UserApiClient extends RestClient {

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
  
  
}
