package guru.qa.rococo.service.grpc;

import guru.qa.grpc.rococo.museum.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component
public class GrpcMuseumService {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcMuseumService.class);

    @GrpcClient("grpcMuseumClient")
    private RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub rococoMuseumServiceBlockingStub;

    @Nonnull
    public MuseumsResponse getMuseums(@Nullable String title, @Nonnull Pageable pageable) {
        return rococoMuseumServiceBlockingStub.getMuseums(MuseumsRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setTitle(title != null ? title : "")
                .build());
    }

    public Museum createMuseum(Museum museum) {
        return rococoMuseumServiceBlockingStub.createMuseum(museum);
    }

    public Museum updateMuseum(Museum museum) {
        return rococoMuseumServiceBlockingStub.updateMuseum(museum);
    }

    public Museum getMuseum(@Nonnull String id) {
        return rococoMuseumServiceBlockingStub.getMuseum(IdRequest.newBuilder()
                        .setId(id)
                        .build());
    }
}
