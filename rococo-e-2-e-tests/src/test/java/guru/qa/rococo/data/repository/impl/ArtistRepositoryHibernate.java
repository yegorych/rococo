package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class ArtistRepositoryHibernate implements ArtistRepository {

  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = em(CFG.artistJdbcUrl());

  @NotNull
  @Override
  public ArtistEntity create(ArtistEntity artist) {
    entityManager.joinTransaction();
    entityManager.persist(artist);
    return artist;
  }

  @NotNull
  @Override
  public Optional<ArtistEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(ArtistEntity.class, id)
    );
  }

  @NotNull
  @Override
  public Optional<ArtistEntity> findByName(String name) {
//    try {
//      return Optional.of(
//              entityManager.createQuery("SELECT a FROM ArtistEntity a WHERE a.name =: name", ArtistEntity.class)
//                      .setParameter("name", name)
//                      .getResultList();
//      );
//    } catch (NoResultException e) {
//      return Optional.empty();
//    }
    List<ArtistEntity> results = entityManager
            .createQuery("SELECT a FROM ArtistEntity a WHERE a.name = :name", ArtistEntity.class)
            .setParameter("name", name)
            .getResultList();

    return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
  }

  @NotNull
  @Override
  public ArtistEntity update(ArtistEntity artist) {
    entityManager.joinTransaction();
    entityManager.merge(artist);
    return artist;
  }

  @Override
  public void remove(ArtistEntity artist) {
    entityManager.joinTransaction();
    entityManager.remove(artist);
  }
}
