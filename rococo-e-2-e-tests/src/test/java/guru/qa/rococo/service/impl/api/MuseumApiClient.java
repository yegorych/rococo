package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.MuseumApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.MuseumJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class MuseumApiClient extends RestClient {

  private final MuseumApi museumApi;

  public MuseumApiClient() {
    super(CFG.gatewayUrl());
    this.museumApi = create(MuseumApi.class);
  }

  @Step("Get museums using GET /api/museum with page={1}, size={2}, title={0}")
  @Nonnull
  public Response<RestResponsePage<MuseumJson>> getMuseumPage(@Nullable String title,
                                                              int page,
                                                              int size) {
    return requireNonNull(execute(museumApi.getMuseumPage(title, page, size)));
  }

  @Step("Get museum by id={0} using GET /api/museum")
  @Nonnull
  public Response<MuseumJson> getMuseum(String id) {
    return requireNonNull(execute(museumApi.getMuseumById(id)));
  }

  @Step("Create museum using POST /api/museum")
  @Nonnull
  public Response<MuseumJson> createMuseum(String token, MuseumJson museumJson) {
    return requireNonNull(execute(museumApi.createMuseum(token, museumJson)));
  }

  @Step("Update museum using PATCH /api/museum")
  @Nonnull
  public Response<MuseumJson> updateMuseum(String token, MuseumJson museumJson) {
    return requireNonNull(execute(museumApi.updateMuseum(token, museumJson)));
  }
  
  
}
