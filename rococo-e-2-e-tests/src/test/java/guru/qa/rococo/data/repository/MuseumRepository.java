package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface MuseumRepository {

  @Nonnull
  static MuseumRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new MuseumRepositoryHibernate();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  MuseumEntity create(MuseumEntity museum);
  @Nonnull
  Optional<MuseumEntity> findById(UUID id);
  @Nonnull
  Optional<MuseumEntity> findByTitle(String title);
  @Nonnull
  MuseumEntity update(MuseumEntity museum);
  void remove(MuseumEntity museum);
}
