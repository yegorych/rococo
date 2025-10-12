package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.SessionApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.SessionJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SessionApiClient extends RestClient {

  private final SessionApi sessionApi;

  public SessionApiClient() {
    super(CFG.gatewayUrl());
    this.sessionApi = create(SessionApi.class);
  }

  @Step("Get session using GET /api/session")
  @Nonnull
  public Response<SessionJson> getSession(String token) {
    return requireNonNull(execute(sessionApi.session(token)));
  }







}
