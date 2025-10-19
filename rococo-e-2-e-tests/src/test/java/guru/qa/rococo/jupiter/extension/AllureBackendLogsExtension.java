package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.config.Config;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AllureBackendLogsExtension implements SuiteExtension{
    protected static final Config CFG = Config.getInstance();
    private final static Logger log = LoggerFactory.getLogger(AllureBackendLogsExtension.class);
    public static final String caseName = "Rococo backend logs";
    String[] serviceNames = {"rococo-auth", "rococo-userdata", "rococo-gateway", "rococo-artist", "rococo-museum", "rococo-geo", "rococo-painting"};

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
        allureLifecycle.startTestCase(caseId);
        addAttachmentLogs(allureLifecycle, serviceNames);
        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    private static void addAttachmentLogs(AllureLifecycle allureLifecycle, String... serviceNames) {
        for (String serviceName : serviceNames) {
            try {
                allureLifecycle.addAttachment(
                        serviceName + " log",
                        "text/html",
                        ".log",
                        Files.newInputStream(
                                Path.of(String.format(CFG.logsDirectory() + "%s/app.log", serviceName))
                        )
                );
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
    }
}
