package guru.qa.rococo.service.impl.api;

import guru.qa.rococo.api.AllureDockerApi;
import guru.qa.rococo.api.core.RestClient;
import guru.qa.rococo.model.allure.AllureProject;
import guru.qa.rococo.model.allure.AllureResults;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Assertions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class AllureDockerApiClient extends RestClient {

  private final AllureDockerApi allureDockerApi;

  public AllureDockerApiClient() {
    super(CFG.allureDockerUrl(), HttpLoggingInterceptor.Level.NONE);
    this.allureDockerApi = create(AllureDockerApi.class);
  }

  public void clean(String projectId) throws IOException {
    allureDockerApi.cleanResults(projectId).execute();
  }

  public void generateReport(String projectId,
                             String executionName,
                             String executionFrom,
                             String executionType) throws IOException {
    allureDockerApi.generateReport(projectId, executionName, executionFrom, executionType).execute();
  }

  public void sendResultsToAllure(String projectId, AllureResults allureResults) throws IOException {
    int code = allureDockerApi.uploadResults(
        projectId,
        allureResults
    ).execute().code();
    Assertions.assertEquals(200, code);
  }

  public void createProjectIfNotExist(String projectId) throws IOException {
    int code = allureDockerApi.project(
        projectId
    ).execute().code();
    if (code == 404) {
      code = allureDockerApi.createProject(new AllureProject(projectId)).execute().code();
      Assertions.assertEquals(201, code);
    } else {
      Assertions.assertEquals(200, code);
    }
  }
}
