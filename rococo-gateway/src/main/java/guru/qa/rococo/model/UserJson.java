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
    @Size(min = 3, max = 50, message = "Допустимая длина имени пользователя — от 3 до 50 символов")
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    @Size(max = 255, message = "Имя не может быть длиннее 255 символов")
    String firstname,
    @JsonProperty("lastname")
    @Size(max = 255, message = "Фамилия не может быть длиннее 255 символов")
    String lastname,
    @JsonProperty("avatar")
    @Size(max = RococoGatewayServiceConfig.ONE_MB, message = "Фото профиля не может превышать 1 MB")
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

    @Override
    public String toString() {
        return "UserJson{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", photo='" + (photo != null ? photo.substring(0, Math.min(photo.length(), 10)) + "..." : "null") + '\'' +
                '}';
    }
}
