package com.example.football.models.dto;

import com.example.football.models.entity.Town;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ImportTeamDTO {

    @Size(min = 3)
    @NotNull
    private String name;

    @Size(min = 3)
    @NotNull
    private String stadiumName;

    @Min(1000)
    @NotNull
    private int fanBase;

    @Size(min = 10)
    @NotNull
    private String history;

    private String townName;

    public String getName() {
        return name;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public int getFanBase() {
        return fanBase;
    }

    public String getHistory() {
        return history;
    }

    public String getTownName() {
        return townName;
    }
}
