package guru.qa.rococo.service.aggregator;

import guru.qa.grpc.rococo.geo.CountriesResponse;
import guru.qa.rococo.model.CountryJson;
import guru.qa.rococo.service.grpc.GrpcGeoService;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeoAggregatorService {
    private final GrpcGeoService geoService;

    @Autowired
    public GeoAggregatorService(GrpcGeoService geoService){
        this.geoService = geoService;
    }

    public Page<CountryJson> getCountries(@Nonnull Pageable pageable) {
        CountriesResponse response = geoService.getCountries(pageable);
        List<CountryJson> artistList = response.getCountriesList()
                .stream()
                .map(CountryJson::fromGrpcMessage)
                .toList();
        return new PageImpl<>(artistList, pageable, response.getTotalCount());
    }

    public CountryJson getCountry(@Nonnull String id) {
        return CountryJson.fromGrpcMessage(geoService.getCountry(id));
    }

}
