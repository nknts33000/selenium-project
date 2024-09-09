package com.example.demo.controller;

import com.example.demo.JPARepo.TemperatureRepo;
import com.example.demo.entity.Temperature;
import jakarta.persistence.GeneratedValue;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class MainController {

    private final TemperatureRepo temperatureRepo;

    @Autowired
    public MainController(TemperatureRepo temperatureRepo){
        this.temperatureRepo=temperatureRepo;
    }

    @PostMapping("/temperature")
    public ResponseEntity<Void> saveDate(@RequestBody Temperature newTemperature){
        try {
            temperatureRepo.save(new Temperature(newTemperature.getDateTime(),newTemperature.getTemperature()));
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        catch (Exception e){
            System.out.println("Exception:"+ e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/temperature/{id}")
    public ResponseEntity<Temperature> getTemperature(@PathVariable long id){
        Optional<Temperature> temperature=temperatureRepo.findTemperatureById(id);
        if (temperature.get()!=null) return new ResponseEntity<>(temperature.get(), HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/temperature/{id}")
    public ResponseEntity<Void> deleteTemperature(@PathVariable long id){
        Optional<Temperature> temperature=temperatureRepo.findTemperatureById(id);
        if (temperature.get()!=null) {
            temperatureRepo.delete(temperature.get());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
