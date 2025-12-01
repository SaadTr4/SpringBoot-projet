package fr.springboot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * Serves as the entry point for the backend application.
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Main method that launches the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}