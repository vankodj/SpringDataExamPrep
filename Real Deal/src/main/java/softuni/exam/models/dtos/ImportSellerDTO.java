package softuni.exam.models.dtos;

import softuni.exam.models.entities.RatingType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportSellerDTO {

    @XmlElement(name = "first-name")
    @Size(min = 2,max = 20)
    private String firstName;

    @XmlElement(name = "last-name")
    @Size(min = 2,max = 20)
    private String lastName;

    @XmlElement
    @Email
    private String email;

    @XmlElement
    @NotNull
    private RatingType rating;

    @XmlElement
    @NotNull
    private String town;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public RatingType getRating() {
        return rating;
    }

    public String getTown() {
        return town;
    }
}
