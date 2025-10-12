package guru.qa.rococo.service.impl.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rococo.api.GithubApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.service.GithubClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GithubApiClient extends RestClient implements GithubClient {

  private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

  private final GithubApi ghApi;

  public GithubApiClient() {
    super(CFG.ghUrl());
    this.ghApi = create(GithubApi.class);
  }

  @Override
  @Step("Get state from GHA api for issue '{0}'")
  @Nonnull
  public String issueState(String issueNumber) {
    final Response<JsonNode> response;
    try {
      response = ghApi.issue(
          "Bearer " + System.getenv(GH_TOKEN_ENV),
          issueNumber
      ).execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return Objects.requireNonNull(response.body()).get("state").asText();
  }
}
