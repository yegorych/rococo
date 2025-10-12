package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;

public interface ArtistApi {

    @GET("/api/artist")
    Call<RestResponsePage<ArtistJson>> getArtistsPage(@Query("name") @Nullable String name,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("/api/artist/{id}")
    Call<ArtistJson> getArtistById(
            @Path("id") String id
    );

    @POST("/api/artist")
    Call<ArtistJson> createArtist(
            @Header("Authorization") String token,
            @Body ArtistJson body
    );

    @PATCH("/api/artist")
    Call<ArtistJson> updateArtist(
            @Header("Authorization") String token,
            @Body ArtistJson body
    );
}
