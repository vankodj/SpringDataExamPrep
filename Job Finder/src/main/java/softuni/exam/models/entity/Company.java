package softuni.exam.models.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "companies")
public class Company extends BaseEntity{

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false)
    private String website;

    @Column(nullable = false,name = "date_established")
    private LocalDate dateEstablished;



    @ManyToOne
    private Country country;

    public Company() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public LocalDate getDateEstablished() {
        return dateEstablished;
    }

    public void setDateEstablished(LocalDate dateEstablished) {
        this.dateEstablished = dateEstablished;
    }


    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
