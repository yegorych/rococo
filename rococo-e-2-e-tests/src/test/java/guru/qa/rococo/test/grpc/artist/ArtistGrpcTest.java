package guru.qa.rococo.test.grpc.artist;

import guru.qa.grpc.rococo.artist.Artist;
import guru.qa.grpc.rococo.artist.ArtistRequest;
import guru.qa.grpc.rococo.artist.ArtistsResponse;
import guru.qa.grpc.rococo.artist.IdRequest;
import guru.qa.rococo.jupiter.annotation.container.Artists;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.test.grpc.BaseGrpcTest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@GrpcTest
public class ArtistGrpcTest extends BaseGrpcTest {

    @Test
    void allArtistsShouldBeReturned() {
        ArtistsResponse response = artistStub.getArtists(
                ArtistRequest.newBuilder()
                        .setPage(0)
                        .setSize(1000)
                        .build()
        );
        // Если сервис содержит какие-то артисты, totalCount >= возвращенного списка
        Assertions.assertTrue(response.getTotalCount() >= response.getArtistsCount());
    }

    @Test
    @Artists(count = 5)
    void artistsShouldBePaginated() {
        int size = 5;
        ArtistsResponse response = artistStub.getArtists(
                ArtistRequest.newBuilder()
                        .setPage(0)
                        .setSize(size)
                        .build()
        );
        Assertions.assertEquals(size, response.getArtistsCount());
    }

    @Test
    void getArtists_shouldReturnFilteredByName() {
        ArtistsResponse response = artistStub.getArtists(
                ArtistRequest.newBuilder()
                        .setName("a") // просто проверка фильтра — сервер не должен падать
                        .setPage(0)
                        .setSize(10)
                        .build()
        );
        Assertions.assertNotNull(response);
    }

    @Test
    @guru.qa.rococo.jupiter.annotation.Artist
    void artistShouldBeReturnedById(TestData data) {
        var artistJson = data.artists().getFirst();
        Artist response = artistStub.getArtist(IdRequest.newBuilder()
                .setId(artistJson.id().toString())
                .build());
        Assertions.assertEquals(artistJson.name(), response.getName());
    }

    @Test
    void artistByRandomIdShouldNotBeReturned() {
        IdRequest request = IdRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.getArtist(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
    }

    @Test
    void artistByInvalidUuidShouldReturnInvalidArgument() {
        IdRequest request = IdRequest.newBuilder().setId("not-uuid").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.getArtist(request)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    void artistShouldBeCreated() {
        Artist request = Artist.newBuilder().setName("Test Artist " + UUID.randomUUID()).build();
        Artist created = artistStub.createArtist(request);
        Assertions.assertFalse(created.getId().isEmpty());
        Assertions.assertEquals(request.getName(), created.getName());
    }

    @Test
    void createArtist_withId_shouldFail() {
        Artist request = Artist.newBuilder().setId("123").setName("BadArtist").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.createArtist(request)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @guru.qa.rococo.jupiter.annotation.Artist
    void createArtist_whenDuplicate_shouldFail(TestData data) {
        var existing = data.artists().getFirst();
        Artist request = Artist.newBuilder().setName(existing.name()).build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.createArtist(request)
        );
        Assertions.assertEquals("ALREADY_EXISTS", ex.getStatus().getCode().toString());
    }

    @Test
    @guru.qa.rococo.jupiter.annotation.Artist
    void artistShouldBeUpdated(TestData data) {
        var existing = data.artists().getFirst();
        Artist request = Artist.newBuilder()
                .setId(existing.id().toString())
                .setName(existing.name() + " updated")
                .build();
        Artist updated = artistStub.updateArtist(request);
        Assertions.assertEquals(request.getName(), updated.getName());
    }

    @Test
    void updateArtist_notFound_shouldReturnNotFound() {
        Artist request = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("NoExists")
                .build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.updateArtist(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
    }

    @Test
    void updateArtist_invalidUuid_shouldReturnInvalidArgument() {
        Artist request = Artist.newBuilder().setId("bad-uuid").setName("x").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.updateArtist(request)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @guru.qa.rococo.jupiter.annotation.Artist
    void updateArtist_whenDuplicateName_shouldFail(TestData data) {
        // подготовим два артиста: первый создан аннотацией, второй создаём вручную
        var a1 = data.artists().getFirst();
        Artist a2 = Artist.newBuilder().setName("duplicate-" + UUID.randomUUID()).build();
        Artist created = artistStub.createArtist(a2);

        // попробуем обновить created, установив имя существующего (a1)
        Artist updateReq = Artist.newBuilder()
                .setId(created.getId())
                .setName(a1.name())
                .build();

        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> artistStub.updateArtist(updateReq)
        );
        Assertions.assertEquals("ALREADY_EXISTS", ex.getStatus().getCode().toString());
    }
}
