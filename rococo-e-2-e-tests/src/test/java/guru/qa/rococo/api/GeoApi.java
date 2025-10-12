package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.CountryJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeoApi {

    @GET("/api/country")
    Call<RestResponsePage<CountryJson>> getCountries(@Query("page") int page, @Query("size") int size);


}
