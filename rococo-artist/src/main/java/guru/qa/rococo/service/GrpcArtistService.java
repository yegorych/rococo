package guru.qa.rococo.service;

import guru.qa.grpc.rococo.artist.*;
import guru.qa.grpc.rococo.painting.Painting;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.ArtistNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@GrpcService
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {

    private final static Logger log = LoggerFactory.getLogger(GrpcArtistService.class);

    private final ArtistRepository artistRepository;

    public GrpcArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    public void getArtists(ArtistRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        List<Artist> artists = new ArrayList<>();
        if (request.getName().isEmpty()) {
            artists.addAll(artistRepository.findAll(pageable)
                    .map(ArtistEntity::toGrpcMessage)
                    .getContent());
        } else {
            artists.addAll(artistRepository.findAllByNameContainsIgnoreCase(request.getName(), pageable)
                    .stream()
                    .map(ArtistEntity::toGrpcMessage)
                    .toList());
        }
        responseObserver.onNext(ArtistsResponse.newBuilder()
                .addAllArtists(artists)
                .setTotalCount(artists.size())
                .build());
        responseObserver.onCompleted();

    }


    @Override
    public void getArtist(IdRequest request, StreamObserver<Artist> responseObserver) {
        Artist artist = artistRepository.findById(UUID.fromString(request.getId()))
                .map(ArtistEntity::toGrpcMessage)
                .orElseThrow(() -> new ArtistNotFoundException("Художник не найдена"));
        responseObserver.onNext(artist);
        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(Artist request, StreamObserver<Artist> responseObserver) {
        if (!request.getId().isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Id не должен быть задан при создании худождника")
                            .asRuntimeException()
            );
            return;
        }
        ArtistEntity createdArtist = artistRepository.save(ArtistEntity.fromGrpcMessage(request));
        responseObserver.onNext(createdArtist.toGrpcMessage());
        responseObserver.onCompleted();

    }

    @Override
    public void updateArtist(Artist request, StreamObserver<Artist> responseObserver) {
        ArtistEntity ae = artistRepository
                .findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new ArtistNotFoundException("Художник не найден"));
        ArtistEntity updatedArtist = artistRepository.save(ArtistEntity.fromGrpcMessage(request, ae));
        responseObserver.onNext(updatedArtist.toGrpcMessage());
        responseObserver.onCompleted();
    }

}


