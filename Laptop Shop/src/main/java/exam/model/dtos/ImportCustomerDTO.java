package exam.model.dtos;

import exam.model.entities.Town;
import exam.util.LocalDateAdapter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class ImportCustomerDTO {

    @Size(min = 2)
    private String firstName;

    @Size(min = 2)
    private String lastName;

    @Email
    private String email;

    @NotNull
    private String registeredOn;

    private townName town;

    public ImportCustomerDTO() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    @NotNull
    public String getRegisteredOn() {
        return registeredOn;
    }

    public townName getTown() {
        return town;
    }
}
