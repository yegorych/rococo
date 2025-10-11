package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import guru.qa.grpc.rococo.museum.Museum;
import guru.qa.rococo.config.RococoGatewayServiceConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @NotBlank(message = "Название не может быть пустым или состоять только из пробелов")
        @Size(max = 255, message = "Название не может быть длиннее 255 символов")
        String title,
        @JsonProperty("description")
        @Size(max = 1000, message = "Описание не может быть длиннее 1000 символов")
        String description,
        @JsonProperty("geo")
        GeoJson geo,
        @JsonProperty("photo")
        @Size(max = RococoGatewayServiceConfig.FOUR_MB, message = "Размер фото не может превышать 4 MB")
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
                        .setPhoto(photo != null ? ByteString.copyFromUtf8(photo) : ByteString.empty())
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
