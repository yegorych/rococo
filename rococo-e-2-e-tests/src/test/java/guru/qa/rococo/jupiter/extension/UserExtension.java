package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.UsersClient;
import guru.qa.rococo.service.impl.UsersDbClient;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.lang.reflect.Method;

import static guru.qa.rococo.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class UserExtension implements
    BeforeEachCallback,
    ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    private static final String defaultPassword = "12345";
    private final UsersClient usersClient = new UsersDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
      Method testMethod = context.getRequiredTestMethod();
      AnnotationSupport.findAnnotation(testMethod, User.class)
              .or(() -> AnnotationSupport.findAnnotation(testMethod, ApiLogin.class).map(ApiLogin::user))
              .ifPresent(userAnno -> {
                  if ("".equals(userAnno.username())) {
                      final String username = RandomDataUtils.randomUsername();

                      UserJson user = usersClient.createUser(
                              username,
                              defaultPassword
                      );

                      setUser(
                              user.withPassword(
                                      defaultPassword
                              )
                      );
                  }
              });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return createdUser();
  }

  @Nullable
  public static UserJson createdUser() {
    final ExtensionContext context = context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }


  public static void setUser(UserJson testUser) {
    final ExtensionContext context = context();
    context.getStore(NAMESPACE).put(
        context.getUniqueId(),
        testUser
    );
  }
}
