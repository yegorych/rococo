package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  instance;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8080/";
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-artist";
  }


  @NotNull
  @Override
  public String museumJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-museum";
  }
  @NotNull
  @Override
  public String paintingJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-painting";
  }

  @NotNull
  @Override
  public String geoJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-geo";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://127.0.0.1:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rococo-userdata";
  }

  @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/local/";
  }

  @Nonnull
  @Override
  public String museumGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String artistGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String geoGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String paintingGrpcAddress() {
    return "127.0.0.1";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    return "http://allure:5050/";
  }

  @Nonnull
  @Override
  public String kafkaAddress() {
    return "127.0.0.1:9092";
  }
}
