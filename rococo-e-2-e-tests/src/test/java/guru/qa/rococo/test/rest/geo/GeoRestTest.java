package guru.qa.rococo.test.rest.geo;

import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.service.CountryClient;
import guru.qa.rococo.service.impl.db.CountryDbClient;
import guru.qa.rococo.service.impl.api.GeoApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestTest
public class GeoRestTest {

  private final GeoApiClient geoApiClient = new GeoApiClient();
  private static final CountryClient countryClient = new CountryDbClient();
  private static final List<CountryJson> allCountry = countryClient.findAll();

  @Test
  void countriesShouldBeReturnedFromGateway() {
    final Response<RestResponsePage<CountryJson>> response = geoApiClient.getCountryPage(0, 10);
    Assertions.assertTrue(response.isSuccessful());
    Assertions.assertNotNull(response.body());
    Assertions.assertEquals(10, response.body().getSize());
  }

  @Test
  void allCountriesShouldBePaginated() {
    int size = 10;
    List<CountryJson> countries = new ArrayList<>();
    boolean last = false;
    int page = 0;
    while (!last) {
      Response<RestResponsePage<CountryJson>> response = geoApiClient.getCountryPage(page, size);
      Assertions.assertTrue(response.isSuccessful());
      Assertions.assertNotNull(response.body());
      countries.addAll(response.body().getContent());
      page++;
      last = response.body().isLast();
    }
    Assertions.assertEquals(allCountry.size(), countries.size());
  }


}
