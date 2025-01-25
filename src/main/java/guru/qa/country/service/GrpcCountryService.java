package guru.qa.country.service;

import com.google.protobuf.Empty;
import guru.qa.country.model.Country;
import guru.qa.grpc.country.*;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrpcCountryService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryService countryService;

    @Autowired
    public GrpcCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public void getAllCountries(Empty request,
                                StreamObserver<CountryListResponse> responseObserver) {

        List<CountryResponse> countryResponse = countryService.getAll()
                .stream()
                .map(country -> CountryResponse.newBuilder()
                        .setId(country.id().toString())
                        .setCountryName(country.countryName())
                        .setCountryCode(country.countryCode())
                        .build())
                .toList();

        CountryListResponse countryListResponse = CountryListResponse.newBuilder()
                .addAllCountries(countryResponse)
                .build();

        responseObserver.onNext(countryListResponse);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<CountryRequest> addCountry(StreamObserver<CountryCountResponse> responseObserver) {
        return new StreamObserver<>() {

            int count = 0;

            @Override
            public void onNext(CountryRequest value) {
                countryService.saveCountry(new Country(null,
                        value.getCountryName(),
                        value.getCountryCode()));
                count++;
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Stream error: " + t.getMessage());
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(
                        CountryCountResponse.newBuilder()
                                .setCountryCount(count)
                                .build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void editCountry(UpdateCountryRequest request,
                            StreamObserver<CountryResponse> responseObserver) {

        Country country = countryService.updateCountryName(request.getName(),
                request.getNewName());

        responseObserver.onNext(CountryResponse.newBuilder()
                .setId(country.id().toString())
                .setCountryName(country.countryName())
                .setCountryCode(country.countryCode())
                .build());

        responseObserver.onCompleted();
    }
}
