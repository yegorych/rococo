package guru.qa.rococo.service;

import guru.qa.grpc.rococo.museum.*;
import guru.qa.rococo.data.MusuemEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.MuseumNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GrpcMuseumServiceTest {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private StreamObserver<MuseumsResponse> museumsObserver;

    @Mock
    private StreamObserver<Museum> museumObserver;

    @Captor
    private ArgumentCaptor<MuseumsResponse> museumsResponseCaptor;

    @Captor
    private ArgumentCaptor<Museum> museumResponseCaptor;

    @Captor
    private ArgumentCaptor<Throwable> errorCaptor;

    private GrpcMuseumService grpcMuseumService;

    @BeforeEach
    void setup() {
        grpcMuseumService = new GrpcMuseumService(museumRepository);
    }

    private MusuemEntity mockMuseumEntity(UUID id, String title) {
        MusuemEntity entity = new MusuemEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setDescription("Description for " + title);
        entity.setPhoto(new byte[]{1, 2, 3});
        return entity;
    }

    @Test
    void getMuseums_shouldReturnPagedMuseums() {
        List<MusuemEntity> list = List.of(
                mockMuseumEntity(UUID.randomUUID(), "Louvre"),
                mockMuseumEntity(UUID.randomUUID(), "Hermitage")
        );
        Page<MusuemEntity> page = new PageImpl<>(list);

        when(museumRepository.findAllByTitleContainsIgnoreCase(eq(""), any(Pageable.class)))
                .thenReturn(page);

        MuseumsRequest request = MuseumsRequest.newBuilder()
                .setTitle("")
                .setPage(0)
                .setSize(10)
                .build();

        grpcMuseumService.getMuseums(request, museumsObserver);

        verify(museumsObserver).onNext(museumsResponseCaptor.capture());
        verify(museumsObserver).onCompleted();

        MuseumsResponse resp = museumsResponseCaptor.getValue();
        assertEquals(2, resp.getMuseumCount());
        assertEquals(list.get(0).getTitle(), resp.getMuseum(0).getTitle());
        assertEquals(2, resp.getTotalCount());
    }

    @Test
    void getMuseums_shouldPassCorrectPageable() {
        when(museumRepository.findAllByTitleContainsIgnoreCase(eq("Modern"), any(Pageable.class)))
                .thenReturn(Page.empty());

        MuseumsRequest request = MuseumsRequest.newBuilder()
                .setTitle("Modern")
                .setPage(3)
                .setSize(15)
                .build();

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        grpcMuseumService.getMuseums(request, museumsObserver);

        verify(museumRepository).findAllByTitleContainsIgnoreCase(eq("Modern"), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(3, pageable.getPageNumber());
        assertEquals(15, pageable.getPageSize());
    }

    @Test
    void getMuseum_shouldReturnMuseum() {
        UUID id = UUID.randomUUID();
        MusuemEntity entity = mockMuseumEntity(id, "British Museum");
        when(museumRepository.findById(id)).thenReturn(Optional.of(entity));

        grpcMuseumService.getMuseum(
                IdRequest.newBuilder().setId(id.toString()).build(),
                museumObserver
        );

        verify(museumObserver).onNext(museumResponseCaptor.capture());
        verify(museumObserver).onCompleted();

        Museum resp = museumResponseCaptor.getValue();
        assertEquals("British Museum", resp.getTitle());
        assertEquals(id.toString(), resp.getId());
    }

    @Test
    void getMuseum_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(museumRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(MuseumNotFoundException.class, () ->
                grpcMuseumService.getMuseum(
                        IdRequest.newBuilder().setId(id.toString()).build(),
                        museumObserver
                )
        );
    }

    @Test
    void createMuseum_shouldSaveAndReturnMuseum() {
        MusuemEntity entity = mockMuseumEntity(UUID.randomUUID(), "Tate Modern");
        Museum req = Museum.newBuilder()
                .setTitle("Tate Modern")
                .setDescription("Art in London")
                .build();

        when(museumRepository.save(any(MusuemEntity.class))).thenReturn(entity);

        grpcMuseumService.createMuseum(req, museumObserver);

        verify(museumRepository).save(any(MusuemEntity.class));
        verify(museumObserver).onNext(museumResponseCaptor.capture());
        verify(museumObserver).onCompleted();

        assertEquals("Tate Modern", museumResponseCaptor.getValue().getTitle());
    }

    @Test
    void createMuseum_shouldFailIfIdIsProvided() {
        Museum req = Museum.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Invalid Museum")
                .build();

        grpcMuseumService.createMuseum(req, museumObserver);

        verify(museumObserver).onError(errorCaptor.capture());
        Throwable thrown = errorCaptor.getValue();

        assertTrue(thrown instanceof StatusRuntimeException);
        assertEquals(Status.INVALID_ARGUMENT.getCode(), ((StatusRuntimeException) thrown).getStatus().getCode());
    }

    @Test
    void updateMuseum_shouldUpdateMuseum() {
        UUID id = UUID.randomUUID();
        MusuemEntity existing = mockMuseumEntity(id, "Old Museum");
        MusuemEntity updated = mockMuseumEntity(id, "Updated Museum");

        when(museumRepository.findById(id)).thenReturn(Optional.of(existing));
        when(museumRepository.save(any(MusuemEntity.class))).thenReturn(updated);

        Museum req = Museum.newBuilder()
                .setId(id.toString())
                .setTitle("Updated Museum")
                .setDescription("Modernized")
                .build();

        grpcMuseumService.updateMuseum(req, museumObserver);

        verify(museumObserver).onNext(museumResponseCaptor.capture());
        verify(museumObserver).onCompleted();

        assertEquals("Updated Museum", museumResponseCaptor.getValue().getTitle());
    }

    @Test
    void updateMuseum_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(museumRepository.findById(id)).thenReturn(Optional.empty());

        Museum req = Museum.newBuilder()
                .setId(id.toString())
                .setTitle("Nonexistent Museum")
                .build();

        assertThrows(MuseumNotFoundException.class, () ->
                grpcMuseumService.updateMuseum(req, museumObserver)
        );
    }
}

