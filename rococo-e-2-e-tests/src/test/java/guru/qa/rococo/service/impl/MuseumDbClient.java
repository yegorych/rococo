package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.geo.CountryEntity;
import guru.qa.rococo.data.entity.museum.MuseumEntity;
import guru.qa.rococo.data.repository.GeoRepository;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.MuseumClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;


@ParametersAreNonnullByDefault
public class MuseumDbClient implements MuseumClient {

  private static final Config CFG = Config.getInstance();
  private final MuseumRepository museumRepository = MuseumRepository.getInstance();
  private final GeoRepository geoRepository = GeoRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.geoJdbcUrl(),
      CFG.museumJdbcUrl()
  );


    @NotNull
    @Override
    @Step("Create museum using SQL INSERT")
    public MuseumJson createMuseum(MuseumJson museum) {
        MuseumEntity museumEntity = MuseumEntity.fromJson(museum);
        return Objects.requireNonNull(xaTransactionTemplate.execute(() -> {
                    CountryJson countryJson = museum.geo().country();
                    if (museumEntity.getCountryId() == null && countryJson.name().getCountryName() != null) {
                        CountryEntity country = geoRepository.findByName(
                                countryJson.name().getCountryName())
                                .orElseThrow();
                        museumEntity.setCountryId(country.getId());
                    }
                    return MuseumJson.fromEntity(museumRepository.create(museumEntity)).addCountry(countryJson);
                })
        );
    }


    @Nullable
    @Override
    @Step("Find museum with title {0} using SQL SELECT")
    public MuseumJson findMuseumByTitle(String title) {
        return xaTransactionTemplate.execute(()->
                museumRepository.findByTitle(title)
                        .map(MuseumJson::fromEntity)
                        .map(mj -> mj.addCountry(
                                CountryJson.fromEntity(
                                        geoRepository.findById(mj.geo().country().id()).orElseThrow()
                                )
                            )
                        )
                        .orElse(null)
        );
    }



}
