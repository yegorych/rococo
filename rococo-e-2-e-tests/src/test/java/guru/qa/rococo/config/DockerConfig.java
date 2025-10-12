package guru.qa.rococo.config;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;


enum DockerConfig implements Config {
  instance;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.rococo.dc/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.rococo.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rococo.dc:8080/";
  }

  @NotNull
  @Override
  public String artistJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-artist";
  }


  @NotNull
  @Override
  public String museumJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-museum";
  }

  @NotNull
  @Override
  public String paintingJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-painting";
  }

  @NotNull
  @Override
  public String geoJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-geo";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.rococo.dc:8089/";
  }

  @NotNull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:mysql://rococo-all-db:3306/rococo-userdata";
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
    return "artist.rococo.dc";
  }

  @NotNull
  @Override
  public String geoGrpcAddress() {
    return "geo.rococo.dc";
  }

  @NotNull
  @Override
  public String userdataGrpcAddress() {
    return "userdata.rococo.dc";
  }

  @NotNull
  @Override
  public String paintingGrpcAddress() {
    return "painting.rococo.dc";
  }

  @Nonnull
  @Override
  public String allureDockerUrl() {
    final String allureDockerApiFromEnv = System.getenv("ALLURE_DOCKER_API");
    return allureDockerApiFromEnv != null
            ? allureDockerApiFromEnv
            : "http://allure:5050/";
  }
  @Nonnull
  @Override
  public String kafkaAddress() {
    return "kafka:9092";
  }
}
