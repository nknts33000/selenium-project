package com.example.demo.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Temperature {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    LocalDateTime dateTime;

    @Column(nullable = false)
    Float temperature;

    public Temperature(){}

    public Temperature(LocalDateTime dateTime,Float temperature){
        this.dateTime=dateTime;
        this.temperature=temperature;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public Float getTemperature() {
        return temperature;
    }
}
