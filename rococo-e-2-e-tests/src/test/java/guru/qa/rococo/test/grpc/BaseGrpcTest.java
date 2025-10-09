package guru.qa.rococo.test.grpc;

import guru.qa.grpc.rococo.artist.RococoArtistServiceGrpc;
import guru.qa.grpc.rococo.geo.RococoGeoServiceGrpc;
import guru.qa.grpc.rococo.museum.RococoMuseumServiceGrpc;
import guru.qa.grpc.rococo.painting.RococoPaintingServiceGrpc;
import guru.qa.rococo.config.Config;
import guru.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class BaseGrpcTest {
    private static final Config CFG = Config.getInstance();

    protected static RococoArtistServiceGrpc.RococoArtistServiceBlockingStub artistStub =
            RococoArtistServiceGrpc.newBlockingStub(
                    channel(CFG.artistGrpcAddress(), CFG.artistGrpcPort())
            );

    protected static RococoGeoServiceGrpc.RococoGeoServiceBlockingStub geoStub =
            RococoGeoServiceGrpc.newBlockingStub(
                    channel(CFG.geoGrpcAddress(), CFG.geoGrpcPort())
            );

    protected static RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub museumStub =
            RococoMuseumServiceGrpc.newBlockingStub(
                    channel(CFG.museumGrpcAddress(), CFG.museumGrpcPort())
            );

    protected static RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub paintingStub
            = RococoPaintingServiceGrpc.newBlockingStub(
                    channel(CFG.paintingGrpcAddress(), CFG.paintingGrpcPort())
    );

    private static Channel channel(String url, int port){
        return ManagedChannelBuilder
                .forAddress(url, port)
                .intercept(new GrpcConsoleInterceptor())
                .usePlaintext()
                .build();
    }
}
