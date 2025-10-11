package guru.qa.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  String projectId = "rococo-yegorych";

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
  String artistJdbcUrl();
  @Nonnull
  String museumJdbcUrl();
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
  @Nonnull
  String allureDockerUrl();

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
