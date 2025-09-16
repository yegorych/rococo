package guru.qa.rococo.service.impl;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.artist.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.ArtistClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;


@ParametersAreNonnullByDefault
public class ArtistDbClient implements ArtistClient {

  private static final Config CFG = Config.getInstance();
  private final ArtistRepository artistRepository = ArtistRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.artistJdbcUrl()
  );

    @NotNull
    @Override
    @Step("Create artist using SQL INSERT")
    public ArtistJson createArtist(ArtistJson artist) {
        return Objects.requireNonNull(xaTransactionTemplate.execute(() ->
                ArtistJson.fromEntity(artistRepository.create(ArtistEntity.fromJson(artist))))
        );
    }

  @Nullable
  @Override
  @Step("Find artist with name {0} using SQL SELECT")
  public ArtistJson findArtistByName(String name) {
    return xaTransactionTemplate.execute(()->
            artistRepository.findByName(name)
                    .map(ArtistJson::fromEntity)
                    .orElse(null)
    );
  }


}
