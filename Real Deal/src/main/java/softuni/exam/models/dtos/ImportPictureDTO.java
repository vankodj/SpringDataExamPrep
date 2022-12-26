package softuni.exam.models.dtos;

import javax.validation.constraints.Size;

public class ImportPictureDTO {

    @Size(min = 2,max = 20)
    private String name;

    private String dateAndTime;

    private long car;

    public String getName() {
        return name;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public long getCar() {
        return car;
    }
}
