package com.example.uit067.studentmanagement.repository;

import com.example.uit067.studentmanagement.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    List<Student> findByName(String name);

    List<Student> findByNameContaining(String keyword);

    List<Student> findByEmail(String email);
}
