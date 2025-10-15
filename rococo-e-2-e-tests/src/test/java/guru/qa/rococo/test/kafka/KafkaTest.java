package guru.qa.rococo.test.kafka;

import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.impl.KafkaService;
import guru.qa.rococo.service.impl.api.UserApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.rococo.utils.RandomDataUtils.randomUsername;

@guru.qa.rococo.jupiter.annotation.meta.KafkaTest
@DisplayName("kafka")
public class KafkaTest {
    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final UserApiClient usersApiClient = new UserApiClient();


    @Test
    @DisplayName("После регистрации в Auth пользователь отправляется в kafka")
    void userShouldBeSendToKafka() throws Exception {
        final String username = randomUsername();
        usersApiClient.createUser(username, "12345");
        UserJson kafkaUser = KafkaService.getUser(username);
        Assertions.assertEquals(username, kafkaUser.username());
    }
}
