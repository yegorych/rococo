package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface MuseumClient {
  @Nonnull
  MuseumJson createMuseum(MuseumJson museum);

  @Nullable
  MuseumJson findMuseumByTitle(String title);
}
