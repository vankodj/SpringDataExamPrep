package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;


public class ImportTownDTO {

    @Size(min = 2,max = 19)
    private String townName;

    @Positive
    private int population;

    public String getTownName() {
        return townName;
    }

    public int getPopulation() {
        return population;
    }
}
