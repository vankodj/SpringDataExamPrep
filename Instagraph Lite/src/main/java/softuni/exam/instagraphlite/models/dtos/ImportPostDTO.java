package softuni.exam.instagraphlite.models.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportPostDTO {

    @XmlElement
    @Size(min = 21)
    private String caption;

    @XmlElement(name = "user")

    private userNameDTO user;

    @XmlElement(name = "picture")

    private picturePathDTO picture;

    public String getCaption() {
        return caption;
    }

    public userNameDTO getUser() {
        return user;
    }

    public picturePathDTO getPicture() {
        return picture;
    }
}
