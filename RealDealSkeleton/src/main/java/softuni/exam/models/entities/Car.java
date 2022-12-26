package softuni.exam.models.entities;



import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String make;

    @Column(nullable = false)
    private String model;

    @Column
    private int kilometers;

    @Column(nullable = false)
    private LocalDate registeredOn;

    public Car() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getKilometers() {
        return kilometers;
    }

    public void setKilometers(int kilometers) {
        this.kilometers = kilometers;
    }

    public LocalDate getRegisteredOn() {
        return registeredOn;
    }

    public void setRegisteredOn(LocalDate registeredOn) {
        this.registeredOn = registeredOn;
    }
}
