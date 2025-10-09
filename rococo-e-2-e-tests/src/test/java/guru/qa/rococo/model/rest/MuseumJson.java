package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.museum.Museum;
import guru.qa.rococo.condition.MuseumConditions.MuseumFront;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.model.CountryEnum;
import guru.qa.rococo.utils.RandomDataUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("geo")
        GeoJson geo,
        @JsonProperty("photo")
        String photo
) {

        public MuseumJson(UUID id){
                this(id, null, null, null, null);
        }

        public static MuseumJson fromGrpcMessage(Museum museum) {
                return new MuseumJson(
                        UUID.fromString(museum.getId()),
                        museum.getTitle(),
                        museum.getDescription(),
                        new GeoJson(
                                museum.getCity(),
                                new CountryJson(
                                        UUID.fromString(museum.getCountryId()),
                                        null
                                )
                        ),
                        new String(museum.getPhoto().toByteArray(), StandardCharsets.UTF_8)
                );

        }

        public Museum toGrpcMessage(){
                return Museum.newBuilder()
                        .setId(id != null ? id.toString() : "")
                        .setTitle(title)
                        .setDescription(description)
                        .setCity(geo().city())
                        .setCountryId(geo().country().id().toString())
                        .setPhoto(ByteString.copyFromUtf8(photo))
                        .build();
        }

        public MuseumFront toMuseumFront(){
                return new MuseumFront(
                        title,
                        description,
                        geo.city(),
                        geo.country().name() != null ? geo.country().name().getCountryName() : ""
                );
        }

        public MuseumJson addCountry(CountryJson country) {
                return new MuseumJson(
                        id,
                        title,
                        description,
                        new GeoJson(geo.city(), country),
                        photo
                );
        }

        public static MuseumJson fromEntity(MuseumEntity entity){
                return new MuseumJson(
                        entity.getId(),
                        entity.getTitle(),
                        entity.getDescription(),
                        new GeoJson(
                                entity.getCity(),
                                new CountryJson(
                                        entity.getCountryId(),
                                        null
                                )
                        ),
                        entity.getPhoto() != null ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : ""
                );
        }

        public static MuseumJson randomMuseum() {
                return new MuseumJson(
                        null,
                        RandomDataUtils.randomMuseumTitle(),
                        RandomDataUtils.randomSentence(30),
                        new GeoJson(
                                RandomDataUtils.randomCity(),
                                new CountryJson(null, CountryEnum.randomCountry())
                        ),
                        null
                );
        }

        public static MuseumJson emptyMuseum(){
                return new MuseumJson(
                        null,
                        "",
                        "",
                        new GeoJson("", new CountryJson(null, null)),
                        null
                );
        }


}
