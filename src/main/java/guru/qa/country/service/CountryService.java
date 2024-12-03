package guru.qa.country.service;

import guru.qa.country.data.CountryEntity;
import guru.qa.country.data.CountryRepository;
import guru.qa.country.model.Country;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static guru.qa.country.data.CountryEntity.fromJson;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> getAll() {
        return countryRepository.findAll().stream().map(Country::fromEntity).toList();
    }

    public Country saveCountry(Country country) {
        CountryEntity savedCountryEntity = countryRepository.save(fromJson(country));
        return Country.fromEntity(savedCountryEntity);
    }

    public Country updateCountryName(String name, String newName) {
        CountryEntity countryEntity = countryRepository.findByCountryName(name)
                .orElseThrow(() -> new EntityNotFoundException("Country not found with name: " + name));
        countryEntity.setCountryName(newName);
        countryRepository.save(countryEntity);
        return Country.fromEntity(countryEntity);
    }
}
