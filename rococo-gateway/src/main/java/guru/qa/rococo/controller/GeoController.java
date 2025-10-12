package guru.qa.rococo.controller;

import guru.qa.rococo.config.RococoGatewayServiceConfig;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.grpc.GrpcGeoService;
import guru.qa.rococo.service.orchestration.GeoOrchestrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/country")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class GeoController {
    private static final Logger LOG = LoggerFactory.getLogger(GeoController.class);

    private final GeoOrchestrationService gpcGeoService;

    @Autowired
    public GeoController(GeoOrchestrationService gpcGeoService) {
        this.gpcGeoService = gpcGeoService;
    }

    @GetMapping
    public Page<CountryJson> getAll(@PageableDefault(size = 15) Pageable pageable) {
        return gpcGeoService.getCountries(pageable);
    }
}
