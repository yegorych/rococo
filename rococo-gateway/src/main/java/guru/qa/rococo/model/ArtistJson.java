package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.artist.Artist;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        @NotBlank(message = "Имя не может быть пустым или состоять только из пробелов")
        @Size(max = 255, message = "Имя не может быть длиннее 255 символов")
        String name,
        @Size(max = 2000, message = "Биография не может быть длиннее 2000 символов")
        @JsonProperty("biography")
        String biography,
        @Size(max = RococoGatewayServiceConfig.FOUR_MB, message = "Размер фото не может превышать 4 MB")
        @JsonProperty("photo")
        String photo
) {
        public static ArtistJson fromGrpcMessage(Artist artist) {
                return new ArtistJson(
                        UUID.fromString(artist.getId()),
                        artist.getName(),
                        artist.getBiography(),
                        new String(artist.getPhoto().toByteArray(), StandardCharsets.UTF_8)
                );
        }

        public Artist toGrpcMessage() {
                return Artist.newBuilder()
                        .setId(id != null ? id.toString() : "")
                        .setName(name)
                        .setBiography(biography)
                        .setPhoto(photo != null ? ByteString.copyFromUtf8(photo) : ByteString.empty())
                        .build();
        }
}
