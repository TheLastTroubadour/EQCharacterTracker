package com.eq.charactertracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class CharacterTrackerApplication {

    public static void main(String[] args){
        SpringApplication.run(CharacterTrackerApplication.class);
    }

}
