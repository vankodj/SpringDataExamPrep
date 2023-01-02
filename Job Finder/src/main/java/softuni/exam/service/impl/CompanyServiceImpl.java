package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportCompanyDTO;
import softuni.exam.models.dto.ImportCompanyRootDTO;
import softuni.exam.models.entity.Company;
import softuni.exam.models.entity.Country;
import softuni.exam.models.entity.Job;
import softuni.exam.repository.CompanyRepository;
import softuni.exam.repository.JobRepository;
import softuni.exam.service.CompanyService;
import softuni.exam.service.CountryService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CountryService countryService;
    private final JobRepository jobRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final Unmarshaller unmarshaller;
    Path path = Path.of("src/main/resources/files/xml/companies.xml");

    @Autowired
    public CompanyServiceImpl(CompanyRepository companyRepository, CountryService countryService, JobRepository jobRepository, ModelMapper modelMapper) throws JAXBException {
        this.companyRepository = companyRepository;
        this.countryService = countryService;
        this.jobRepository = jobRepository;
        this.modelMapper = modelMapper;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        JAXBContext context = JAXBContext.newInstance(ImportCompanyRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
    }


    @Override
    public boolean areImported() {
        return this.companyRepository.count() >0;
    }

    @Override
    public String readCompaniesFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importCompanies() throws IOException, JAXBException {
        ImportCompanyRootDTO companies = (ImportCompanyRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return companies.getCompanies().stream()
                .map(this::importCompany).collect(Collectors.joining("\n"));
    }

    @Override
    public Company findById(Long id) {
        return this.companyRepository.findById(id).orElse(null);
    }

    private String importCompany(ImportCompanyDTO company) {
        Set<ConstraintViolation<ImportCompanyDTO>> validate
                = this.validator.validate(company);
        if (validate.isEmpty()) {
            if (this.companyRepository.findByName(company.getCompanyName()).isEmpty()) {
                Country country = this.countryService.findById(company.getCountryId());
                Company companyToAdd = this.modelMapper.map(company, Company.class);
                companyToAdd.setCountry(country);
                this.companyRepository.save(companyToAdd);
                return String.format("Successfully imported company %s - %d"
                        , companyToAdd.getName(), companyToAdd.getCountry().getId());
            } else {
                return "Invalid company";
            }
        }else{
            return "Invalid company";
        }

    }
}
