package guru.qa.rococo.data.repository;


import guru.qa.rococo.data.MusuemEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MusuemEntity, UUID>{
    @Nonnull
    Optional<MusuemEntity> findById(@Nonnull UUID id);

    @Nonnull
    Page<MusuemEntity> findAll(
            @Nonnull Pageable pageable
    );

}
