package guru.qa.country.service;

import com.google.protobuf.Empty;
import guru.qa.country.data.CountryEntity;
import guru.qa.country.data.CountryRepository;
import guru.qa.country.model.Country;
import guru.qa.grpc.country.*;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GrpcCountryService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public GrpcCountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public void getAllCountries(Empty request,
                                StreamObserver<CountryListResponse> responseObserver) {

        List<CountryResponse> countryResponse = countryRepository.findAll()
                .stream()
                .map(Country::fromEntity)
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
                CountryEntity entity = new CountryEntity(null, value.getCountryName(), value.getCountryCode());
                countryRepository.save(entity);
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

        CountryEntity countryEntity = countryRepository.findByCountryName(request.getName())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with name: " + request.getName()));

        countryEntity.setCountryName(request.getNewName());
        CountryEntity updatedEntity = countryRepository.save(countryEntity);

        CountryResponse response = CountryResponse.newBuilder()
                .setId(updatedEntity.getId().toString())
                .setCountryName(updatedEntity.getCountryName())
                .setCountryCode(updatedEntity.getCountryCode())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
