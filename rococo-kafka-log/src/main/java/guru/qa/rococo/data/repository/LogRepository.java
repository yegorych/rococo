package guru.qa.rococo.data.repository;

import guru.qa.rococo.data.LogEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LogRepository extends JpaRepository<LogEntity, UUID> {

}
