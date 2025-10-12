package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class GeoRepositoryHibernate implements GeoRepository {

  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = em(CFG.geoJdbcUrl());

  @NotNull
  @Override
  public Optional<CountryEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(CountryEntity.class, id)
    );
  }

  @Override
  public List<CountryEntity> findAll() {
    return entityManager.createQuery("SELECT c FROM CountryEntity c", CountryEntity.class)
            .getResultList();

  }

  @NotNull
  @Override
  public Optional<CountryEntity> findByName(String name) {
    try {
      return Optional.of(
              entityManager.createQuery("SELECT c FROM CountryEntity c WHERE c.name =: name", CountryEntity.class)
                      .setParameter("name", name)
                      .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }


}
