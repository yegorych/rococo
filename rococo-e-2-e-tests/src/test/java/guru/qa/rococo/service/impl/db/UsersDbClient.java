package guru.qa.rococo.service.impl.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import guru.qa.rococo.data.entity.auth.Authority;
import guru.qa.rococo.data.entity.auth.AuthorityEntity;
import guru.qa.rococo.data.entity.userdata.UserEntity;
import guru.qa.rococo.data.repository.AuthUserRepository;
import guru.qa.rococo.data.repository.UserdataRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Objects;


@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  private final AuthUserRepository authUserRepository = AuthUserRepository.getInstance();
  private final UserdataRepository userdataUserRepository = UserdataRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );



    @Override
    @Step("Create user with username '{0}' using SQL INSERT")
    @Nonnull
    public UserJson createUser(String username, String password) {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                AuthUserEntity authUser = authUserEntity(username, password);
                authUserRepository.create(authUser);
                return UserJson.fromEntity(userdataUserRepository.create(userEntity(username)));
            })
        );
    }

  @Nonnull
  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    return ue;
  }

  @Nonnull
  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
        Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
        ).toList()
    );
    return authUser;
  }
}
