package guru.qa.rococo.jupiter.extension;

import guru.qa.rococo.data.jdbc.Connections;

public class DatabasesExtension implements SuiteExtension {
  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
  }
}
