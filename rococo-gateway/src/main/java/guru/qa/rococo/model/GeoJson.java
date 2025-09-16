package guru.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record GeoJson(
        @JsonProperty("city")
        @Size(max = 255, message = "Название города не может быть длиннее 255 символов")
        String city,
        @JsonProperty("country")
        CountryJson country
) {
}

