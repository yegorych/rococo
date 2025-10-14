package guru.qa.rococo.data.repository;


import guru.qa.rococo.data.PaintingEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID>, JpaSpecificationExecutor<PaintingEntity> {
    @Nonnull
    Optional<PaintingEntity> findById(@Nonnull UUID id);

}
