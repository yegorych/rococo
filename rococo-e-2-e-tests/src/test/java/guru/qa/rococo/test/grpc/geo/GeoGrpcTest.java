package guru.qa.rococo.test.grpc.geo;

import guru.qa.grpc.rococo.geo.CountriesRequest;
import guru.qa.grpc.rococo.geo.CountriesResponse;
import guru.qa.grpc.rococo.geo.Country;
import guru.qa.grpc.rococo.geo.IdRequest;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.service.CountryClient;
import guru.qa.rococo.service.impl.db.CountryDbClient;
import guru.qa.rococo.test.grpc.BaseGrpcTest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@GrpcTest
public class GeoGrpcTest extends BaseGrpcTest {

    private static final CountryClient countryClient = new CountryDbClient();
    private static final List<CountryJson> allCountry = countryClient.findAll();

    @Test
    void allCountriesShouldBeReturned() {
        int expectedNumberOfCountries = allCountry.size();

        CountriesResponse response = geoStub.getCountries(
                CountriesRequest.newBuilder()
                        .setSize(expectedNumberOfCountries)
                        .setPage(0)
                        .build()
        );
        int actualNumberOfCountriesFromTotalCount = response.getTotalCount();
        int actualNumberOfCountries = response.getCountriesList().size();
        Assertions.assertEquals(expectedNumberOfCountries, actualNumberOfCountriesFromTotalCount);
        Assertions.assertEquals(expectedNumberOfCountries, actualNumberOfCountries);
    }

    @Test
    void countriesShouldBePaginated() {
        int size = 17;
        CountriesResponse response = geoStub.getCountries(
                CountriesRequest.newBuilder()
                        .setSize(size)
                        .setPage(3)
                        .build()
        );
        Assertions.assertEquals(allCountry.size(), response.getTotalCount());
        Assertions.assertEquals(size, response.getCountriesList().size());
    }

    @Test
    void countriesShouldNotBeReturnedForEmptyRequest() {
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                ()-> geoStub.getCountries(CountriesRequest.newBuilder().build())
        );
        Assertions.assertEquals("INTERNAL", ex.getStatus().getCode().toString());
    }

    @Test
    void countriesShouldNotBeReturnedWhenSizeIsInvalid() {
        CountriesRequest request = CountriesRequest.newBuilder()
                .setSize(-1)
                .setPage(0)
                .build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                ()-> geoStub.getCountries(request)
        );
        Assertions.assertEquals("INTERNAL", ex.getStatus().getCode().toString());
    }

    @Test
    void countriesShouldNotBeReturnedWhenPageIsInvalid() {
        CountriesRequest request = CountriesRequest.newBuilder()
                .setSize(0)
                .setPage(-1)
                .build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                ()-> geoStub.getCountries(request)
        );
        Assertions.assertEquals("INTERNAL", ex.getStatus().getCode().toString());
    }

    @Test
    void countryByIdShouldBeReturned() {
        CountryJson countryJson = allCountry.get(new Random().nextInt(allCountry.size()));
        Country response = geoStub.getCounty(
                IdRequest.newBuilder()
                        .setId(countryJson.id().toString())
                        .build()
        );
        Assertions.assertEquals(countryJson.name().getCountryName(), response.getName());
    }

    @Test
    void countryByRandomIdShouldNotBeReturned() {
        String id = UUID.randomUUID().toString();
        IdRequest request = IdRequest.newBuilder()
                        .setId(id)
                        .build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                ()-> geoStub.getCounty(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
        Assertions.assertEquals("Страна с ID " + id + " не найдена", ex.getStatus().getDescription());
    }



}
