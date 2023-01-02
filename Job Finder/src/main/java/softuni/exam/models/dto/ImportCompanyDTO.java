package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportCompanyDTO {

    @XmlElement
    @Size(min = 2,max = 40)
    @NotNull
    private String companyName;

    @XmlElement
    @NotNull
    private String dateEstablished;

    @XmlElement
    @Size(min = 2,max = 30)
    @NotNull
    private String website;

    @XmlElement
    private Long countryId;

    public String getCompanyName() {
        return companyName;
    }

    public String getDateEstablished() {
        return dateEstablished;
    }

    public String getWebsite() {
        return website;
    }

    public Long getCountryId() {
        return countryId;
    }
}
