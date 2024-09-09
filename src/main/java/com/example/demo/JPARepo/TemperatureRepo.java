package com.example.demo.JPARepo;

import com.example.demo.entity.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemperatureRepo extends JpaRepository<Temperature,Long> {

    Optional<Temperature> findTemperatureById(long id);

}
