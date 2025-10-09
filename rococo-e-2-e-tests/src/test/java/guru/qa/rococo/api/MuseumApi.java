package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.MuseumJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;

public interface MuseumApi {

    @GET("/api/museum")
    Call<RestResponsePage<MuseumJson>> getMuseumPage(@Query("title") @Nullable String title,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("/api/museum/{id}")
    Call<MuseumJson> getMuseumById(
            @Path("id") String id
    );

    @POST("/api/museum")
    Call<MuseumJson> createMuseum(
            @Header("Authorization") String token,
            @Body MuseumJson body
    );

    @PATCH("/api/museum")
    Call<MuseumJson> updateMuseum(
            @Header("Authorization") String token,
            @Body MuseumJson body
    );
}
