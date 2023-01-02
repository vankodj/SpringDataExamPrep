package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportPersonDTO;
import softuni.exam.models.entity.Country;
import softuni.exam.models.entity.Person;
import softuni.exam.repository.PersonRepository;
import softuni.exam.service.CountryService;
import softuni.exam.service.PersonService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final CountryService countryService;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final StringBuilder sb;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/json/people.json");

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, CountryService countryService, ModelMapper modelMapper, Gson gson) {
        this.personRepository = personRepository;
        this.countryService = countryService;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.sb = new StringBuilder();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.personRepository.count() >0;
    }

    @Override
    public String readPeopleFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importPeople() throws IOException, JAXBException {
       String json = this.readPeopleFromFile();
        ImportPersonDTO[] people = this.gson.fromJson(json,ImportPersonDTO[].class);

        for (ImportPersonDTO person : people) {

            Set<ConstraintViolation<ImportPersonDTO>> validate
                    = this.validator.validate(person);
            if (validate.isEmpty()){
            if (this.personRepository.findByFirstName(person.getFirstName()).isEmpty()&&
            this.personRepository.findByEmail(person.getEmail()).isEmpty()&&
            this.personRepository.findByPhone(person.getPhone()).isEmpty()){
                Country country = this.countryService.findById(person.getCountry());
                Person personToAdd = this.modelMapper.map(person,Person.class);
                personToAdd.setCountry(country);
                this.personRepository.save(personToAdd);
                sb.append(String.format("Successfully imported person %s %s"
                ,personToAdd.getFirstName(),personToAdd.getLastName())).append(System.lineSeparator());
            }else{
                sb.append("Invalid person").append(System.lineSeparator());
            }
            }else{
                sb.append("Invalid person").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}
