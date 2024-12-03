package guru.qa.country.controller;

import guru.qa.country.model.Country;
import guru.qa.country.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CountryController {

    private final CountryService countryService;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/all")
    public List<Country> getAll() {
        return countryService.getAll();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Country addSCountry(@RequestBody Country country) {
        return countryService.saveCountry(country);
    }

    @PatchMapping("/edit/{name}")
    public Country updateCountryName(@PathVariable String name, @RequestBody String newName) {
        return countryService.updateCountryName(name, newName);
    }
}

