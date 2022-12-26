package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dtos.ImportCustomerDTO;
import exam.model.entities.Customer;
import exam.model.entities.Town;
import exam.repository.CustomerRepository;
import exam.service.CustomerService;
import exam.service.TownService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final Validator validator;
    private final TownService townService;



    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, Gson gson, TownService townService) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;


        this.gson = gson;
        this.townService = townService;

        this.validator = Validation.buildDefaultValidatorFactory().getValidator();




    }

    @Override
    public boolean areImported() {
        return this.customerRepository.count() >0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
       Path path = Path.of("src/main/resources/files/json/customers.json");
        return Files.readString(path);
    }

    @Override
    public String importCustomers() throws IOException {
        StringBuilder sb = new StringBuilder();
        String json = this.readCustomersFileContent();
        ImportCustomerDTO[] customers= gson.fromJson(json,ImportCustomerDTO[].class);

        for (ImportCustomerDTO customer : customers) {
            Set<ConstraintViolation<ImportCustomerDTO>> validate
                    = this.validator.validate(customer);
            if (validate.isEmpty()){
                if (this.customerRepository.findByEmail(customer.getEmail()).isEmpty()){
                    Customer customerToAdd = this.modelMapper.map(customer,Customer.class);
                    Town town = this.townService.findByName(customer.getTown().getName());
                    customerToAdd.setTown(town);
                    this.customerRepository.save(customerToAdd);
                    sb.append(String.format("Successfully imported Customer %s %s - %s"
                            ,customerToAdd.getFirstName()
                            ,customerToAdd.getLastName()
                            ,customerToAdd.getEmail()));
                }else{
                    sb.append("Invalid Customer").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid Customer").append(System.lineSeparator());
            }

        }


        return sb.toString();
    }
}
