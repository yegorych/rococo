package guru.qa.rococo.service.aggregator;

import guru.qa.grpc.rococo.artist.ArtistsResponse;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.grpc.GrpcArtistService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ArtistAggregatorService {
    private final GrpcArtistService artistService;

    @Autowired
    public ArtistAggregatorService(GrpcArtistService artistService){
        this.artistService = artistService;
    }

    public Page<ArtistJson> getArtists(@Nullable String name, @Nonnull Pageable pageable) {
        ArtistsResponse response = artistService.getArtists(name, pageable);
        List<ArtistJson> artistList = response.getArtistsList()
                .stream()
                .map(ArtistJson::fromGrpcMessage)
                .toList();
        return new PageImpl<>(artistList, pageable, response.getTotalCount());
    }

    public ArtistJson createArtist(@Nonnull ArtistJson artistJson){
        return ArtistJson.fromGrpcMessage(artistService.createArtist(artistJson.toGrpcMessage()));
    }

    public ArtistJson updateArtist(@Nonnull ArtistJson artistJson){
        return ArtistJson.fromGrpcMessage(artistService.updateArtist(artistJson.toGrpcMessage()));
    }

    public ArtistJson getArtist(@Nonnull String id){
        return ArtistJson.fromGrpcMessage(artistService.getArtistById(id));
    }
}
