package guru.qa.country.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.country.data.CountryEntity;

public record Country(
        @JsonProperty("country_name") String countryName,
        @JsonProperty("country_code") String countryCode) {

    public static Country fromEntity(CountryEntity ce) {
        return new Country(ce.getCountryName(), ce.getCountryCode());
    }
}
