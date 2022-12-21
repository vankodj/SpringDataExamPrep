package com.example.football.models.dto;

import com.example.football.models.entity.PlayerPosition;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportPlayerDTO {

    @XmlElement(name = "first-name")
    @Size(min = 2)
    @NotNull
    private String firstName;

    @XmlElement(name = "last-name")
    @Size(min = 2)
    @NotNull
    private String lastName;

    @XmlElement
    @Email
    @NotNull
    private String email;

    @XmlElement(name = "birth-date")
    @NotNull
    private String birthDate;

    @XmlElement
    @NotNull
    private PlayerPosition position;

    @XmlElement
    private TownNameDTo town;

    @XmlElement
    private TeamNameDTO team;

    @XmlElement
    private StatIdDTO stat;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public PlayerPosition getPosition() {
        return position;
    }

    public TownNameDTo getTown() {
        return town;
    }

    public TeamNameDTO getTeam() {
        return team;
    }

    public StatIdDTO getStat() {
        return stat;
    }
}
