package guru.qa.rococo.api;

import guru.qa.rococo.model.rest.UserJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;

public interface UserApi {

    @GET("/api/user")
    Call<UserJson> currentUser(@Header("Authorization") String token);

    @PATCH("/api/user")
    Call<UserJson> updateUser(
            @Header("Authorization") String token,
            @Body UserJson user
    );

}
