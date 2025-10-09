package guru.qa.rococo.service;

import guru.qa.grpc.rococo.artist.*;
import guru.qa.rococo.data.ArtistEntity;
import guru.qa.rococo.data.repository.ArtistRepository;
import guru.qa.rococo.ex.ArtistNotFoundException;
import guru.qa.rococo.ex.InvalidUUIDException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        Page<Artist> artists;
        if (request.getName().isEmpty()) {
            artists = artistRepository.findAll(pageable)
                    .map(ArtistEntity::toGrpcMessage);
        } else {
            artists = artistRepository.findAllByNameContainsIgnoreCase(request.getName(), pageable)
                    .map(ArtistEntity::toGrpcMessage);
        }
        responseObserver.onNext(ArtistsResponse.newBuilder()
                .addAllArtists(artists)
                .setTotalCount((int) artists.getTotalElements())
                .build());
        responseObserver.onCompleted();

    }


    @Override
    public void getArtist(IdRequest request, StreamObserver<Artist> responseObserver) {
        UUID id;
        try {
            id = UUID.fromString(request.getId());
        } catch (IllegalArgumentException e){
            throw new InvalidUUIDException("Некорректный UUID string: " + request.getId());
        }

        Artist artist = artistRepository.findById(UUID.fromString(id.toString()))
                .map(ArtistEntity::toGrpcMessage)
                .orElseThrow(() -> new ArtistNotFoundException(
                        String.format("Художник с ID %s не найден", request.getId()))
                );
        responseObserver.onNext(artist);
        responseObserver.onCompleted();
    }

    @Override
    public void createArtist(Artist request, StreamObserver<Artist> responseObserver) {
        if (!request.getId().isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("ID не должен быть задан при создании художника")
                            .asRuntimeException()
            );
            return;
        }

        boolean exists = artistRepository.existsByName(((request.getName())));
        if (exists) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Художник с таким именем уже существует")
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
        UUID id;
        try {
            id = UUID.fromString(request.getId());
        } catch (IllegalArgumentException e){
            throw new InvalidUUIDException("Некорректный UUID string: " + request.getId());
        }

        ArtistEntity ae = artistRepository
                .findById(UUID.fromString(id.toString()))
                .orElseThrow(() -> new ArtistNotFoundException(
                        String.format("Художник с ID %s не найден", request.getId()))
                );

        boolean exists = artistRepository.existsByName(((request.getName())));
        if (exists) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Художник с таким именем уже существует")
                            .asRuntimeException()
            );
            return;
        }

        ArtistEntity updatedArtist = artistRepository.save(ArtistEntity.fromGrpcMessage(request, ae));
        responseObserver.onNext(updatedArtist.toGrpcMessage());
        responseObserver.onCompleted();
    }

}


