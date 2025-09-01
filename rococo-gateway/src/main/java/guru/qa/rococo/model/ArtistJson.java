package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.artist.Artist;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
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
                        .setPhoto(ByteString.copyFromUtf8(photo))
                        .build();
        }
}
