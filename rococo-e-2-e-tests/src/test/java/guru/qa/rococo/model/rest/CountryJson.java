package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.geo.Country;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.model.CountryEnum;

import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        CountryEnum name
) {
        public static CountryJson fromGrpcMessage(Country country) {
                return new CountryJson(UUID.fromString(country.getId()), CountryEnum.fromName(country.getName()));
        }

        public static CountryJson fromEntity(CountryEntity entity) {
                return new CountryJson(entity.getId(), CountryEnum.fromName(entity.getName()));
        }
}
