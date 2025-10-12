package guru.qa.rococo.service.aggregator;

import guru.qa.grpc.rococo.painting.PaintingsResponse;
import guru.qa.rococo.ex.NotFoundException;
import guru.qa.rococo.model.*;
import guru.qa.rococo.model.Event;
import guru.qa.rococo.model.EventType;
import guru.qa.rococo.service.grpc.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PaintingAggregatorService {
    private static final Logger LOG = LoggerFactory.getLogger(PaintingAggregatorService.class);
    private final GrpcPaintingService paintingService;
    private final GrpcArtistService artistService;
    private final GrpcMuseumService museumService;
    private final GrpcGeoService geoService;
    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Autowired
    public PaintingAggregatorService(GrpcPaintingService paintingService, GrpcArtistService artistService, GrpcMuseumService museumService, GrpcGeoService geoService, KafkaTemplate<String, Event> kafkaTemplate) {
        this.paintingService = paintingService;
        this.artistService = artistService;
        this.museumService = museumService;
        this.geoService = geoService;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Page<PaintingJson> getPaintings(@Nullable String title, @Nullable String artistId, @Nonnull Pageable pageable) {
        PaintingsResponse response = paintingService.getPaintings(title, artistId, pageable);
        List<PaintingJson> paintingList = response.getPaintingsList()
                .stream()
                .map(PaintingJson::fromGrpcMessage)
                .map(pj ->
                        pj.addArtist(
                                ArtistJson.fromGrpcMessage(artistService.getArtistById(
                                        pj.artist().id().toString()
                                ))
                        )
                )
                .map(pj -> {
                        if (pj.museum().id() != null) {
                            MuseumJson museumJson =  MuseumJson.fromGrpcMessage(museumService.getMuseum(pj.museum().id().toString()));
                            return pj.addMuseum(museumJson.addCountry(
                                    CountryJson.fromGrpcMessage(geoService.getCountry(museumJson.geo().country().id().toString())))
                            );
                        }
                        return pj;
                    }
                )
                .toList();

        return new PageImpl<>(paintingList, pageable, response.getTotalCount());
    }

    public PaintingJson createPainting(@Nonnull String username, @Nonnull PaintingJson paintingJson){
        PaintingJson painting = PaintingJson.fromGrpcMessage(
                paintingService.createPainting(
                        paintingJson.toGrpcMessage()
                )
        );
        if (painting.id() != null) {
            Event event = new Event(
                    username,
                    painting.id().toString(),
                    EventType.CREATE,
                    new Date()
            );
            kafkaTemplate.send("events", event);
            LOG.info("### Kafka topic [events] sent message: {}", event);
        }
        return painting;

    }

    public PaintingJson updatePainting(@Nonnull PaintingJson paintingJson){
        PaintingJson painting =
                PaintingJson.fromGrpcMessage(paintingService.updatePainting(paintingJson.toGrpcMessage()))
                .addArtist(ArtistJson.fromGrpcMessage(
                        artistService.getArtistById(paintingJson.artist().id().toString()))
                );
//        if (painting.id() != null) {
//            Event event = new Event(
//                    username,
//                    painting.id().toString(),
//                    EventType.CREATE,
//                    new Date()
//            );
//            kafkaTemplate.send("events", event);
//            LOG.info("### Kafka topic [events] sent message: {}", event);
//        }
        return painting;
    }

    public PaintingJson getPainting(@Nonnull String id){
        PaintingJson paintingJson = PaintingJson.fromGrpcMessage(paintingService.getPaintingById(id));

        ArtistJson artistJson = paintingJson.getArtistId()
                .map(artistId -> ArtistJson.fromGrpcMessage(artistService.getArtistById(artistId)))
                .orElseThrow(() -> new NotFoundException("Художник не найден"));

        paintingJson = paintingJson.addArtist(artistJson);

        if (paintingJson.getMuseumId().isPresent()) {
            MuseumJson museumJson = MuseumJson.fromGrpcMessage(
                    museumService.getMuseum(paintingJson.getMuseumId().get()));
            CountryJson countryJson = CountryJson.fromGrpcMessage(
                    geoService.getCountry(museumJson.geo().country().id().toString()));
            paintingJson = paintingJson.addMuseum(museumJson.addCountry(countryJson));
        }
        return paintingJson;
    }



}
