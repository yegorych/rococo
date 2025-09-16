package guru.qa.rococo.service;

import guru.qa.grpc.rococo.geo.*;
import guru.qa.grpc.rococo.museum.Museum;
import guru.qa.rococo.data.CountryEntity;
import guru.qa.rococo.data.repository.CountryRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;


@GrpcService
public class GrpcGeoService extends RococoGeoServiceGrpc.RococoGeoServiceImplBase {

    private final static Logger log = LoggerFactory.getLogger(GrpcGeoService.class);

    private final CountryRepository countryRepository;

    public GrpcGeoService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getCountries(CountriesRequest request, StreamObserver<CountriesResponse> responseObserver) {
        try {
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
            Page<Country> countries = countryRepository.findAll(pageable)
                    .map(ce -> Country.newBuilder()
                            .setId(ce.getId().toString())
                            .setName(ce.getName())
                            .build());

            responseObserver.onNext(CountriesResponse.newBuilder()
                    .addAllCountries(countries)
                    .setTotalCount((int) countries.getTotalElements())
                    .build()
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Failed to fetch countries", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Ошибка при получении списка стран")
                            .withCause(e)
                            .asRuntimeException()
            );
        }

    }

    @Override
    public void getCounty(IdRequest request, StreamObserver<Country> responseObserver) {
        try {
            Country country = countryRepository.findById(UUID.fromString(request.getId()))
                    .map(CountryEntity::toGrpcMessage)
                    .orElseThrow();
            responseObserver.onNext(country);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Country not found", e);
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Страна с таким ID не найдена")
                            .withCause(e)
                            .asRuntimeException()
            );
        }



    }
}


