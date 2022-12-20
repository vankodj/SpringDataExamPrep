package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MechanicNameDTO {

    @XmlElement(name = "firstName")
    private String firstName;

    public String getFirstName() {
        return firstName;
    }
}
