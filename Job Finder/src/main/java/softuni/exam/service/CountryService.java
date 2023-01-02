package softuni.exam.service;


import softuni.exam.models.entity.Country;

import java.io.IOException;

// TODO: Implement all methods
public interface CountryService {

    boolean areImported();

    String readCountriesFileContent() throws IOException;

    String importCountries() throws IOException;

    Country findById(Long id);
}
