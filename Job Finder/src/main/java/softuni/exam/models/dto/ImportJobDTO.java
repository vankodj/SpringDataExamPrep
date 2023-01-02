package softuni.exam.models.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportJobDTO {

    @XmlElement
    @Size(min = 2,max = 40)
    @NotNull
    private String jobTitle;

    @XmlElement
    @Min(10)
    @NotNull
    private double hoursAWeek;

    @XmlElement
    @Min(300)
    @NotNull
    private double salary;

    @XmlElement
    @Size(min = 5)
    @NotNull
    private String description;

    @XmlElement
    @NotNull
    private Long companyId;

    public String getJobTitle() {
        return jobTitle;
    }

    public double getHoursAWeek() {
        return hoursAWeek;
    }

    public double getSalary() {
        return salary;
    }

    public String getDescription() {
        return description;
    }

    public Long getCompanyId() {
        return companyId;
    }
}
