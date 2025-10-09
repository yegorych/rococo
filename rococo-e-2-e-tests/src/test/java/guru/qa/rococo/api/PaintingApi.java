package guru.qa.rococo.api;

import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.PaintingJson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;

public interface PaintingApi {

    @GET("/api/painting")
    Call<RestResponsePage<PaintingJson>> getPaintingPage(@Query("title") @Nullable String title,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("/api/painting/{id}")
    Call<PaintingJson> getPaintingById(
            @Path("id") String id
    );

    @POST("/api/painting")
    Call<PaintingJson> createPainting(
            @Header("Authorization") String token,
            @Body PaintingJson body
    );

    @PATCH("/api/painting")
    Call<PaintingJson> updatePainting(
            @Header("Authorization") String token,
            @Body PaintingJson body
    );

    @GET("/api/painting/author/{id}")
    Call<RestResponsePage<PaintingJson>> getByArtistId(@Path("id") String id,
                                     @Query("page") int page,
                                     @Query("size") int size
    );
}
