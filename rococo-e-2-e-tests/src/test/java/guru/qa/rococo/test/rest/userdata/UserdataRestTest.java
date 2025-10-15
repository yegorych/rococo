package guru.qa.rococo.test.rest.userdata;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.rest.UserJson;
import guru.qa.rococo.service.impl.api.UserApiClient;
import guru.qa.rococo.utils.RandomDataUtils;
import guru.qa.rococo.utils.TokenDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

import static guru.qa.rococo.jupiter.extension.ApiLoginExtension.rest;
import static guru.qa.rococo.utils.ErrorMessageResolver.getErrorMessage;
import static guru.qa.rococo.utils.ImgBase64Utils.imageToBase64;

@RestTest
@DisplayName("rest: тесты контроллера User в Gateway")
public class UserdataRestTest {
    @RegisterExtension
    private static final ApiLoginExtension extension = rest();
    private final static UserApiClient userApiClient = new UserApiClient();

    @Test
    @ApiLogin
    @DisplayName("Получение пользователя по токену")
    void userShouldBeReturnedByToken(@Token String token){
        Response<UserJson> response = userApiClient.getUser(token);
        UserJson body = response.body();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(200, response.code());
        Assertions.assertNotNull(body.id());
        Assertions.assertEquals(
                TokenDecoder.decodeBearerToken(token).get("sub"), body.username());
    }

    @Test
    @DisplayName("Получение пользователя с недействительным токеном возвращает 401")
    void userShouldNotBeReturnedByInvalidToken(){
        Response<UserJson> response = userApiClient.getUser("fake");
        Assertions.assertEquals(401, response.code());
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление имени и фамилии пользователя")
    void userShouldBeUpdated(@Token String token){
        String name = "name";
        String lastname = "lastname";
        UserJson user = userApiClient.getUser(token).body();
        Assertions.assertNotNull(user);
        UserJson request = user.addFirstname(name).addLastname(lastname);
        Response<UserJson> response = userApiClient.updateUser(token, request);
        UserJson body = response.body();
        Assertions.assertNotNull(body);
        Assertions.assertEquals(200, response.code());

        UserJson updatedUser = userApiClient.getUser(token).body();
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(name, updatedUser.firstname());
        Assertions.assertEquals(lastname, updatedUser.lastname());
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление другого пользователя возвращает 403")
    void userShouldNotBeUpdatedWithAnotherUsername(@Token String token){
        UserJson request = new UserJson("fakeName");
        Response<UserJson> response = userApiClient.updateUser(token, request);
        Assertions.assertEquals(403, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Вы не можете изменить данные другого пользователя",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление с именем >255 символов возвращает ошибку")
    void userShouldNotBeUpdatedWithLongName(@Token String token){
        String name = RandomDataUtils.randomWord(256);
        UserJson user = userApiClient.getUser(token).body();
        Assertions.assertNotNull(user);
        UserJson request = user.addFirstname(name);
        Response<UserJson> response = userApiClient.updateUser(token, request);
        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Имя не может быть длиннее 255 символов",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление с фамилией >255 символов возвращает ошибку")
    void userShouldNotBeUpdatedWithLongLastname(@Token String token){
        String lastname = RandomDataUtils.randomWord(256);
        UserJson user = userApiClient.getUser(token).body();
        Assertions.assertNotNull(user);
        UserJson request = user.addLastname(lastname);
        Response<UserJson> response = userApiClient.updateUser(token, request);
        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Фамилия не может быть длиннее 255 символов",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление с пустой фамилией (пробелы) возвращает ошибку")
    void userShouldNotBeUpdatedWithBlankLastname(@Token String token){
        String lastname = "   ";
        UserJson user = userApiClient.getUser(token).body();
        Assertions.assertNotNull(user);
        UserJson request = user.addLastname(lastname);
        Response<UserJson> response = userApiClient.updateUser(token, request);
        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Имя и фамилия не могут состоять из пробелов",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление с пустым именем (пробелы) возвращает ошибку")
    void userShouldNotBeUpdatedWithBlankName(@Token String token){
        String name = "   ";
        UserJson user = userApiClient.getUser(token).body();
        Assertions.assertNotNull(user);
        UserJson request = user.addFirstname(name);
        Response<UserJson> response = userApiClient.updateUser(token, request);
        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Имя и фамилия не могут состоять из пробелов",
                getErrorMessage(response.errorBody())
        );
    }

    @Test
    @ApiLogin
    @DisplayName("Обновление с фото >1MB возвращает ошибку")
    void userShouldNotBeUpdatedWithLargePhoto(@Token String token){
        String image = imageToBase64("img/1_1mb_photo.png");
        UserJson user = userApiClient.getUser(token).body();
        Assertions.assertNotNull(user);
        UserJson request = user.addPhoto(image);
        Response<UserJson> response = userApiClient.updateUser(token, request);
        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals(
                "Фото профиля не может превышать 1 MB",
                getErrorMessage(response.errorBody())
        );
    }
}
