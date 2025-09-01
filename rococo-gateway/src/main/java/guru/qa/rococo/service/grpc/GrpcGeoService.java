package guru.qa.rococo.service.grpc;

import guru.qa.grpc.rococo.geo.*;
import guru.qa.rococo.model.CountryJson;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GrpcGeoService {
    private static final Logger LOG = LoggerFactory.getLogger(GrpcGeoService.class);

    @GrpcClient("grpcGeoClient")
    private RococoGeoServiceGrpc.RococoGeoServiceBlockingStub rococoGeoServiceBlockingStub;

    @Nonnull
    public CountriesResponse getCountries(@Nonnull Pageable pageable) {
        return rococoGeoServiceBlockingStub.getCountries(CountriesRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .build());
    }

    @Nonnull
    public Country getCountry(@Nonnull String id) {
        return rococoGeoServiceBlockingStub.getCounty(IdRequest.newBuilder().setId(id).build());
    }
}
