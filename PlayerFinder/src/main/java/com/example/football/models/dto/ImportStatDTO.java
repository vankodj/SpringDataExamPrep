package com.example.football.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class ImportStatDTO {

    @XmlElement
    @Positive
    @NotNull
    private float passing;

    @XmlElement
    @Positive
    @NotNull
    private float shooting;

    @XmlElement
    @Positive
    @NotNull
    private float endurance;

    public float getPassing() {
        return passing;
    }

    public float getShooting() {
        return shooting;
    }

    public float getEndurance() {
        return endurance;
    }
}
