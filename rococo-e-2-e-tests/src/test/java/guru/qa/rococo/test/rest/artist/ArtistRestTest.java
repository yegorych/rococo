package guru.qa.rococo.test.rest.artist;

import guru.qa.rococo.jupiter.annotation.ApiLogin;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Token;
import guru.qa.rococo.jupiter.annotation.container.Artists;
import guru.qa.rococo.jupiter.annotation.meta.RestTest;
import guru.qa.rococo.jupiter.extension.ApiLoginExtension;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.pageable.RestResponsePage;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.service.impl.api.ArtistApiClient;
import guru.qa.rococo.utils.ErrorMessageResolver;
import guru.qa.rococo.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.core.io.ClassPathResource;
import retrofit2.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static guru.qa.rococo.utils.ErrorMessageResolver.getErrorMessage;

@RestTest
public class ArtistRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();
    private final ArtistApiClient artistApiClient = new ArtistApiClient();


    @Test
    @Artist
    void artistShouldBeReturnedByIdFromGateway(TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        final Response<ArtistJson> response = artistApiClient
                .getArtist(createdArtist.id().toString());

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(createdArtist, response.body());
    }

    @Test
    @Artist(name = "Artist yeg 1")
    @Artist(name = "Artist yeg 2")
    @Artist(name = "Artist yeg 3")
    void shouldReturnFilteredArtistsPageByName() {
        final Response<RestResponsePage<ArtistJson>> response = artistApiClient
                .getArtistPage("Artist yeg", 0, 10);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertNotNull(response.body());
        Assertions.assertEquals(2, response.body().getContent().size());
    }

    @Test
    @Artists(count = 15)
    void artistsShouldBePaginated() {
        int size = 5;
        Set<ArtistJson> artists = new HashSet<>();
        int page = 0;
        while (page!= 3) {
            Response<RestResponsePage<ArtistJson>> response = artistApiClient.getArtistPage(null, page, size);
            Assertions.assertTrue(response.isSuccessful());
            Assertions.assertNotNull(response.body());
            artists.addAll(response.body().getContent());
            page++;
        }
        Assertions.assertEquals(15, artists.size());
    }

    @Test
    @ApiLogin
    void artistShouldNotBeCreatedWithNonEmptyId(@Token String token) {
        ArtistJson artistJson = ArtistJson.randomArtist().addId(UUID.randomUUID());
        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals(400, response.code());
        Assertions.assertEquals("ID не должен быть задан при создании художника", errorMessage);
    }

    @Test
    @ApiLogin
    @Artist
    void shouldNotCreateArtistWithDuplicateName(@Token String token, TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        ArtistJson artistJson = new ArtistJson(
                null,
                createdArtist.name(),
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(409, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals("Художник с таким именем уже существует", ErrorMessageResolver.getErrorMessage(response.errorBody()));
    }

    @Test
    @ApiLogin
    void artistShouldNotBeCreatedWithBlankName(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                "      ",
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Имя не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    void artistShouldNotBeCreatedWithEmptyName(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                "",
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Имя не может быть пустым или состоять только из пробелов", errorMessage);
    }

    @Test
    @ApiLogin
    void artistShouldNotBeCreatedWithLongName(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                RandomDataUtils.randomWord(300),
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Имя не может быть длиннее 255 символов", errorMessage);
    }

    @Test
    @ApiLogin
    void artistShouldNotBeCreatedWithLongBiography(@Token String token) {
        ArtistJson artistJson = new ArtistJson(
                null,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomWord(2002),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Биография не может быть длиннее 2000 символов", errorMessage);
    }

    @Test
    @ApiLogin
    void artistShouldNotBeCreatedWithLargeImage(@Token String token) throws IOException {
        byte[] imageBytes = Files.readAllBytes(new ClassPathResource("img/5mb-photo.png").getFile().toPath());
        String image =  Base64.getEncoder().encodeToString(imageBytes);

        ArtistJson artistJson = new ArtistJson(
                null,
                RandomDataUtils.randomWord(10),
                RandomDataUtils.randomWord(2002),
                image
        );

        final Response<ArtistJson> response = artistApiClient
                .createArtist(token, artistJson);

        Assertions.assertEquals(400, response.code());
        Assertions.assertNotNull(response.errorBody());
        String errorMessage = getErrorMessage(response.errorBody());
        Assertions.assertEquals("Размер фото не может превышать 4 MB", errorMessage);
    }



    @Test
    @ApiLogin
    @Artist
    void artistShouldBeUpdated(@Token String token, TestData testData) {
        ArtistJson createdArtist = testData.artists().getFirst();
        ArtistJson expectedArtist = ArtistJson.randomArtist().addId(createdArtist.id());
        final Response<ArtistJson> response = artistApiClient
                .updateArtist(token, expectedArtist);

        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertEquals(expectedArtist, response.body());
    }

    @Test
    @ApiLogin
    @Artists(count = 2)
    void shouldNotUpdatedArtistWithDuplicateName(@Token String token, TestData testData) {
        ArtistJson firstArtist = testData.artists().getFirst();
        ArtistJson secondArtist = testData.artists().getLast();

        ArtistJson artistJson = new ArtistJson(
                firstArtist.id(),
                secondArtist.name(),
                RandomDataUtils.randomSentence(20),
                null
        );

        final Response<ArtistJson> response = artistApiClient
                .updateArtist(token, artistJson);

        Assertions.assertEquals(409, response.code());
        Assertions.assertNotNull(response.errorBody());
        Assertions.assertEquals("Художник с таким именем уже существует", ErrorMessageResolver.getErrorMessage(response.errorBody()));
    }





    //  @User(friends = 1)
//  @ApiLogin
//  @Test
//  void friendsShouldBeDeletedByGateway(UserJson user,  @Token String bearerToken) {
//    user.testData().friends().forEach(friend-> gatewayApiClient.removeFriend(bearerToken, friend.username()));
//    final List<UserJson> friendsList = gatewayApiClient.allFriends(bearerToken, null);
//    Assertions.assertTrue(friendsList.isEmpty());
//  }
//
//  @User(incomeInvitations = 2)
//  @ApiLogin
//  @Test
//  void incomeInvitationsShouldBeAcceptedByGateway(UserJson user,  @Token String bearerToken) {
//    final int expectedFriends = user.testData().incomeInvitations().size();
//    user.testData().incomeInvitations().forEach(income-> gatewayApiClient.acceptInvitation(bearerToken, income.username()));
//    final List<UserJson> friendsList = gatewayApiClient.allFriends(bearerToken, null).stream()
//            .filter(userJson -> FRIEND.equals(userJson.friendshipStatus())).toList();
//    Assertions.assertEquals(expectedFriends, friendsList.size());
//  }
//
//  @User(incomeInvitations = 2)
//  @ApiLogin
//  @Test
//  void incomeInvitationsShouldBeDeclinedByGateway(UserJson user,  @Token String bearerToken) {
//    user.testData().incomeInvitations().forEach(income-> gatewayApiClient.declineInvitation(bearerToken, income.username()));
//    final List<UserJson> friendsList = gatewayApiClient.allFriends(bearerToken, null);
//    Assertions.assertTrue(friendsList.isEmpty());
//  }
//
//  @User
//  @ApiLogin
//  @Test
//  void incomeAndOutcomeInvitationsShouldBeCreatedByGateway(UserJson user,  @Token String bearerToken) {
//    String randomUsername = RandomDataUtils.randomUsername();
//    String randomPassword = "12345";
//    UserJson targetUser = usersClient.createUser(randomUsername, randomPassword);
//
//    gatewayApiClient.sendInvitation(bearerToken, targetUser.username());
//
//    Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                    targetUser.username(),
//                    usersClient.getOutcomeInvitations(user.username()).getFirst().username()
//            ),
//            () -> Assertions.assertEquals(
//                    user.username(),
//                    usersClient.getIncomeInvitations(targetUser.username()).getFirst().username()
//            )
//    );
//  }
}
