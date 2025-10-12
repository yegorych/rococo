package guru.qa.rococo.test.rest.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.SessionJson;
import guru.qa.rococo.service.impl.api.SessionApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import java.util.Base64;
import java.util.Map;

import static guru.qa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static guru.qa.rococo.utils.TokenDecoder.decodeBearerToken;

@RestTest
public class SessionRestTest {
    @RegisterExtension
    private static final ApiLoginExtension extension = rest();
    private final static SessionApiClient sessionClient = new SessionApiClient();

    @Test
    void emptySessionShouldBeReturnedForInvalidToken(){
        Response<SessionJson> response = sessionClient.getSession("fake");
        SessionJson body = response.body();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(200, response.code());
        Assertions.assertAll(
                () -> Assertions.assertNull(body.username()),
                () -> Assertions.assertNull(body.expiresAt()),
                () -> Assertions.assertNull(body.issuedAt())
        );
    }

    @Test
    @ApiLogin
    void sessionShouldBeReturned(@Token String token){
        Response<SessionJson> response = sessionClient.getSession(token);
        SessionJson body = response.body();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(200, response.code());
        Assertions.assertEquals(decodeBearerToken(token).get("sub"), body.username());
    }


}
