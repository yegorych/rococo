package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorJson(@JsonProperty("errors") String[] error) {
}
