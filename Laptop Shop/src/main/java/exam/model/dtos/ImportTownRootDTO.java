package exam.model.dtos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "towns")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportTownRootDTO {

    @XmlElement(name = "town")
    private List<ImportTownDTO> towns;

    public List<ImportTownDTO> getTowns() {
        return towns;
    }
}
