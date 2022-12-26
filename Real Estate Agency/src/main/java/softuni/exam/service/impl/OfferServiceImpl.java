package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOfferDTO;
import softuni.exam.models.dto.ImportOfferRootDTO;
import softuni.exam.models.entity.*;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.AgentService;
import softuni.exam.service.ApartmentService;
import softuni.exam.service.OfferService;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final AgentService agentService;
    private final ApartmentService apartmentService;
    private final TownService townService;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final StringBuilder sb;
    private final Unmarshaller unmarshaller;
    private final Validator validator;
    private final Path path = Path.of("src/main/resources/files/xml/offers.xml");

    @Autowired
    public OfferServiceImpl(OfferRepository offerRepository, AgentService agentService, ApartmentService apartmentService, TownService townService, Gson gson, ModelMapper modelMapper) throws JAXBException {
        this.offerRepository = offerRepository;
        this.agentService = agentService;
        this.apartmentService = apartmentService;
        this.townService = townService;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.sb = new StringBuilder();
        JAXBContext context = JAXBContext.newInstance(ImportOfferRootDTO.class);
        this.unmarshaller = context.createUnmarshaller();
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
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
        Set<ConstraintViolation<ImportOfferDTO>> validate = this.validator.validate(offer);
        if (validate.isEmpty()){
            Agent agent = this.agentService.findAgentByFirstName(offer.getAgent().getName());
            if (agent != null){
                Apartment apartment = this.apartmentService.findApartmentById(offer.getApartment().getId());

                 Offer offerToAdd = this.modelMapper.map(offer,Offer.class);
                offerToAdd.setAgent(agent);
                offerToAdd.setApartment(apartment);
                this.offerRepository.save(offerToAdd);
                sb.append(String.format("Successfully imported offer %.2f"
                ,offerToAdd.getPrice())).append(System.lineSeparator());
            }else{
                sb.append("Invalid offer").append(System.lineSeparator());
            }
        }else{
            sb.append("Invalid offer").append(System.lineSeparator());
        }


        return sb.toString();
    }

//    •	"Agent {firstName} {lastName} with offer №{offerId}:
//            -Apartment area: {area}
//   		--Town: {townName}
//   		---Price: {price}$

    @Override
    public String exportOffers() {
        List<Offer> offers = this.offerRepository.findAllByApartment_ApartmentTypeOrderByApartment_AreaDescPriceAsc(ApartmentType.three_rooms);
        for (Offer offer : offers) {
            sb.append(String.format("Agent %s %s with offer №%d%n" +
                    "\t-Apartment area: %.2f%n" +
                    "\t--Town: %s%n" +
                    "\t---Price: %.2f$%n"
            ,offer.getAgent().getFirstName(),offer.getAgent().getLastName(),offer.getId()
            ,offer.getApartment().getArea(),offer.getApartment().getTown().getTownName()
            ,offer.getPrice()));
        }
        return sb.toString();
    }
}
