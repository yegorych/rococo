package guru.qa.rococo.test.grpc.userdata;

import guru.qa.grpc.rococo.geo.CountriesRequest;
import guru.qa.grpc.rococo.geo.CountriesResponse;
import guru.qa.grpc.rococo.geo.Country;
import guru.qa.grpc.rococo.geo.IdRequest;
import guru.qa.grpc.rococo.userdata.UserInfo;
import guru.qa.grpc.rococo.userdata.UserRequest;
import guru.qa.rococo.jupiter.annotation.User;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.rest.CountryJson;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.CountryClient;
import guru.qa.rococo.service.impl.db.CountryDbClient;
import guru.qa.rococo.test.grpc.BaseGrpcTest;
import guru.qa.rococo.utils.RandomDataUtils;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@GrpcTest
public class UserdataGrpcTest extends BaseGrpcTest {

    @Test
    @User
    void userShouldBeReturned(UserJson user) {
        UserInfo response = userdataStub.getUser(
                UserRequest.newBuilder()
                        .setUsername(user.username())
                        .build()
        );
        Assertions.assertEquals(user.username(), response.getUsername());
        Assertions.assertEquals(user.id().toString(), response.getId());
    }

    @Test
    void userWithRandomUsernameShouldNotBeReturned() {
        String username = RandomDataUtils.randomUsername();
        UserRequest request = UserRequest.newBuilder()
                        .setUsername(username)
                        .build();

        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> userdataStub.getUser(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
        Assertions.assertEquals(
                String.format("Пользователь с именем \"%s\" не найден", username),
                ex.getStatus().getDescription()
        );
    }

    @Test
    @User
    void userShouldBeUpdated(UserJson user) {
        final String name = "name";
        final String lastname = "lastname";
        UserInfo response = userdataStub.updateUser(
                UserInfo.newBuilder()
                        .setId(user.id().toString())
                        .setUsername(user.username())
                        .setFirstname(name)
                        .setLastname(lastname)
                        .build()
        );
        Assertions.assertEquals(user.username(), response.getUsername());
        Assertions.assertEquals(name, response.getFirstname());
        Assertions.assertEquals(lastname, response.getLastname());
    }

    @Test
    void userWithRandomNameShouldNotBeUpdated() {
        String username = RandomDataUtils.randomUsername();
        UserInfo request = UserInfo.newBuilder()
                .setUsername(username)
                .build();

        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> userdataStub.updateUser(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
        Assertions.assertEquals(
                String.format("Пользователь с именем \"%s\" не найден", username),
                ex.getStatus().getDescription()
        );
    }

}
