package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.DisabledByIssue;
import guru.qa.rococo.service.GithubClient;
import guru.qa.rococo.service.impl.api.GithubApiClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.platform.commons.support.SearchOption;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.Optional;

@ParametersAreNonnullByDefault
public class IssueExtension implements ExecutionCondition {

  private final GithubClient ghApiClient = new GithubApiClient();

  @SneakyThrows
  @Nonnull
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    final Optional<Method> method = context.getTestMethod();
    final Class<?> clazz = context.getRequiredTestClass();
    final Optional<DisabledByIssue> annotation;
    if (method.isPresent()) {
      annotation = AnnotationSupport.findAnnotation(
          method.get(),
          DisabledByIssue.class
      );
    } else {
      annotation = AnnotationSupport.findAnnotation(
          clazz,
          DisabledByIssue.class,
          SearchOption.INCLUDE_ENCLOSING_CLASSES
      );
    }

    return annotation.map(
        byIssue -> "open".equals(ghApiClient.issueState(byIssue.value()))
            ? ConditionEvaluationResult.disabled("Disabled by issue #" + byIssue.value())
            : ConditionEvaluationResult.enabled("Issue closed")
    ).orElseGet(
        () -> ConditionEvaluationResult.enabled("Annotation @DisabledByIssue not found")
    );
  }
}
