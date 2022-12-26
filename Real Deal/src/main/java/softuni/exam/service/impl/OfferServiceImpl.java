package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dtos.ImportOfferDTO;
import softuni.exam.models.dtos.ImportOfferRootDTO;
import softuni.exam.models.entities.Car;
import softuni.exam.models.entities.Offer;
import softuni.exam.models.entities.Picture;
import softuni.exam.models.entities.Seller;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.CarService;
import softuni.exam.service.OfferService;
import softuni.exam.service.PictureService;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final Unmarshaller unmarshaller;
    private final CarService carService;
    private final SellerService sellerService;
    private final PictureService pictureService;
    Path path = Path.of("src/main/resources/files/xml/offers.xml");

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, ModelMapper modelMapper, CarService carService, SellerService sellerService, PictureService pictureService) throws JAXBException {
        this.offerRepository = offerRepository;
        this.modelMapper = modelMapper;
        this.carService = carService;
        this.sellerService = sellerService;
        this.pictureService = pictureService;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
        JAXBContext context = JAXBContext.newInstance(ImportOfferDTO.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count() >0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(path);
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        ImportOfferRootDTO offers = (ImportOfferRootDTO)
                this.unmarshaller.unmarshal(new FileReader(path.toAbsolutePath().toString()));

        return offers.getOffers().stream()
                .map(this::importOffer).collect(Collectors.joining("\n"));
    }

    private String importOffer(ImportOfferDTO offer) {
        Set<ConstraintViolation<ImportOfferDTO>> validate
                = this.validator.validate(offer);
        if (validate.isEmpty()){
            Car car = this.carService.findCarById(offer.getCar().getId());
            Seller seller = this.sellerService.findSellerById(offer.getSeller().getId());
            Offer offerToAdd = this.modelMapper.map(offer,Offer.class);
            offerToAdd.setCar(car);
            offerToAdd.setSeller(seller);
            List<Picture> pictures = this.pictureService.findAllByCar(car);
            offerToAdd.setPictures(pictures);
            return String.format("Successfully import offer %s - %s"
            ,offerToAdd.getAddedOn(),offerToAdd.isHasGoldStatus());
        }
        else{
            return "Invalid offer";
        }
    }
}
