package guru.qa.rococo.service;

import guru.qa.grpc.rococo.painting.IdRequest;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.grpc.rococo.painting.PaintingRequest;
import guru.qa.grpc.rococo.painting.PaintingsResponse;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.InvalidUUIDException;
import guru.qa.rococo.ex.PaintingNotFoundException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class GrpcPaintingServiceTest {

    private PaintingRepository paintingRepository;
    private GrpcPaintingService service;

    @BeforeEach
    void setUp() {
        paintingRepository = mock(PaintingRepository.class);
        service = new GrpcPaintingService(paintingRepository);
    }

    @Test
    void getPaintings_shouldReturnPagedPaintings() {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setPage(0)
                .setSize(2)
                .setTitle("Sunset")
                .setArtistId("artist-123")
                .build();

        PaintingEntity entity1 = mock(PaintingEntity.class);
        PaintingEntity entity2 = mock(PaintingEntity.class);
        Painting grpc1 = Painting.newBuilder().setTitle("Sunset 1").build();
        Painting grpc2 = Painting.newBuilder().setTitle("Sunset 2").build();

        when(entity1.toGrpcMessage()).thenReturn(grpc1);
        when(entity2.toGrpcMessage()).thenReturn(grpc2);

        Page<PaintingEntity> page = new PageImpl<>(List.of(entity1, entity2));
        when(paintingRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);

        StreamObserver<PaintingsResponse> observer = mock(StreamObserver.class);
        service.getPaintings(request, observer);

        ArgumentCaptor<PaintingsResponse> captor = ArgumentCaptor.forClass(PaintingsResponse.class);
        verify(observer).onNext(captor.capture());
        verify(observer).onCompleted();

        PaintingsResponse response = captor.getValue();
        assertEquals(2, response.getPaintingsCount());
        assertEquals(2, response.getTotalCount());
        assertEquals("Sunset 1", response.getPaintings(0).getTitle());
        assertEquals("Sunset 2", response.getPaintings(1).getTitle());
    }

    @Test
    void getPainting_shouldReturnPainting() {
        UUID id = UUID.randomUUID();
        IdRequest request = IdRequest.newBuilder().setId(id.toString()).build();

        PaintingEntity entity = mock(PaintingEntity.class);
        Painting grpc = Painting.newBuilder().setId(id.toString()).build();

        when(entity.toGrpcMessage()).thenReturn(grpc);
        when(paintingRepository.findById(id)).thenReturn(Optional.of(entity));

        StreamObserver<Painting> observer = mock(StreamObserver.class);
        service.getPainting(request, observer);

        verify(observer).onNext(grpc);
        verify(observer).onCompleted();
    }

    @Test
    void getPainting_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        IdRequest request = IdRequest.newBuilder().setId(id.toString()).build();

        when(paintingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaintingNotFoundException.class, () ->
                service.getPainting(request, mock(StreamObserver.class)));
    }

    @Test
    void createPainting_shouldRejectIfIdPresent() {
        Painting request = Painting.newBuilder().setId("not-empty").build();
        StreamObserver<Painting> observer = mock(StreamObserver.class);

        service.createPainting(request, observer);

        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(observer).onError(captor.capture());

        StatusRuntimeException error = (StatusRuntimeException) captor.getValue();
        assertEquals("ID не должен быть задан при создании картины", error.getStatus().getDescription());
    }

    @Test
    void createPainting_shouldSaveAndReturn() {
        Painting request = Painting.newBuilder()
                .setTitle("Sunset 1")
                .setArtistId(UUID.randomUUID().toString())
                .build();
        PaintingEntity entity = mock(PaintingEntity.class);
        Painting grpc = Painting.newBuilder().setTitle("Created").build();

        when(paintingRepository.save(any())).thenReturn(entity);
        when(entity.toGrpcMessage()).thenReturn(grpc);

        StreamObserver<Painting> observer = mock(StreamObserver.class);
        service.createPainting(request, observer);
        verify(observer).onNext(grpc);
        verify(observer).onCompleted();
    }

    @Test
    void updatePainting_shouldUpdateExisting() {
        UUID id = UUID.randomUUID();
        UUID auhorId = UUID.randomUUID();
        Painting request = Painting.newBuilder()
                .setId(id.toString())
                .setArtistId(auhorId.toString())
                .setTitle("Updated")
                .build();

        PaintingEntity existing = mock(PaintingEntity.class);
        PaintingEntity updated = mock(PaintingEntity.class);
        Painting grpc = Painting.newBuilder().setTitle("Updated").build();

        when(paintingRepository.findById(id)).thenReturn(Optional.of(existing));
        when(paintingRepository.save(any())).thenReturn(updated);
        when(updated.toGrpcMessage()).thenReturn(grpc);

        StreamObserver<Painting> observer = mock(StreamObserver.class);
        service.updatePainting(request, observer);

        verify(observer).onNext(grpc);
        verify(observer).onCompleted();
    }

    @Test
    void updatePainting_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        Painting request = Painting.newBuilder().setId(id.toString()).build();

        when(paintingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(PaintingNotFoundException.class, () ->
                service.updatePainting(request, mock(StreamObserver.class)));
    }

    @Test
    void parseUuidOrThrow_shouldReturnValidUuid() {
        UUID id = UUID.randomUUID();
        UUID parsed = service.parseUuidOrThrow(id.toString());
        assertEquals(id, parsed);
    }

    @Test
    void parseUuidOrThrow_shouldThrowOnInvalidUuid() {
        assertThrows(InvalidUUIDException.class, () ->
                service.parseUuidOrThrow("not-a-uuid"));
    }
}