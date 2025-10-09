package guru.qa.rococo.service.impl.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.service.CountryClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@ParametersAreNonnullByDefault
public class CountryDbClient implements CountryClient {

  private static final Config CFG = Config.getInstance();
  private final GeoRepository geoRepository = GeoRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.geoJdbcUrl()
  );


  @NotNull
  @Override
  @Step("Find all country using SQL SELECT")
  public List<CountryJson> findAll() {
    return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
            geoRepository.findAll()
                    .stream()
                    .map(CountryJson::fromEntity)
                    .toList()
    ));
  }

  @Nullable
  @Override
  @Step("Find country with id {0} using SQL SELECT")
  public CountryJson findById(String id) {
    return xaTransactionTemplate.execute(()->
            geoRepository.findById(UUID.fromString(id))
                    .map(CountryJson::fromEntity)
                    .orElse(null)
    );
  }
}
