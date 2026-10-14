package com.example.uit067.studentmanagement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing a Student record mapped to the 'students' table.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Student() {
    }

    /**
     * Constructor for creating a new student without an ID (ID is auto-generated).
     *
     * @param name  Student's full name
     * @param email Student's unique email address
     */
    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Full-argument constructor.
     *
     * @param id    Student primary key identifier
     * @param name  Student's full name
     * @param email Student's unique email address
     */
    public Student(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
