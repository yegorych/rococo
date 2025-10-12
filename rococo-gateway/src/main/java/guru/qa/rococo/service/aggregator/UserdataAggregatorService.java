package guru.qa.rococo.service.aggregator;

import guru.qa.rococo.ex.ValidationException;
import guru.qa.rococo.model.UserJson;
import guru.qa.rococo.service.grpc.GrpcUserdataClient;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserdataAggregatorService {
    private final GrpcUserdataClient userdataClient;

    @Autowired
    public UserdataAggregatorService(GrpcUserdataClient userdataClient){
        this.userdataClient = userdataClient;
    }

    public @Nonnull UserJson getUser(String username){
        return UserJson.fromGrpcMessage(userdataClient.getUser(username));
    }

    public @Nonnull UserJson updateUser(@Nonnull UserJson userJson){
        if ((!userJson.firstname().isEmpty() && userJson.firstname().isBlank()) ||
                (!userJson.lastname().isEmpty() && userJson.lastname().isBlank())) {
            throw new ValidationException("Имя и фамилия не могут состоять из пробелов");
        }
        return UserJson.fromGrpcMessage(userdataClient.updateUser(userJson.toGrpcMessage()));
    }
}
