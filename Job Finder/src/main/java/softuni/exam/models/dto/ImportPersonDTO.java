package softuni.exam.models.dto;

import softuni.exam.models.entity.StatusType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportPersonDTO {

    @Email
    @NotNull
    private String email;

    @Size(min = 2,max = 30)
    @NotNull
    private String firstName;

    @Size(min = 2,max = 30)
    @NotNull
    private String lastName;

    @Size(min = 2,max = 13)
    private String phone;

    @NotNull
    private StatusType statusType;

    @NotNull
    private Long country;

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public Long getCountry() {
        return country;
    }
}
