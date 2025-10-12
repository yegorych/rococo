package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.repository.impl.GeoRepositoryHibernate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface GeoRepository {

  @Nonnull
  static GeoRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new GeoRepositoryHibernate();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  Optional<CountryEntity> findById(UUID id);
  List<CountryEntity> findAll();
  @Nonnull
  Optional<CountryEntity> findByName(String name);

}
