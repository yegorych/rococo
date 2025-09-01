package guru.qa.rococo.service.orchestration;

import guru.qa.grpc.rococo.painting.PaintingsResponse;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.grpc.GrpcArtistService;
import guru.qa.rococo.service.grpc.GrpcGeoService;
import guru.qa.rococo.service.grpc.GrpcPaintingService;
import guru.qa.rococo.service.grpc.GrpcUserdataClient;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaintingOrchestrationService {
    private final GrpcPaintingService paintingService;
    private final GrpcArtistService artistService;

    @Autowired
    public PaintingOrchestrationService(GrpcPaintingService paintingService, GrpcArtistService artistService, GrpcGeoService geoService, GrpcUserdataClient userdataClient) {
        this.paintingService = paintingService;
        this.artistService = artistService;
    }

    public Page<PaintingJson> getPaintings(@Nullable String title, @Nullable String artistId, @Nonnull Pageable pageable) {
        PaintingsResponse response = paintingService.getPaintings(title, artistId, pageable);
        List<PaintingJson> paintingList = response.getPaintingsList()
                .stream()
                .map(PaintingJson::fromGrpcMessage)
                .map(paintingJson ->
                        paintingJson.addArtist(
                                ArtistJson.fromGrpcMessage(artistService.getArtistById(
                                        paintingJson.artist().id().toString()
                                ))
                        )
                )
                .toList();

        return new PageImpl<>(paintingList, pageable, response.getTotalCount());
    }

    public PaintingJson createPainting(@Nonnull PaintingJson paintingJson){
        return PaintingJson.fromGrpcMessage(paintingService.createPainting(paintingJson.toGrpcMessage()));
    }

    public PaintingJson updatePainting(@Nonnull PaintingJson paintingJson){
        return PaintingJson.fromGrpcMessage(paintingService.updatePainting(paintingJson.toGrpcMessage()));
    }

    public PaintingJson getPainting(@Nonnull String id){
        PaintingJson paintingJson = PaintingJson.fromGrpcMessage(paintingService.getPaintingById(id));
        return paintingJson.addArtist(
                ArtistJson.fromGrpcMessage(
                        artistService.getArtistById(paintingJson.artist().id().toString())
                )
        );
    }



}
