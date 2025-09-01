package guru.qa.rococo.service.grpc;

import guru.qa.grpc.rococo.artist.*;
import guru.qa.rococo.model.ArtistJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class GrpcArtistService {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcArtistService.class);

    @GrpcClient("grpcArtistClient")
    private RococoArtistServiceGrpc.RococoArtistServiceBlockingStub rococoArtistServiceBlockingStub;

    public ArtistsResponse getArtists(@Nullable String name, @Nonnull Pageable pageable) {
        return rococoArtistServiceBlockingStub.getArtists(ArtistRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setName(name != null ? name : "")
                .build());
    }

    public Artist createArtist(Artist artist) {
        return rococoArtistServiceBlockingStub.createArtist(artist);
    }

    public Artist updateArtist(Artist artist) {
        return rococoArtistServiceBlockingStub.updateArtist(artist);
    }

    public Artist getArtistById(@Nonnull String id) {
        return rococoArtistServiceBlockingStub.getArtist(IdRequest.newBuilder()
                        .setId(id)
                        .build());
    }
}
