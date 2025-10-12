package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.grpc.rococo.geo.Country;

import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name
) {
        public static CountryJson fromGrpcMessage(Country country) {
                return new CountryJson(UUID.fromString(country.getId()), country.getName());
        }
}
