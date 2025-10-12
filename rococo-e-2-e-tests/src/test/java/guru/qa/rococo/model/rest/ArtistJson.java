package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.artist.Artist;
import guru.qa.rococo.condition.ArtistConditions;
import guru.qa.rococo.data.entity.artist.ArtistEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static guru.qa.rococo.utils.RandomDataUtils.randomName;
import static guru.qa.rococo.utils.RandomDataUtils.randomSentence;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo,
        @JsonIgnore
        List<PaintingJson> paintings
) {

        public ArtistJson(UUID id){
                this(id, null, null, null, new ArrayList<>());
        }

        public ArtistJson(UUID id, String name, String biography, String photo) {
                this(id, name, biography, photo, new ArrayList<>());
        }

        public static ArtistJson fromArtistAnno(guru.qa.rococo.jupiter.annotation.Artist artist) {
                return new ArtistJson(null,
                        artist.name().isEmpty()
                                ? randomName()
                                : artist.name(),
                        artist.biography().isEmpty()
                                ? randomSentence(50)
                                : artist.biography(),
                        null,
                        new ArrayList<>());
        }

        public static ArtistJson fromGrpcMessage(Artist artist) {
                return new ArtistJson(
                        UUID.fromString(artist.getId()),
                        artist.getName(),
                        artist.getBiography(),
                        new String(artist.getPhoto().toByteArray(), StandardCharsets.UTF_8),
                        null
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

        public ArtistJson addId(UUID id) {
                return new ArtistJson(id, name, biography, photo, new ArrayList<>());
        }

        public static ArtistJson fromEntity(ArtistEntity entity){
                return new ArtistJson(
                        entity.getId(),
                        entity.getName(),
                        entity.getBiography(),
                        entity.getPhoto() != null ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : ""
                );
        }

        public ArtistConditions.ArtistFront toArtistFront() {
                return new ArtistConditions.ArtistFront(
                        name,
                        biography,
                        paintings.stream().map(PaintingJson::title).toList()
                );
        }

        public static ArtistJson emptyArtist() {
                return new ArtistJson(null,
                        "",
                        "",
                        null,
                        new ArrayList<>()
                );
        }

        public static ArtistJson randomArtist() {
                return new ArtistJson(
                        null,
                        randomName(),
                        randomSentence(20),
                        ""
                );
        }

        @Override
        public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                ArtistJson that = (ArtistJson) o;
                return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(photo, that.photo) && Objects.equals(biography, that.biography);
        }

        @Override
        public int hashCode() {
                return Objects.hash(id, name, biography, photo);
        }
}
