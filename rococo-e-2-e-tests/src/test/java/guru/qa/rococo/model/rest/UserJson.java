package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.userdata.UserInfo;
import guru.qa.rococo.data.entity.userdata.UserEntity;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("lastname")
    String lastname,
    @JsonProperty("avatar")
    String photo,
    @JsonIgnore
    String password) {

    public UserJson(String username) {
        this(null, username, null, null, null, null);
    }

    public static UserJson fromEntity(UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
                null
        );
    }

    public static UserJson fromGrpcMessage(UserInfo userInfo) {
        return new UserJson(
                UUID.fromString(userInfo.getId()),
                userInfo.getUsername(),
                userInfo.getFirstname(),
                userInfo.getLastname(),
                new String(userInfo.getAvatar().toByteArray(), StandardCharsets.UTF_8),
                null
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

    public UserJson withPassword(String password) {
        return new UserJson(id, username, firstname, lastname, photo, password);

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserJson userJson = (UserJson) o;
        return Objects.equals(id, userJson.id) && Objects.equals(photo, userJson.photo) && Objects.equals(username, userJson.username) && Objects.equals(lastname, userJson.lastname) && Objects.equals(firstname, userJson.firstname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstname, lastname, photo);
    }
}
