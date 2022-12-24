package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportApartmentDTO;
import softuni.exam.models.dto.ImportApartmentRootDTO;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.service.ApartmentService;
import softuni.exam.service.TownService;

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
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final TownService townService;
    private final Validator validator;
    private final Unmarshaller unmarshaller;
    private final Path path =Path.of("src/main/resources/files/xml/apartments.xml");
    private final StringBuilder sb;

    @Autowired
    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, Gson gson, ModelMapper modelMapper, TownService townService) throws JAXBException {
        this.apartmentRepository = apartmentRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.townService = townService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        JAXBContext context = JAXBContext.newInstance(ImportApartmentRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.sb = new StringBuilder();
    }

    @Override
    public boolean areImported() {
        return this.apartmentRepository.count() >0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        ImportApartmentRootDTO apartments = (ImportApartmentRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return apartments.getApartments().stream()
                .map(this::importApartment).collect(Collectors.joining("\n"));
    }

    @Override
    public Apartment findApartmentById(Long id) {
        return this.apartmentRepository.findApartmentById(id).orElse(null);
    }

    private String importApartment(ImportApartmentDTO dto) {
        Set<ConstraintViolation<ImportApartmentDTO>> validate = this.validator.validate(dto);
        if (validate.isEmpty()){
        if (this.apartmentRepository.findByTownAndArea(this.townService.findByTownName(dto.getTown()),dto.getArea()).isEmpty()){
            Town town = this.townService.findByTownName(dto.getTown());
            Apartment apartmentToAdd = this.modelMapper.map(dto,Apartment.class);
            apartmentToAdd.setTown(town);
            this.apartmentRepository.save(apartmentToAdd);
            sb.append(String.format("Successfully imported apartment %s - %.2f"
            ,apartmentToAdd.getApartmentType(),apartmentToAdd.getArea()));
        }else{
            sb.append("Invalid apartment");
        }
        }else{
            sb.append("Invalid apartment");
        }
return sb.toString();
    }
}
