package guru.qa.rococo.service;

import guru.qa.grpc.rococo.painting.*;
import guru.qa.rococo.data.PaintingEntity;
import guru.qa.rococo.data.PaintingSpecifications;
import guru.qa.rococo.data.repository.PaintingRepository;
import guru.qa.rococo.ex.PaintingNotFoundException;
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
public class GrpcPaintingService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {
    private final static Logger log = LoggerFactory.getLogger(GrpcPaintingService.class);

    private final PaintingRepository paintingRepository;

    public GrpcPaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository =  paintingRepository;
    }

    @Override
    public void getPaintings(PaintingRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Painting> paintings = paintingRepository.findAll(
                PaintingSpecifications.withFilter(request.getTitle(), request.getArtistId()), pageable)
                .map(PaintingEntity::toGrpcMessage);

        responseObserver.onNext(PaintingsResponse.newBuilder()
                .addAllPaintings(paintings)
                .setTotalCount((int) paintings.getTotalElements())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getPainting(IdRequest request, StreamObserver<Painting> responseObserver) {
        Painting painting = paintingRepository.findById(UUID.fromString(request.getId()))
                .map(PaintingEntity::toGrpcMessage)
                .orElseThrow(() -> new PaintingNotFoundException("Картина не найдена"));
        responseObserver.onNext(painting);
        responseObserver.onCompleted();

    }

    @Override
    public void createPainting(Painting request, StreamObserver<Painting> responseObserver) {
        if (!request.getId().isEmpty()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("ID не должен быть задан при создании картины")
                            .asRuntimeException()
            );
            return;
        }
        PaintingEntity createdPainting = paintingRepository.save(PaintingEntity.fromGrpcMessage(request));
        responseObserver.onNext(createdPainting.toGrpcMessage());
        responseObserver.onCompleted();
    }

    @Override
    public void updatePainting(Painting request, StreamObserver<Painting> responseObserver) {
        PaintingEntity pe = paintingRepository
                    .findById(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new PaintingNotFoundException("Картина не найдена"));

        PaintingEntity updatedPainting = paintingRepository.save(PaintingEntity.fromGrpcMessage(request, pe));
        responseObserver.onNext(updatedPainting.toGrpcMessage());
        responseObserver.onCompleted();
    }
}


