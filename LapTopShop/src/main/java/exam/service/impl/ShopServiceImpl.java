package exam.service.impl;

import exam.model.dtos.ImportShopDTO;
import exam.model.dtos.ImportShopRootDTO;
import exam.model.entities.Shop;
import exam.model.entities.Town;
import exam.repository.ShopRepository;
import exam.repository.TownRepository;
import exam.service.ShopService;
import exam.service.TownService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {
    private final ShopRepository shopRepository;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final ModelMapper modelMapper;
    private final TownService townService;
    Path path = Path.of("src/main/resources/files/xml/shops.xml");

    @Autowired
    public ShopServiceImpl(ShopRepository shopRepository,TownService townService) throws JAXBException {
        this.shopRepository = shopRepository;
        this.townService = townService;

        JAXBContext context = JAXBContext.newInstance(ImportShopRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        this.modelMapper = new ModelMapper();
    }

    @Override
    public boolean areImported() {
        return this.shopRepository.count() >0;
    }

    @Override
    public String readShopsFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importShops() throws JAXBException, FileNotFoundException {
        ImportShopRootDTO shopDTOs = (ImportShopRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));
      return shopDTOs.getShops().stream()
               .map(this::importShop).collect(Collectors.joining("\n"));

    }

    @Override
    public Shop findByName(String name) {
        return this.shopRepository.findByName(name).orElse(null);
    }

    private String importShop(ImportShopDTO dto) {
        Set<ConstraintViolation<ImportShopDTO>> validate = this.validator.validate(dto);
        if (validate.isEmpty()){
            if (this.shopRepository.findByName(dto.getName()).isEmpty()){
                Town town = this.townService.findByName(dto.getTownName().getName());
                Shop shopToAdd = this.modelMapper.map(dto,Shop.class);
                shopToAdd.setTown(town);
                this.shopRepository.save(shopToAdd);
                return String.format("Successfully imported Shop %s - %.2f"
                ,shopToAdd.getName(),shopToAdd.getIncome());
            }else{
                return "Invalid shop";
            }
        }else {
            return "Invalid shop";
        }

    }
}
