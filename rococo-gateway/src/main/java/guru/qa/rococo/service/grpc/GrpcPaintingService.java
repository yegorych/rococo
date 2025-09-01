package guru.qa.rococo.service.grpc;

import guru.qa.grpc.rococo.painting.*;
import guru.qa.rococo.model.PaintingJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class GrpcPaintingService {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcPaintingService.class);

    @GrpcClient("grpcPaintingClient")
    private RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub rococoPaintingServiceBlockingStub;

    public PaintingsResponse getPaintings(@Nullable String title, @Nullable String artistId, @Nonnull Pageable pageable) {
        return rococoPaintingServiceBlockingStub.getPaintings(PaintingRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setTitle(title != null ? title : "")
                .setArtistId(artistId != null ? artistId : "")
                .build()
        );

//        return response.getPaintingsList()
//                .stream()
//                .map(PaintingJson::fromGrpcMessage)
//                .toList();
        //return new PageImpl<>(paintingJsonList, pageable, response.getTotalCount());
        //добавить сюда музеи гео и художники из соответствующих сервисов
    }

    public Painting createPainting(@Nonnull Painting painting) {
        return rococoPaintingServiceBlockingStub.createPainting(painting);
    }

    public Painting updatePainting(@Nonnull Painting painting) {
        return rococoPaintingServiceBlockingStub.updatePainting(painting);
    }

    public Painting getPaintingById(@Nonnull String id) {
        return rococoPaintingServiceBlockingStub.getPainting(IdRequest.newBuilder()
                        .setId(id)
                        .build()
        );
    }


}
