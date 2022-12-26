package softuni.exam.models.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String name;
    
    @Column(name = "date_and_time")
    private LocalDateTime dateAndTime;

    @ManyToOne
    private Car car;


    public Picture() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(LocalDateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }


}
