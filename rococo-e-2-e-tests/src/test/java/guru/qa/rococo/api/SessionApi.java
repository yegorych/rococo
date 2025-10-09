package guru.qa.rococo.api;

import guru.qa.rococo.model.rest.SessionJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SessionApi {

    @GET("/api/session")
    Call<SessionJson> session(@Header("Authorization") String token);

}
