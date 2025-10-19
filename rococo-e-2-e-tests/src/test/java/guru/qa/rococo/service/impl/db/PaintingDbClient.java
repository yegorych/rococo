package guru.qa.rococo.service.impl.db;

import guru.qa.rococo.config.Config;
import guru.qa.rococo.data.entity.painting.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.data.tpl.XaTransactionTemplate;
import guru.qa.rococo.model.rest.PaintingJson;
import guru.qa.rococo.service.ArtistClient;
import guru.qa.rococo.service.MuseumClient;
import guru.qa.rococo.service.PaintingClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;


@ParametersAreNonnullByDefault
public class PaintingDbClient  implements PaintingClient {

  private static final Config CFG = Config.getInstance();
  private final PaintingRepository paintingRepository = PaintingRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.geoJdbcUrl(),
      CFG.museumJdbcUrl(),
      CFG.paintingJdbcUrl(),
      CFG.artistJdbcUrl()
  );

    @NotNull
    @Override
    @Step("Create painting using SQL INSERT")
    public PaintingJson createPainting(PaintingJson painting) {
        return Objects.requireNonNull(
                xaTransactionTemplate.execute(() ->
                        PaintingJson.fromEntity(paintingRepository.create(PaintingEntity.fromJson(painting)))
                )
        );
    }

    @Nullable
    @Override
    public PaintingJson findByTitle(String title) {
        return xaTransactionTemplate.execute(()->
                paintingRepository.findByTitle(title)
                        .map(PaintingJson::fromEntity)
                        .orElse(null)
        );
    }


}
