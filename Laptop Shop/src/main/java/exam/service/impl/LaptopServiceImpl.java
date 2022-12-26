package exam.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exam.model.dtos.ImportLaptopDTO;
import exam.model.entities.Laptop;
import exam.model.entities.Shop;
import exam.model.entities.WarrantyType;
import exam.repository.LaptopRepository;
import exam.service.LaptopService;
import exam.service.ShopService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
public class LaptopServiceImpl implements LaptopService {
    private final LaptopRepository laptopRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final ShopService shopService;

    @Autowired
    public LaptopServiceImpl(LaptopRepository laptopRepository, ShopService shopService) {
        this.laptopRepository = laptopRepository;
        this.shopService = shopService;
        this.gson = new GsonBuilder().create();
        this.modelMapper = new ModelMapper();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean areImported() {
        return this.laptopRepository.count() >0;
    }

    @Override
    public String readLaptopsFileContent() throws IOException {
        Path path = Path.of("src/main/resources/files/json/laptops.json");
        return Files.readString(path);
    }

    @Override
    public String importLaptops() throws IOException {
       StringBuilder sb = new StringBuilder();
        String json = this.readLaptopsFileContent();
        ImportLaptopDTO[] laptops = this.gson.fromJson(json,ImportLaptopDTO[].class);
        for (ImportLaptopDTO laptop : laptops) {
            Set<ConstraintViolation<ImportLaptopDTO>> validate
                    = this.validator.validate(laptop);
            if (validate.isEmpty()){
                if (this.laptopRepository.findByMacAddress(laptop.getMacAddress()).isEmpty()){
                    Laptop laptopToAdd = this.modelMapper.map(laptop,Laptop.class);
                    Shop shop = this.shopService.findByName(laptop.getShopName().getName());
                    laptopToAdd.setShop(shop);
                    this.laptopRepository.save(laptopToAdd);
                    sb.append(String.format("Successfully imported Laptop %s - %.2f- %d - %d"
                    ,laptopToAdd.getMacAddress(),laptopToAdd.getCpuSpeed()
                    ,laptopToAdd.getRam(),laptopToAdd.getStorage()));
                }else{
                    sb.append("Invalid laptop").append(System.lineSeparator());
                }
            }else{
                sb.append("Invalid laptop").append(System.lineSeparator());
            }
        }

       return sb.toString();
    }

    @Override
    public String exportBestLaptops() {
       StringBuilder sb = new StringBuilder();
        List<Laptop> laptops = this.laptopRepository.findAlLaptops();
        for (Laptop laptop : laptops) {
            sb.append(String.format("%s%n",laptop.getMacAddress()));
        }

        return sb.toString();
    }
}
