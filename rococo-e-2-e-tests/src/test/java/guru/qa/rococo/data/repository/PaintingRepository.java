package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.PaintingRepositoryHibernate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface PaintingRepository {

  @Nonnull
  static PaintingRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new PaintingRepositoryHibernate();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  PaintingEntity create(PaintingEntity painting);
  @Nonnull
  Optional<PaintingEntity> findById(UUID id);
  @Nonnull
  Optional<PaintingEntity> findByTitle(String title);
  @Nonnull
  PaintingEntity update(PaintingEntity painting);
  void remove(PaintingEntity painting);
}
