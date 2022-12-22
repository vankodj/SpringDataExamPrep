package softuni.exam.instagraphlite.models.dtos;

import softuni.exam.instagraphlite.models.entities.Picture;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportUserDTO {

    @NotNull
    @Size(min = 2,max = 18)
    private String username;

    @NotNull
    @Size(min = 4)
    private String password;

    @NotNull
    @Size(min = 20)
    private String profilePicture;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
