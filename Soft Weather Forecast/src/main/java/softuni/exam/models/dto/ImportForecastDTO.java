package softuni.exam.models.dto;

import softuni.exam.models.entity.DayOfWeek;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportForecastDTO {

    @XmlElement(name = "day_of_week")
    @NotNull
    private DayOfWeek dayOfWeek;

    @XmlElement(name = "max_temperature")
    @Min(-20)
    @Max(60)
    private double maxTemperature;

    @XmlElement(name = "min_temperature")
    @Min(-50)
    @Max(40)
    private double minTemperature;

    @XmlElement
    @NotNull
    private String sunrise;

    @XmlElement
    @NotNull
    private String sunset;

    @XmlElement
    private Long city;

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public Long getCity() {
        return city;
    }
}
