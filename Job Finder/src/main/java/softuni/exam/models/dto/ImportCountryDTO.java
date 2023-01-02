package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportCountryDTO {

    @Size(min = 2,max = 30)
    @NotNull
    private String name;

    @Size(min = 2,max = 19)
    @NotNull
    private String countryCode;

    @Size(min = 2,max = 19)
    @NotNull
    private String currency;

    public String getName() {
        return name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCurrency() {
        return currency;
    }
}
