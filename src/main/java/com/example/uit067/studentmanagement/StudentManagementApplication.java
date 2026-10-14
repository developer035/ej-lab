package com.example.uit067.studentmanagement;

import com.example.uit067.studentmanagement.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Main Spring Boot Application Entry Point for Experiment 5:
 * Spring Data JPA Repository - Student Management System.
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }

    /**
     * Seeds initial sample students if the database starts empty.
     * Demonstrates initial dataset for search, pagination, and sorting.
     * Active only when not in 'test' profile to avoid interfering with test fixtures.
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner initSampleData(StudentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.saveAll(List.of(
                        new Student("Rahul", "rahul@example.com"),
                        new Student("Priya", "priya@example.com"),
                        new Student("Amit", "amit@example.com"),
                        new Student("Neha", "neha@example.com"),
                        new Student("Ravi", "ravi@example.com")
                ));
            }
        };
    }
}
