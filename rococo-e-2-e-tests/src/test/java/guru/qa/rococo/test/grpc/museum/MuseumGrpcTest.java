package guru.qa.rococo.test.grpc.museum;

import guru.qa.grpc.rococo.museum.IdRequest;
import guru.qa.grpc.rococo.museum.Museum;
import guru.qa.grpc.rococo.museum.MuseumsRequest;
import guru.qa.grpc.rococo.museum.MuseumsResponse;
import guru.qa.rococo.jupiter.annotation.container.Museums;
import guru.qa.rococo.jupiter.annotation.meta.GrpcTest;
import guru.qa.rococo.model.TestData;
import guru.qa.rococo.model.rest.MuseumJson;
import guru.qa.rococo.service.CountryClient;
import guru.qa.rococo.service.impl.db.CountryDbClient;
import guru.qa.rococo.test.grpc.BaseGrpcTest;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@GrpcTest
@DisplayName("gRPC: сервис Museum")
public class MuseumGrpcTest extends BaseGrpcTest {
    private static final CountryClient countryClient = new CountryDbClient();


    @Test
    @Museums(count = 10)
    @DisplayName("Получение всех музеев")
    void allMuseumsShouldBeReturned() {
        MuseumsResponse resp = museumStub.getMuseums(
                MuseumsRequest.newBuilder().setPage(0).setSize(1000).setTitle("").build()
        );
        Assertions.assertTrue(resp.getTotalCount() >= resp.getMuseumCount());
    }

    @Test
    @Museums(count = 7)
    @DisplayName("Пагинация списка музеев")
    void museumsShouldBePaginated() {
        int size = 7;
        MuseumsResponse resp = museumStub.getMuseums(
                MuseumsRequest.newBuilder().setPage(0).setSize(size).build()
        );
        Assertions.assertEquals(size, resp.getMuseumCount());
    }

    @Test
    @DisplayName("Получение музея по ID")
    @guru.qa.rococo.jupiter.annotation.Museum
    void museumShouldBeReturnedById(TestData data) {
        var museumJson = data.museums().getFirst();
        Museum response = museumStub.getMuseum(IdRequest.newBuilder()
                .setId(museumJson.id().toString()).build());
        Assertions.assertEquals(museumJson.title(), response.getTitle());
    }

    @Test
    @DisplayName("Музей по случайному ID не найден")
    void museumByRandomIdShouldNotBeReturned() {
        IdRequest request = IdRequest.newBuilder().setId(UUID.randomUUID().toString()).build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.getMuseum(request)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Некорректный UUID возвращает INVALID_ARGUMENT")
    void museumByInvalidUuidShouldReturnInvalidArgument() {
        IdRequest request = IdRequest.newBuilder().setId("123").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.getMuseum(request)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Создание нового музея")
    void museumShouldBeCreated() {
        Museum req = Museum.newBuilder()
                .setTitle("New Museum " + UUID.randomUUID())
                .setCountryId(countryClient.findAll().getFirst().id().toString())// optional; can be empty
                .build();
        Museum created = museumStub.createMuseum(req);
        Assertions.assertFalse(created.getId().isEmpty());
        Assertions.assertEquals(req.getTitle(), created.getTitle());
    }

    @Test
    @DisplayName("Создание дубликата музея должно завершиться ошибкой")
    @guru.qa.rococo.jupiter.annotation.Museum
    void museumWithDuplicateTitleShouldNotBeCreated(TestData data) {
        var existing = data.museums().getFirst();
        Museum req = Museum.newBuilder().setTitle(existing.title()).build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.createMuseum(req)
        );
        Assertions.assertEquals("ALREADY_EXISTS", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Создание музея с ID должно завершиться ошибкой")
    void museumWithNonEmptyIdShouldNotBeCreated() {
        Museum req = Museum.newBuilder().setId("abc").setTitle("X").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.createMuseum(req)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Обновление данных музея")
    @guru.qa.rococo.jupiter.annotation.Museum
    void museumShouldBeUpdated(TestData data) {
        var existing = data.museums().getFirst();
        Museum req = Museum.newBuilder()
                .setId(existing.id().toString())
                .setTitle(existing.title() + " updated")
                .setDescription(existing.description())
                .setCity("Minsk")
                .setCountryId(existing.geo().country().id().toString())
                .build();
        Museum updated = museumStub.updateMuseum(req);
        Assertions.assertEquals(req.getTitle(), updated.getTitle());
    }

    @Test
    void updateMuseum_whenNotFound_shouldReturnNotFound() {
        Museum req = Museum.newBuilder().setId(UUID.randomUUID().toString()).setTitle("x").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.updateMuseum(req)
        );
        Assertions.assertEquals("NOT_FOUND", ex.getStatus().getCode().toString());
    }

    @Test
    @DisplayName("Обновление несуществующего музея возвращает NOT_FOUND")
    void museumWithRandomIdShouldNotBeUpdated() {
        Museum req = Museum.newBuilder().setId("bad-uuid").setTitle("x").build();
        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.updateMuseum(req)
        );
        Assertions.assertEquals("INVALID_ARGUMENT", ex.getStatus().getCode().toString());
    }

    @Test
    @guru.qa.rococo.jupiter.annotation.Museum(title = "first___")
    @guru.qa.rococo.jupiter.annotation.Museum(title = "second___")
    @DisplayName("Обновление с дублирующим названием должно завершиться ошибкой")
    void museumWithDuplicateTitleShouldNotBeUpdated(TestData data) {
        MuseumJson first = data.museumByTitle("first___");
        MuseumJson second = data.museumByTitle("second___");
        Museum req = Museum.newBuilder()
                .setId(first.id().toString())
                .setTitle(second.title())
                .setCountryId(countryClient.findAll().getFirst().id().toString())
                .build();

        StatusRuntimeException ex = Assertions.assertThrows(
                StatusRuntimeException.class,
                () -> museumStub.updateMuseum(req)
        );
        Assertions.assertEquals("ALREADY_EXISTS", ex.getStatus().getCode().toString());
    }
}
