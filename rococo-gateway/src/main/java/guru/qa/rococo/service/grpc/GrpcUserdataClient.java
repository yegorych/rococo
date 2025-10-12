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
}
