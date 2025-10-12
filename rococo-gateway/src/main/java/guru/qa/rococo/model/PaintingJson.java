package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @NotBlank(message = "Название не может быть пустым или состоять только из пробелов")
        @Size(max = 255, message = "Название не может быть длиннее 255 символов")
        String title,
        @JsonProperty("description")
        @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
        String description,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        @NotNull(message = "Художник должен быть задан")
        ArtistJson artist,
        @JsonProperty("content")
        @Size(max = RococoGatewayServiceConfig.FOUR_MB, message = "Размер фото не может превышать 4 MB")
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
                        .setContent(content != null ? ByteString.copyFromUtf8(content) : ByteString.empty())
                        .build();
        }

        public PaintingJson addArtist(ArtistJson artistJson) {
                return new PaintingJson(id, title, description, museum, artistJson, content);
        }

        public PaintingJson addMuseum(MuseumJson museumJson) {
                return new PaintingJson(id, title, description, museumJson, artist, content);
        }

        @JsonIgnore
        public Optional<String> getArtistId() {
                return artist() != null && artist().id() != null
                        ? Optional.of(artist().id().toString())
                        : Optional.empty();
        }

        @JsonIgnore
        public Optional<String> getMuseumId() {
                return museum() != null && museum().id() != null
                        ? Optional.of(museum().id().toString())
                        : Optional.empty();
        }




}
