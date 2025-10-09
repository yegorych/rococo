package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.ArtistApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.CountryJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class ArtistApiClient extends RestClient {

  private final ArtistApi artistApi;

  public ArtistApiClient() {
    super(CFG.gatewayUrl());
    this.artistApi = create(ArtistApi.class);
  }

  @Step("Get artists using GET /api/artist with page={1}, size={2}, name={0}")
  @Nonnull
  public Response<RestResponsePage<ArtistJson>> getArtistPage(@Nullable String name,
                                                              int page,
                                                              int size) {
    return requireNonNull(execute(artistApi.getArtistsPage(name, page, size)));
  }

  @Step("Get artist by id={0} using GET /api/artist")
  @Nonnull
  public Response<ArtistJson> getArtist(String id) {
    return requireNonNull(execute(artistApi.getArtistById(id)));
  }

  @Step("Create artist using POST /api/artist")
  @Nonnull
  public Response<ArtistJson> createArtist(String token, ArtistJson artistJson) {
    return requireNonNull(execute(artistApi.createArtist(token, artistJson)));
  }

  @Step("Update artist using PATCH /api/artist")
  @Nonnull
  public Response<ArtistJson> updateArtist(String token, ArtistJson artistJson) {
    return requireNonNull(execute(artistApi.updateArtist(token, artistJson)));
  }


}
