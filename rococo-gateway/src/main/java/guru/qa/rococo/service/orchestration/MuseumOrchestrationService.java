package guru.qa.rococo.service.orchestration;

import guru.qa.grpc.rococo.artist.ArtistsResponse;
import guru.qa.grpc.rococo.museum.MuseumsResponse;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.grpc.GrpcArtistService;
import guru.qa.rococo.service.grpc.GrpcGeoService;
import guru.qa.rococo.service.grpc.GrpcMuseumService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MuseumOrchestrationService {
    private final GrpcMuseumService museumService;
    private final GeoOrchestrationService geoService;

    @Autowired
    public MuseumOrchestrationService(GrpcMuseumService museumService, GeoOrchestrationService geoService) {
        this.museumService = museumService;
        this.geoService = geoService;
    }

    @Nonnull
    public Page<MuseumJson> getMuseums(@Nonnull Pageable pageable) {
        MuseumsResponse response = museumService.getMuseums(pageable);
        List<MuseumJson> museumList = response.getMuseumList()
                .stream()
                .map(MuseumJson::fromGrpcMessage)
                .map(mj -> mj.addCountry(geoService.getCountry(mj.geo().country().id().toString())))
                .toList();
        return new PageImpl<>(museumList, pageable, response.getTotalCount());
    }

    public MuseumJson createMuseum(@Nonnull MuseumJson museumJson){
        return MuseumJson.fromGrpcMessage(museumService.createMuseum(museumJson.toGrpcMessage()))
                .addCountry(geoService.getCountry(museumJson.geo().country().id().toString()));
    }

    public MuseumJson updateMuseum(@Nonnull MuseumJson museumJson){
        return MuseumJson.fromGrpcMessage(museumService.updateMuseum(museumJson.toGrpcMessage()))
                .addCountry(geoService.getCountry(museumJson.geo().country().id().toString()));
    }

    public MuseumJson getMuseum(@Nonnull String id){
        MuseumJson museum = MuseumJson.fromGrpcMessage(museumService.getMuseum(id));
        return museum.addCountry(geoService.getCountry(museum.geo().country().id().toString()));
    }

}
