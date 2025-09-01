package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.userdata.UserInfo;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    @Size(max = 30, message = "First name can`t be longer than 30 characters")
    String firstname,
    @JsonProperty("lastname")
    @Size(max = 50, message = "Surname can`t be longer than 50 characters")
    String lastname,
    @JsonProperty("avatar")
    @Size(max = RococoGatewayServiceConfig.ONE_MB)
    String photo) {

    public static UserJson fromGrpcMessage(UserInfo userInfo) {
        return new UserJson(
                UUID.fromString(userInfo.getId()),
                userInfo.getUsername(),
                userInfo.getFirstname(),
                userInfo.getLastname(),
                new String(userInfo.getAvatar().toByteArray(), StandardCharsets.UTF_8)
        );
    }

    public UserInfo toGrpcMessage() {
        return UserInfo.newBuilder()
                .setId(id.toString())
                .setUsername(username)
                .setFirstname(firstname)
                .setLastname(lastname)
                .setAvatar(ByteString.copyFromUtf8(photo))
                .build();
    }
}
