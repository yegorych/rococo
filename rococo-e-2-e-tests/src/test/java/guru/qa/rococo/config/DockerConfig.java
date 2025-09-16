package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

//доделать !!!!
enum DockerConfig implements Config {
  instance;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.rococo.dc:3000/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.rococo.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    //return "jdbc:postgresql://rococo-all-db:3306/rococo-auth";
    return "";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rococo.dc:8080/";
  }

  @NotNull
  @Override
  public String artistUrl() {
    return "";
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String museumUrl() {
    return "";
  }

  @NotNull
  @Override
  public String museumJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String paintingUrl() {
    return "";
  }

  @NotNull
  @Override
  public String paintingJdbcUrl() {
    return "";
  }

  @NotNull
  @Override
  public String geoJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.rococo.dc:8089/";
  }

  @NotNull
  @Override
  public String userdataJdbcUrl() {
    return "";
  }

  @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/selenoid/";
  }

  @Nonnull
  @Override
  public String museumGrpcAddress() {
    return "museum.rococo.dc";
  }

  @NotNull
  @Override
  public String artistGrpcAddress() {
    return "";
  }

  @NotNull
  @Override
  public String geoGrpcAddress() {
    return "";
  }

  @NotNull
  @Override
  public String paintingGrpcAddress() {
    return "";
  }
}
