package guru.qa.rococo.service;

import guru.qa.grpc.rococo.museum.*;
import guru.qa.rococo.data.MusuemEntity;
import guru.qa.rococo.data.repository.MuseumRepository;
import guru.qa.rococo.ex.MuseumNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


@GrpcService
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {
    private final static Logger log = LoggerFactory.getLogger(GrpcMuseumService.class);

    private final MuseumRepository museumRepository;

    public GrpcMuseumService(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @Override
    public void getMuseums(MuseumsRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        List<Museum> museums = museumRepository.findAll(pageable)
                .map(MusuemEntity::toGrpcMessage)
                .toList();

        responseObserver.onNext(MuseumsResponse.newBuilder()
                .addAllMuseum(museums)
                .setTotalCount(museums.size())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getMuseum(IdRequest request, StreamObserver<Museum> responseObserver) {
        Museum museum = museumRepository.findById(UUID.fromString(request.getId()))
                .map(MusuemEntity::toGrpcMessage)
                .orElseThrow(() -> new MuseumNotFoundException("Музей не найден"));
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
        MusuemEntity createdPainting = museumRepository.save(MusuemEntity.fromGrpcMessage(request));
        responseObserver.onNext(createdPainting.toGrpcMessage());
        responseObserver.onCompleted();
    }

    @Override
    public void updateMuseum(Museum request, StreamObserver<Museum> responseObserver) {
        MusuemEntity pe = museumRepository
                .findById(UUID.fromString(request.getId()))
                .orElseThrow(() -> new MuseumNotFoundException("Музей не найден"));

        MusuemEntity updatedPainting = museumRepository.save(MusuemEntity.fromGrpcMessage(request, pe));
        responseObserver.onNext(updatedPainting.toGrpcMessage());
        responseObserver.onCompleted();
    }
}


