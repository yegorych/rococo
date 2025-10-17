package guru.qa.rococo.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.model.allure.AllureResults;
import guru.qa.rococo.model.allure.DecodedAllureFile;
import guru.qa.rococo.service.impl.api.AllureDockerApiClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Stream;

public class AllureDockerExtension implements SuiteExtension {

  private static final boolean inDocker = "docker".equals(System.getProperty("test.env"));
  private static final Base64.Encoder encoder = Base64.getEncoder();
  private final static Logger log = LoggerFactory.getLogger(AllureBackendLogsExtension.class);
  private static final Path allureResultsDirectory = Path.of("./rococo-e-2-e-tests/build/allure-results");
  private static final String projectId = Config.projectId;
  protected static final Config CFG = Config.getInstance();
  private static final ObjectMapper mapper = new ObjectMapper();
  private static final String[] serviceNames = {"rococo-auth", "rococo-userdata", "rococo-gateway", "rococo-artist", "rococo-museum", "rococo-geo", "rococo-painting"};

  private static final AllureDockerApiClient allureDockerApiClient = new AllureDockerApiClient();
  private boolean allureBroken = false;

  @Override
  public void beforeSuite(ExtensionContext context) {
    if (inDocker) {
      try {
        allureDockerApiClient.createProjectIfNotExist(projectId);
        allureDockerApiClient.clean(projectId);
      } catch (Throwable e) {
        allureBroken = true;
        // do nothing
      }
    }
  }

  @Override
  public void afterSuite() {
    if (inDocker && !allureBroken) {
      //способ через копирование
      for (String serviceName : serviceNames) {
        try {
          Path source = Path.of(CFG.logsDirectory(), serviceName, "app.log");
          Path target = allureResultsDirectory.resolve(serviceName + "-app.log");

          if (Files.exists(source) && Files.isReadable(source) && Files.size(source) > 0) {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Copied log for {} to allure-results: {}", serviceName, target);
          } else {
              log.warn("Log file not found or unreadable: {}", source);
          }
        } catch (Exception e) {
          log.error("Failed to copy log for {}: {}", serviceName, e.getMessage(), e);
        }
      }

      for (String serviceName : serviceNames) {
        try {
          Path logFile = allureResultsDirectory.resolve(serviceName + "-app.log");
          if (Files.exists(logFile) && Files.size(logFile) > 0) {
            String uuid = "log-" + serviceName;
            Path resultJson = allureResultsDirectory.resolve(uuid + "-result.json");

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("uuid", uuid);
            result.put("name", "Лог " + serviceName);
            result.put("status", "passed");

            Map<String, String> attachment = new LinkedHashMap<>();
            attachment.put("name", serviceName + "-app.log");
            attachment.put("type", "text/plain");
            attachment.put("source", serviceName + "-app.log");

            result.put("attachments", List.of(attachment));

            mapper.writerWithDefaultPrettyPrinter().writeValue(resultJson.toFile(), result);
            log.info("Generated result.json for log: {}", resultJson);
          }
        } catch (Exception e) {
          log.error("Failed to generate result.json for {}: {}", serviceName, e.getMessage(), e);
        }
      }

      try (Stream<Path> paths = Files.walk(allureResultsDirectory).filter(Files::isRegularFile)) {
        List<DecodedAllureFile> filesToSend = new ArrayList<>();
        for (String serviceName : serviceNames) {
          final String path = String.format(CFG.logsDirectory() + "%s/app.log", serviceName);
          try (InputStream is = Files.newInputStream(Path.of(path))) {
            final String encoded = encoder.encodeToString(is.readAllBytes());
            filesToSend.add(
                    new DecodedAllureFile(
                            serviceName + "-app.log",
                            encoded
                    )
            );
          }
        }

        for (Path allureResult : paths.toList()) {
          try (InputStream is = Files.newInputStream(allureResult)) {
            filesToSend.add(
                new DecodedAllureFile(
                    allureResult.getFileName().toString(),
                    encoder.encodeToString(is.readAllBytes())
                )
            );
          }
        }
        allureDockerApiClient.sendResultsToAllure(
            projectId,
            new AllureResults(
                filesToSend
            )
        );

        allureDockerApiClient.generateReport(
            projectId,
            System.getenv("HEAD_COMMIT_MESSAGE"),
            System.getenv("BUILD_URL"),
            System.getenv("EXECUTION_TYPE")
        );
      } catch (Throwable e) {
        // do nothing
      }
    }
  }
}
