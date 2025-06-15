package com.tecnocampus.examsimulation.api.domain;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class Kingdom {
    private String id;

    @NotNull
    @Min(0)
    @Max(60)
    private Integer gold;

    @NotNull
    @Min(0)
    @Max(60)
    private Integer citizens;

    @NotNull
    @Min(0)
    @Max(60)
    private Integer food;

    private LocalDate dateOfCreation;

    public Kingdom() {
        this.dateOfCreation = LocalDate.now();
    }

    public Kingdom(Integer gold, Integer citizens, Integer food) {
        this();
        this.gold = gold;
        this.citizens = citizens;
        this.food = food;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getGold() { return gold; }
    public void setGold(Integer gold) { this.gold = gold; }
    public Integer getCitizens() { return citizens; }
    public void setCitizens(Integer citizens) { this.citizens = citizens; }
    public Integer getFood() { return food; }
    public void setFood(Integer food) { this.food = food; }
    public LocalDate getDateOfCreation() { return dateOfCreation; }
    public void setDateOfCreation(LocalDate dateOfCreation) { this.dateOfCreation = dateOfCreation; }

    public static class InvestRequest {
        @NotNull @Min(0)
        private Integer gold;

        public Integer getGold() { return gold; }
        public void setGold(Integer gold) { this.gold = gold; }
    }
}