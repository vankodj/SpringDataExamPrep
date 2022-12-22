package softuni.exam.instagraphlite.models.dtos;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportPictureDTO {

    @NotNull
    @Size(min = 20)
    private String path;

    @Min(500)
    @Max(60000)
    private double size;

    public String getPath() {
        return path;
    }

    public double getSize() {
        return size;
    }
}
