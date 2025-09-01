package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.MuseumJson;
import guru.qa.rococo.service.orchestration.MuseumOrchestrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/museum")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class MuseumController {
    private static final Logger LOG = LoggerFactory.getLogger(MuseumController.class);

    private final MuseumOrchestrationService museumService;

    @Autowired
    public MuseumController(MuseumOrchestrationService museumService) {
        this.museumService = museumService;
    }

    @GetMapping
    public Page<MuseumJson> getAll(@PageableDefault(size = 15) Pageable pageable) {
        return museumService.getMuseums(pageable);
    }

    @PostMapping
    public MuseumJson create(@RequestBody MuseumJson museumJson) {
        return museumService.createMuseum(museumJson);
    }

    @PatchMapping
    public MuseumJson update(@RequestBody MuseumJson museumJson) {
        return museumService.updateMuseum(museumJson);
    }

    @GetMapping("/{id}")
    public MuseumJson getById(@PathVariable("id") String id) {
        return museumService.getMuseum(id);
    }
}
