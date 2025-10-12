package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.ArtistJson;
import guru.qa.rococo.service.orchestration.ArtistOrchestrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/artist")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class ArtistController {
    private static final Logger LOG = LoggerFactory.getLogger(ArtistController.class);

    private final ArtistOrchestrationService artistService;

    @Autowired
    public ArtistController(ArtistOrchestrationService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public Page<ArtistJson> getAll(@RequestParam(required = false) String name,
                                     @PageableDefault(size = 15) Pageable pageable) {
        return artistService.getArtists(name, pageable);
    }

    @PostMapping
    public ArtistJson create(@Valid @RequestBody ArtistJson artistJson) {
        return artistService.createArtist(artistJson);
    }

    @PatchMapping
    public ArtistJson update(@Valid @RequestBody ArtistJson artistJson) {
        return artistService.updateArtist(artistJson);
    }

    @GetMapping("/{id}")
    public ArtistJson getById(@PathVariable("id") String id) {
        return artistService.getArtist(id);
    }
}
