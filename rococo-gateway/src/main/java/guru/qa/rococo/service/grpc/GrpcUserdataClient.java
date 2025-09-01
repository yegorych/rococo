package guru.qa.rococo.service.grpc;

import guru.qa.grpc.rococo.userdata.RococoUserdataServiceGrpc;
import guru.qa.grpc.rococo.userdata.UserInfo;
import guru.qa.grpc.rococo.userdata.UserRequest;
import guru.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GrpcUserdataClient {

    private static final Logger LOG = LoggerFactory.getLogger(GrpcUserdataClient.class);

    @GrpcClient("grpcUserdataClient")
    private RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub rococoUserdataServiceStub;

    public @Nonnull UserInfo getUser(@Nonnull String username) {
        return rococoUserdataServiceStub.getUser(UserRequest.newBuilder().setUsername(username).build());
    }

    public @Nonnull UserInfo updateUser(@Nonnull UserInfo user) {
        return rococoUserdataServiceStub.updateUser(user);
    }

//  public @Nonnull UserJson getUser(@Nonnull String username) {
//    try {
//      UserInfo userGrpc = rococoUserdataServiceStub.getUser(UserRequest.newBuilder().setUsername(username).build());
//      return UserJson.fromGrpcUser(userGrpc);
//    } catch (StatusRuntimeException e) {
//      LOG.error("### Error while calling gRPC server ", e);
//      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
//    }
//  }
//
//  public @Nonnull UserJson updateUser(@Nonnull UserJson user) {
//    try {
//      return UserJson.fromGrpcUser(rococoUserdataServiceStub.updateUser(user.toGrpcUser()));
//    }  catch (StatusRuntimeException e) {
//      LOG.error("### Error while calling gRPC server ", e);
//      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The gRPC operation was cancelled", e);
//    }
//  }

}
