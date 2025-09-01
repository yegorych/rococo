package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.museum.Museum;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @Size(max = 255, message = "Title can`t be longer than 255 characters")
        String title,
        @JsonProperty("description")
        @Size(max = 1000, message = "Description can`t be longer than 1000 characters")
        String description,
        @JsonProperty("geo")
        GeoJson geo,
        @JsonProperty("photo")
        @Size(max = RococoGatewayServiceConfig.ONE_MB)
        String photo
) {

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

        public MuseumJson addCountry(CountryJson country) {
                return new MuseumJson(
                        id,
                        title,
                        description,
                        new GeoJson(geo.city(), country),
                        photo
                );
        }
}
