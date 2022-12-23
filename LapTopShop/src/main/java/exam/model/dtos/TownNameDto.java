package exam.model.dtos;

import exam.model.entities.Town;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class TownNameDto {

    @XmlElement
    private String name;

    public String getName() {
        return name;
    }
}
