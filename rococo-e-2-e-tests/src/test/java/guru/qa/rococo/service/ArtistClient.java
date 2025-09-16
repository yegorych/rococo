package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.ArtistJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ArtistClient {
  @Nonnull
  ArtistJson createArtist(ArtistJson artist);

  @Nullable
  ArtistJson findArtistByName(String name);
}
