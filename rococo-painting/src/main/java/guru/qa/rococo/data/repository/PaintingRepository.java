package guru.qa.rococo.data.repository;


import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID>, JpaSpecificationExecutor<PaintingEntity> {
    @Nonnull
    Optional<PaintingEntity> findById(@Nonnull UUID id);

//    @Nonnull
//    List<PaintingEntity> findAllByTitleContainsIgnoreCase(
//            @Nonnull String title,@Nonnull Pageable pageable
//    );

//    @Nonnull
//    Page<PaintingEntity> findAll(
//            @Nonnull Pageable pageable
//    );

//    @Nonnull
//    Page<PaintingEntity> findAllByArtistId(@Nonnull UUID artistId, @Nonnull Pageable pageable);
//
//    @Nonnull
//    Page<PaintingEntity> findAllByArtistIdAndTitleContainingIgnoreCase(@Nonnull UUID uuid,
//                                                                       @Nonnull String title,
//                                                                       Pageable pageable);
}
