package guru.qa.rococo.data.repository.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.repository.PaintingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.rococo.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class PaintingRepositoryHibernate implements PaintingRepository {

  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = em(CFG.paintingJdbcUrl());

  @NotNull
  @Override
  public PaintingEntity create(PaintingEntity painting) {
    entityManager.joinTransaction();
    entityManager.persist(painting);
    return painting;
  }

  @NotNull
  @Override
  public Optional<PaintingEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(PaintingEntity.class, id)
    );
  }

  @NotNull
  @Override
  public Optional<PaintingEntity> findByTitle(String title) {
    try {
      return Optional.of(
              entityManager.createQuery("SELECT p FROM PaintingEntity p WHERE p.title =: title", PaintingEntity.class)
                      .setParameter("title", title)
                      .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @NotNull
  @Override
  public PaintingEntity update(PaintingEntity painting) {
    entityManager.joinTransaction();
    entityManager.merge(painting);
    return painting;
  }

  @Override
  public void remove(PaintingEntity painting) {
    entityManager.joinTransaction();
    entityManager.remove(painting);
  }
}
