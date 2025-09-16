package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.impl.ArtistRepositoryHibernate;
import guru.qa.rococo.data.repository.impl.MuseumRepositoryHibernate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface ArtistRepository {

  @Nonnull
  static ArtistRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new ArtistRepositoryHibernate();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  ArtistEntity create(ArtistEntity artist);
  @Nonnull
  Optional<ArtistEntity> findById(UUID id);
  @Nonnull
  Optional<ArtistEntity> findByName(String name);
  @Nonnull
  ArtistEntity update(ArtistEntity artist);
  void remove(ArtistEntity artist);
}
