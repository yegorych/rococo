package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.model.rest.PaintingJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface PaintingClient {
  @Nonnull
  PaintingJson createPainting(PaintingJson painting);
  @Nullable
  PaintingJson findByTitle(String title);
}
