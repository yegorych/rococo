package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @Size(max = 255, message = "Title can`t be longer than 255 characters")
        String title,
        @JsonProperty("description")
        @Size(max = 1000, message = "Description can`t be longer than 1000 characters")
        String description,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        ArtistJson artist,
        @JsonProperty("content")
        //@Size(max = RococoGatewayServiceConfig.ONE_MB)
        String content
) {
        public static PaintingJson fromGrpcMessage(Painting painting) {
                return new PaintingJson(
                        UUID.fromString(painting.getId()),
                        painting.getTitle(),
                        painting.getDescription(),
                        new MuseumJson(
                                !painting.getMuseumId().isEmpty() ? UUID.fromString(painting.getMuseumId()) : null,
                                null,
                                null,
                                null,
                                null),
                        new ArtistJson(UUID.fromString(painting.getArtistId()), null, null, null),
                        new String(painting.getContent().toByteArray(), StandardCharsets.UTF_8)
                );
        }

        public Painting toGrpcMessage() {
                return Painting.newBuilder()
                        .setId(id != null ? id.toString() : "")
                        .setTitle(title)
                        .setDescription(description)
                        .setMuseumId(museum.id() != null ? museum.id().toString() : "")
                        .setArtistId(artist.id() != null ? artist.id().toString() : "")
                        .setContent(ByteString.copyFromUtf8(content))
                        .build();
        }

        public PaintingJson addArtist(ArtistJson artistJson) {
                return new PaintingJson(id, title, description, museum, artistJson, content);
        }


}
