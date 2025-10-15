package guru.qa.rococo.test.grpc.painting;

import guru.qa.grpc.rococo.painting.IdRequest;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.grpc.rococo.painting.PaintingRequest;
import guru.qa.grpc.rococo.painting.PaintingsResponse;
import guru.qa.rococo.jupiter.annotation.Artist;
import guru.qa.rococo.jupiter.annotation.Museum;
import guru.qa.rococo.jupiter.annotation.container.Paintings;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.ArtistJson;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.test.grpc.BaseGrpcTest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@GrpcTest
@DisplayName("gRPC: сервис Painting")
public class PaintingGrpcTest extends BaseGrpcTest {

    @Test
    @Paintings(count = 20)
    @DisplayName("Получение всех картин")
    void allPaintingsShouldBeReturned() {
        PaintingsResponse resp = paintingStub.getPaintings(
                PaintingRequest.newBuilder().setPage(0).setSize(20).setTitle("").setArtistId("").build()
        );
        Assertions.assertTrue(resp.getTotalCount() >= resp.getPaintingsCount());
    }

    @Test
    @DisplayName("Пагинация списка картин")
    void paintingsShouldBePaginated() {
        int size = 4;
        PaintingsResponse resp = paintingStub.getPaintings(
                PaintingRequest.newBuilder().setPage(1).setSize(size).build()
        );
        Assertions.assertEquals(size, resp.getPaintingsCount());
    }

    @Test
    @DisplayName("Получение картины по ID")
    @guru.qa.rococo.jupiter.annotation.Painting
    void paintingShouldBeReturnedById(TestData data) {
        var paintingJson = data.paintings().getFirst();
        Painting response = paintingStub.getPainting(IdRequest.newBuilder()
                .setId(paintingJson.id().toString()).build());
        Assertions.assertEquals(paintingJson.title(), response.getTitle());
    }

    @Test
    @DisplayName("Картина по случайному ID не найдена")
    void paintingByRandomIdShouldNotBeReturned() {
        IdRequest request = IdRequest.newBuilder().setId(UUID.randomUUID().toString()).build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> paintingStub.getPainting(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Некорректный UUID возвращает INVALID_ARGUMENT")
    void paintingByInvalidUuidShouldReturnInvalidArgument() {
        IdRequest request = IdRequest.newBuilder().setId("no-uuid").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> paintingStub.getPainting(request)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Создание новой картины")
    @Museum
    @Artist
    void paintingShouldBeCreated(TestData data) {
        MuseumJson museum = data.museums().getFirst();
        ArtistJson artist = data.artists().getFirst();

        Painting req = Painting.newBuilder()
                .setTitle("New painting " + UUID.randomUUID())
                .setArtistId(artist.id().toString())
                .setMuseumId(museum.id().toString())
                .build();

        Painting created = paintingStub.createPainting(req);
        Assertions.assertFalse(created.getId().isEmpty());
        Assertions.assertEquals(req.getTitle(), created.getTitle());
    }

    @Test
    @DisplayName("Создание картины с ID должно завершиться ошибкой")
    void paintingWithNonEmptyIdShouldNotBeCreated() {
        Painting req = Painting.newBuilder().setId("123").setTitle("x").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> paintingStub.createPainting(req)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Обновление существующей картины")
    @guru.qa.rococo.jupiter.annotation.Painting(museum = @Museum)
    void paintingShouldBeUpdated(TestData data) {
        Painting created = data.paintings().getFirst().toGrpcMessage();
        Painting request = Painting.newBuilder()
                .setId(created.getId())
                .setTitle(created.getTitle() + "upd")
                .setArtistId(created.getArtistId())
                .build();
        Painting updated = paintingStub.updatePainting(request);
        Assertions.assertEquals(request.getTitle(), updated.getTitle());
    }

    @Test
    @DisplayName("Обновление несуществующей картины возвращает NOT_FOUND")
    void paintingWithRandomIdShouldNotBeUpdated() {
        Painting req = Painting.newBuilder().setId(UUID.randomUUID().toString()).setTitle("x").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> paintingStub.updatePainting(req)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Обновление с некорректным UUID возвращает INVALID_ARGUMENT")
    void paintingWithInvalidIdShouldNotBeUpdated() {
        Painting req = Painting.newBuilder().setId("bad-uuid").setTitle("x").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> paintingStub.updatePainting(req)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @Artist
    @DisplayName("Фильтрация картин по художнику")
    void paintingsShouldBeFilteredByArtist(TestData data) {
        var artist = data.artists().getFirst();
        PaintingsResponse resp = paintingStub.getPaintings(
                PaintingRequest.newBuilder()
                        .setArtistId(artist.id().toString())
                        .setPage(0)
                        .setSize(10)
                        .build()
        );
        resp.getPaintingsList().forEach(p -> Assertions.assertEquals(artist.id().toString(), p.getArtistId()));
    }
}
