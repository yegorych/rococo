package guru.qa.rococo.data.jpa;

import guru.qa.rococo.data.jdbc.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



@ParametersAreNonnullByDefault
public class EntityManagers {
  private EntityManagers() {
  }

  private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

  @SuppressWarnings("resource")
  @Nonnull
  public static EntityManager em(String jdbcUrl) {
    return new ThreadSafeEntityManager(
        emfs.computeIfAbsent(
            jdbcUrl,
            key -> {
              DataSources.dataSource(jdbcUrl);
              final String persistenceUnitName = StringUtils.substringAfter(jdbcUrl, "3306/");
              return Persistence.createEntityManagerFactory(persistenceUnitName);
            }
        ).createEntityManager()
    );
  }
}
