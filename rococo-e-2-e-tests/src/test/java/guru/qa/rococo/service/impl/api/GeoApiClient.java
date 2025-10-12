package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.GeoApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class GeoApiClient extends RestClient {

  private final GeoApi geoApi;

  public GeoApiClient() {
    super(CFG.gatewayUrl());
    this.geoApi = create(GeoApi.class);
  }

  @Step("Get countries using /api/country endpoint with page={0}, size={1}")
  @Nonnull
  public Response<RestResponsePage<CountryJson>> getCountryPage(int page,
                                                         int size) {
    return requireNonNull(execute(geoApi.getCountries(page, size)));
  }







}
