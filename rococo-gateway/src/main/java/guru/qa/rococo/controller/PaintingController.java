package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.PaintingJson;
import guru.qa.rococo.service.grpc.GrpcPaintingService;
import guru.qa.rococo.service.orchestration.PaintingOrchestrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/painting")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class PaintingController {
    private static final Logger LOG = LoggerFactory.getLogger(PaintingController.class);

    private final PaintingOrchestrationService paintingService;

    @Autowired
    public PaintingController(PaintingOrchestrationService paintingService) {
        this.paintingService = paintingService;
    }

    @GetMapping
    public Page<PaintingJson> getAll(@RequestParam(required = false) String title,
                                     @PageableDefault(size = 15) Pageable pageable) {
        return paintingService.getPaintings(title, null, pageable);
    }

    @PostMapping
    public PaintingJson create(@RequestBody PaintingJson painting) {
        return paintingService.createPainting(painting);
    }

    @PatchMapping
    public PaintingJson update(@RequestBody PaintingJson painting) {
        return paintingService.updatePainting(painting);
    }

    @GetMapping("/{id}")
    public PaintingJson getById(@PathVariable("id") String id) {
        return paintingService.getPainting(id);
    }

    @GetMapping("/author/{id}")
    public Page<PaintingJson> getByArtistId(@PathVariable("id") String id,
                                            @PageableDefault(size = 15) Pageable pageable) {
        return paintingService.getPaintings(null, id, pageable);
    }




}
