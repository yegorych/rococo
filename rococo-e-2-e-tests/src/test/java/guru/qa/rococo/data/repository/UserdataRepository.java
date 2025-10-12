package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.repository.impl.UserdataRepositoryHibernate;
import guru.qa.rococo.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataRepository {

  @Nonnull
  static UserdataRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new UserdataRepositoryHibernate();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  UserEntity create(UserEntity user);
  @Nonnull
  Optional<UserEntity> findById(UUID id);
  @Nonnull
  Optional<UserEntity> findByUsername(String username);
  @Nonnull
  UserEntity update(UserEntity user);
  void remove(UserEntity user);
}
