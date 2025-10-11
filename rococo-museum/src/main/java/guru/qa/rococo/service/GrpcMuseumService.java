package guru.qa.rococo.service;

import guru.qa.grpc.rococo.museum.*;
import guru.qa.rococo.data.MusuemEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.InvalidUUIDException;
import guru.qa.rococo.ex.MuseumNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


@GrpcService
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {
    private final MuseumRepository museumRepository;

    public GrpcMuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @Override
    public void getMuseums(MuseumsRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Museum> museums = museumRepository.findAllByTitleContainsIgnoreCase(request.getTitle(), pageable)
                .map(MusuemEntity::toGrpcMessage);

        responseObserver.onNext(MuseumsResponse.newBuilder()
                .addAllMuseum(museums)
                .setTotalCount((int) museums.getTotalElements())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMuseum(IdRequest request, StreamObserver<Museum> responseObserver) {
        UUID id = parseUuidOrThrow(request.getId());
        Museum museum = museumRepository.findById(UUID.fromString(id.toString()))
                .map(MusuemEntity::toGrpcMessage)
                .orElseThrow(() -> new MuseumNotFoundException(
                        String.format("Музей с ID %s не найден", id))
                );
        responseObserver.onNext(museum);
        responseObserver.onCompleted();
    }

    @Override
    public void createMuseum(Museum request, StreamObserver<Museum> responseObserver) {
        if (!request.getId().isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("ID не должен быть задан при создании музея")
                            .asRuntimeException()
            );
            return;
        }

        boolean exists = museumRepository.existsByTitle(request.getTitle());
        if (exists) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Музей с таким названием уже существует")
                            .asRuntimeException()
            );
            return;
        }

        MusuemEntity createdPainting = museumRepository.save(MusuemEntity.fromGrpcMessage(request));
        responseObserver.onNext(createdPainting.toGrpcMessage());
        responseObserver.onCompleted();
    }

    @Override
    public void updateMuseum(Museum request, StreamObserver<Museum> responseObserver) {
        UUID id = parseUuidOrThrow(request.getId());

        boolean exists = museumRepository.existsByTitle(request.getTitle());
        if (exists) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Музей с таким названием уже существует")
                            .asRuntimeException()
            );
            return;
        }

        MusuemEntity pe = museumRepository
                .findById(UUID.fromString(id.toString()))
                .orElseThrow(() -> new MuseumNotFoundException(
                        String.format("Музей с ID %s не найден", id))
                );

        MusuemEntity updatedPainting = museumRepository.save(MusuemEntity.fromGrpcMessage(request, pe));
        responseObserver.onNext(updatedPainting.toGrpcMessage());
        responseObserver.onCompleted();
    }

    private UUID parseUuidOrThrow(String uuidString) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Некорректный UUID string: " + uuidString);
        }
    }
}


