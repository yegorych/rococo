package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.auth.AuthUserEntity;
import guru.qa.rococo.data.repository.impl.AuthUserRepositoryHibernate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {
  @Nonnull
  static AuthUserRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new AuthUserRepositoryHibernate();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  AuthUserEntity create(AuthUserEntity user);
  @Nonnull
  AuthUserEntity update(AuthUserEntity user);
  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);
  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);
  void remove(AuthUserEntity user);
}
