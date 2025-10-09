package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.PaintingJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface CountryClient {
  @Nonnull
  List<CountryJson> findAll();
  @Nullable
  CountryJson findById(String id);
}
