package guru.qa.rococo.service.orchestration;

import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.grpc.GrpcUserdataClient;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserdataOrchestrationService {
    private final GrpcUserdataClient userdataClient;

    @Autowired
    public UserdataOrchestrationService(GrpcUserdataClient userdataClient){
        this.userdataClient = userdataClient;
    }

    public @Nonnull UserJson getUser(String username){
        return UserJson.fromGrpcMessage(userdataClient.getUser(username));
    }

    public @Nonnull UserJson updateUser(@Nonnull UserJson userJson){
        return UserJson.fromGrpcMessage(userdataClient.updateUser(userJson.toGrpcMessage()));
    }
}
