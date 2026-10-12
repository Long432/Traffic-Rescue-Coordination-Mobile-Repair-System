package com.sosrescuenow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SosRescueNowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SosRescueNowApplication.class, args);
    }
}
