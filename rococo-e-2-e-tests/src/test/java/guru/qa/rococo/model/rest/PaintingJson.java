package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.rococo.condition.PaintingConditions;
import guru.qa.rococo.data.entity.painting.PaintingEntity;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.utils.RandomDataUtils.randomName;
import static guru.qa.rococo.utils.RandomDataUtils.randomSentence;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        ArtistJson artist,
        @JsonProperty("content")
        String content
) {
        public static PaintingJson fromEntity(PaintingEntity entity){
                return new PaintingJson(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getDescription(),
                        new MuseumJson(
                                entity.getMuseumId(),
                                null,
                                null,
                                null,
                                null
                        ),
                        new ArtistJson(
                                entity.getArtistId(),
                                null,
                                null,
                                null
                        ),
                        entity.getContent() != null ? new String(entity.getContent(), StandardCharsets.UTF_8) : ""
                );
        }

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
                        .setMuseumId(museum != null && museum.id() != null ? museum.id().toString() : "")
                        .setArtistId(artist != null && artist.id() != null ? artist.id().toString() : "")
                        .setContent(ByteString.copyFromUtf8(content))
                        .build();
        }

        public PaintingJson addArtist(ArtistJson artistJson) {
                return new PaintingJson(id, title, description, museum, artistJson, content);
        }

        public PaintingJson addMuseum(MuseumJson museumJson) {
                return new PaintingJson(id, title, description, museumJson, artist, content);
        }

        public PaintingJson addId(UUID id) {
                return new PaintingJson(id, title, description, museum, artist, content);
        }


        @Nonnull
        public static PaintingJson randomPainting() {
                return new PaintingJson(
                        null,
                        randomName(),
                        randomSentence(20),
                        MuseumJson.randomMuseum(),
                        ArtistJson.randomArtist(),
                        null
                );
        }

        public PaintingConditions.PaintingFront toPaintingFront() {
                return new PaintingConditions.PaintingFront(
                       title,
                       description,
                       artist.name(),
                       museum!= null ? museum.title() : ""
                );
        }

        public static PaintingJson emptyPainting() {
                return new PaintingJson(
                        null,
                        "",
                        "",
                        MuseumJson.emptyMuseum(),
                        ArtistJson.emptyArtist(),
                        null
                );
        }




}
