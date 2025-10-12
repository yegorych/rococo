package guru.qa.rococo.service;

import guru.qa.grpc.rococo.artist.*;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.ArtistNotFoundException;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcArtistServiceTest {

    @Mock
    ArtistRepository artistRepository;
    @Mock
    StreamObserver<ArtistsResponse> artistsObserver;
    @Mock
    StreamObserver<Artist> artistObserver;
    @Captor
    ArgumentCaptor<Artist> artistCaptor;
    @Captor
    ArgumentCaptor<ArtistEntity> artistEntityCaptor;
    @Captor
    ArgumentCaptor<Pageable> pageableCaptor;
    GrpcArtistService grpcArtistService;

    @BeforeEach
    void setUp() {
        grpcArtistService = new GrpcArtistService(artistRepository);
    }

    @Test
    void getArtists_pagination_shouldPassCorrectPageable() {
        List<ArtistEntity> list = List.of(
                mockArtistEntity(UUID.randomUUID(), "A"),
                mockArtistEntity(UUID.randomUUID(), "B")
        );
        Page<ArtistEntity> page = new PageImpl<>(list);

        when(artistRepository.findAll(any(Pageable.class))).thenReturn(page);

        ArtistRequest request = ArtistRequest.newBuilder()
                .setPage(2)
                .setSize(5)
                .setName("")  // без фильтра
                .build();

        grpcArtistService.getArtists(request, artistsObserver);

        verify(artistRepository).findAll(pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(2, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
    }

    @Test
    void getArtists_pagination_withFilter_shouldPassCorrectPageable() {
        List<ArtistEntity> list = List.of(mockArtistEntity(UUID.randomUUID(), "Picasso"));
        Page<ArtistEntity> page = new PageImpl<>(list);

        when(artistRepository.findAllByNameContainsIgnoreCase(eq("Pic"), any(Pageable.class)))
                .thenReturn(page);

        ArtistRequest request = ArtistRequest.newBuilder()
                .setPage(1)
                .setSize(20)
                .setName("Pic")
                .build();

        grpcArtistService.getArtists(request, artistsObserver);

        verify(artistRepository).findAllByNameContainsIgnoreCase(eq("Pic"), pageableCaptor.capture());
        Pageable pageable = pageableCaptor.getValue();

        assertEquals(1, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }



    private ArtistEntity mockArtistEntity(UUID id, String name) {
        ArtistEntity ae = new ArtistEntity();
        ae.setId(id);
        ae.setName(name);
        ae.setBiography("Bio of " + name);
        ae.setPhoto(new byte[]{1,2,3});
        return ae;
    }

    @Test
    void getArtists_withoutFilter_shouldReturnAll() {
        List<ArtistEntity> list = List.of(
                mockArtistEntity(UUID.randomUUID(), "A"),
                mockArtistEntity(UUID.randomUUID(), "B")
        );
        Page<ArtistEntity> page = new PageImpl<>(list);

        when(artistRepository.findAll(any(Pageable.class))).thenReturn(page);

        ArtistRequest request = ArtistRequest.newBuilder().setPage(0).setSize(10).setName("").build();
        grpcArtistService.getArtists(request, artistsObserver);

        verify(artistsObserver).onNext(any(ArtistsResponse.class));
        verify(artistsObserver).onCompleted();
    }

    @Test
    void getArtists_withFilter_shouldReturnFiltered() {
        List<ArtistEntity> list = List.of(mockArtistEntity(UUID.randomUUID(), "Picasso"));
        Page<ArtistEntity> page = new PageImpl<>(list);

        when(artistRepository.findAllByNameContainsIgnoreCase(eq("Pic"), any(Pageable.class)))
                .thenReturn(page);

        ArtistRequest request = ArtistRequest.newBuilder().setPage(0).setSize(10).setName("Pic").build();
        grpcArtistService.getArtists(request, artistsObserver);

        verify(artistsObserver).onNext(any(ArtistsResponse.class));
        verify(artistsObserver).onCompleted();
    }

    @Test
    void getArtist_shouldReturnArtist() {
        UUID id = UUID.randomUUID();
        ArtistEntity ae = mockArtistEntity(id, "Rembrandt");
        when(artistRepository.findById(id)).thenReturn(Optional.of(ae));

        grpcArtistService.getArtist(IdRequest.newBuilder().setId(id.toString()).build(), artistObserver);

        verify(artistObserver).onNext(artistCaptor.capture());
        assertEquals("Rembrandt", artistCaptor.getValue().getName());
        verify(artistObserver).onCompleted();
    }

    @Test
    void getArtist_notFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () ->
                grpcArtistService.getArtist(IdRequest.newBuilder().setId(id.toString()).build(), artistObserver)
        );
    }

    @Test
    void createArtist_shouldSaveAndReturn() {
        Artist request = Artist.newBuilder()
                .setName("Van Gogh")
                .setBiography("Bio")
                .setPhoto(com.google.protobuf.ByteString.copyFrom(new byte[]{9,9,9}))
                .build();

        ArtistEntity saved = mockArtistEntity(UUID.randomUUID(), "Van Gogh");

        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(saved);

        grpcArtistService.createArtist(request, artistObserver);

        verify(artistRepository).save(artistEntityCaptor.capture());
        ArtistEntity passed = artistEntityCaptor.getValue();

        assertEquals("Van Gogh", passed.getName());
        assertEquals("Bio", passed.getBiography());
        assertArrayEquals(new byte[]{9,9,9}, passed.getPhoto());

        verify(artistObserver).onNext(any(Artist.class));
        verify(artistObserver).onCompleted();
    }

    @Test
    void createArtist_withId_shouldFail() {
        Artist request = Artist.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName("Should Fail")
                .build();

        grpcArtistService.createArtist(request, artistObserver);

        verify(artistObserver).onError(any(StatusRuntimeException.class));
    }

    @Test
    void updateArtist_shouldUpdate() {
        UUID id = UUID.randomUUID();
        ArtistEntity existing = mockArtistEntity(id, "Old Name");
        when(artistRepository.findById(id)).thenReturn(Optional.of(existing));

        Artist request = Artist.newBuilder()
                .setId(id.toString())
                .setName("New Name")
                .setBiography("New Bio")
                .setPhoto(com.google.protobuf.ByteString.copyFrom(new byte[]{5,5,5}))
                .build();

        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(existing);

        grpcArtistService.updateArtist(request, artistObserver);

        verify(artistRepository).save(artistEntityCaptor.capture());
        ArtistEntity updated = artistEntityCaptor.getValue();
        assertEquals("New Name", updated.getName());
        assertEquals("New Bio", updated.getBiography());
        assertArrayEquals(new byte[]{5,5,5}, updated.getPhoto());

        verify(artistObserver).onNext(any(Artist.class));
        verify(artistObserver).onCompleted();
    }
}
