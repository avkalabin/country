package guru.qa.country.service;

import guru.qa.country.config.AppConfig;

import guru.qa.country.data.CountryEntity;
import guru.qa.country.data.CountryRepository;
import guru.qa.xml.country.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;


@Endpoint
public class CountrySoapService {
    private final CountryRepository countryRepository;

    @Autowired
    public CountrySoapService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "GetAllCountriesRequest")
    @ResponsePayload
    public CountryListResponse getAllCountries() {
        CountryListResponse response = new CountryListResponse();
        List<CountryEntity> countries = countryRepository.findAll();

        response.getCountryList().addAll(
                countries.stream().map(countryEntity -> {
                    Country country = new Country();
                    country.setId(countryEntity.getId().toString());
                    country.setCountryName(countryEntity.getCountryName());
                    country.setCountryCode(countryEntity.getCountryCode());
                    return country;
                }).toList()
        );

        return response;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "AddCountryRequest")
    @ResponsePayload
    public CountryResponse addCountry(@RequestPayload AddCountryRequest request) {
        CountryResponse response = new CountryResponse();

        CountryEntity countryEntity = countryRepository.save(
                new CountryEntity(
                        null,
                        request.getCountryName(),
                        request.getCountryCode()));

        Country country = new Country();
        country.setId(countryEntity.getId().toString());
        country.setCountryName(countryEntity.getCountryName());
        country.setCountryCode(countryEntity.getCountryCode());

        response.setCountry(country);
        return response;
    }

    @PayloadRoot(namespace = AppConfig.SOAP_NAMESPACE, localPart = "UpdateCountryRequest")
    @ResponsePayload
    public CountryResponse editCountry(@RequestPayload UpdateCountryRequest request) {
        CountryResponse response = new CountryResponse();

        CountryEntity countryEntity = countryRepository.findByCountryName(request.getName())
                .orElseThrow(() -> new EntityNotFoundException("Country not found with name: " + request.getName()));
        countryEntity.setCountryName(request.getNewName());
        countryRepository.save(countryEntity);

        Country country = new Country();
        country.setId(countryEntity.getId().toString());
        country.setCountryName(countryEntity.getCountryName());
        country.setCountryCode(countryEntity.getCountryCode());

        response.setCountry(country);
        return response;
    }
}