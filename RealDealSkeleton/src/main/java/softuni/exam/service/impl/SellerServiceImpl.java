package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.ImportSellerDTO;
import softuni.exam.models.dtos.ImportSellerRootDTO;
import softuni.exam.models.entities.Seller;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;

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
public class SellerServiceImpl implements SellerService {
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    Path path = Path.of("src/main/resources/files/xml/sellers.xml");

    @Autowired
    public SellerServiceImpl(SellerRepository sellerRepository, ModelMapper modelMapper) throws JAXBException {
        this.sellerRepository = sellerRepository;
        this.modelMapper = modelMapper;

        JAXBContext context = JAXBContext.newInstance(ImportSellerRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.sellerRepository.count() >0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importSellers() throws IOException, JAXBException {
        ImportSellerRootDTO sellers = (ImportSellerRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return sellers.getSellers().stream()
                .map(this::importSeller).collect(Collectors.joining("\n"));
    }

    @Override
    public Seller findSellerById(Long id) {
        return this.sellerRepository.findSellerById(id).orElse(null);
    }

    private String importSeller(ImportSellerDTO seller) {
        Set<ConstraintViolation<ImportSellerDTO>> validate
                = this.validator.validate(seller);
        if (validate.isEmpty()){
            Seller sellerToAdd = this.modelMapper.map(seller,Seller.class);
            this.sellerRepository.save(sellerToAdd);
            return String.format("Successfully import seller %s - %s"
            ,sellerToAdd.getLastName(),sellerToAdd.getEmail());
        }else{
            return ("Invalid seller");
        }


    }
}
