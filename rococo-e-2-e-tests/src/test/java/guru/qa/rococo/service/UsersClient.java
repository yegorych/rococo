package guru.qa.rococo.service;

import guru.qa.rococo.model.rest.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UsersClient {
  @Nonnull
  UserJson createUser(String username, String password);
}
