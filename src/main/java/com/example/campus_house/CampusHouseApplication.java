package com.example.campus_house;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampusHouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusHouseApplication.class, args);
    }

}
