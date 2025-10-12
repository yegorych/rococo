package guru.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.Date;

public record SessionJson(@JsonProperty("username")
                          String username,
                          @JsonProperty("issuedAt")
                          Date issuedAt,
                          @JsonProperty("expiresAt")
                          Date expiresAt) {
  public static @Nonnull SessionJson empty() {
    return new SessionJson(null, null, null);
  }
}
