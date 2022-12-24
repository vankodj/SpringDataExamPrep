package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportOfferDTO {

    @XmlElement
    @NotNull
    @Positive
    private BigDecimal price;

    @XmlElement(name = "agent")
    @NotNull
    private AgentNameDTO agent;

    @XmlElement
    @NotNull
    private ApartmentIdDTO apartment;

    @XmlElement
    @NotNull
    private String publishedOn;

    public BigDecimal getPrice() {
        return price;
    }

    public AgentNameDTO getAgent() {
        return agent;
    }

    public ApartmentIdDTO getApartment() {
        return apartment;
    }

    public String getPublishedOn() {
        return publishedOn;
    }
}
