package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  @Nonnull
  String frontUrl();
  @Nonnull
  String authUrl();
  @Nonnull
  String authJdbcUrl();
  @Nonnull
  String userdataUrl();
  @Nonnull
  String userdataJdbcUrl();
  @Nonnull
  String gatewayUrl();
  @Nonnull
  String artistUrl();
  @Nonnull
  String artistJdbcUrl();
  @Nonnull
  String museumUrl();
  @Nonnull
  String museumJdbcUrl();
  @Nonnull
  String paintingUrl();
  @Nonnull
  String paintingJdbcUrl();
  @Nonnull
  String geoJdbcUrl();
  default String ghUrl() {
    return "https://api.github.com/";
  }
  @Nonnull
  String screenshotBaseDir();
  @Nonnull
  String museumGrpcAddress();
  @Nonnull
  String artistGrpcAddress();
  @Nonnull
  String geoGrpcAddress();
  @Nonnull
  String paintingGrpcAddress();

  default int museumGrpcPort() {
    return 9095;
  }

  default int artistGrpcPort() {
    return 9093;
  }

  default int geoGrpcPort() {
    return 9094;
  }

  default int paintingGrpcPort() {
    return 9091;
  }

}
