package com.example.uit067.studentmanagement.controller;

import java.util.List;

import com.example.uit067.studentmanagement.Course;
import com.example.uit067.studentmanagement.Student;
import com.example.uit067.studentmanagement.repository.StudentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.uit067.studentmanagement.repository.CourseRepository;

@RestController
public class HelloController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public HelloController(
            StudentRepository studentRepository,
            CourseRepository courseRepository) {

        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/student")
    public Student getStudent() {
        return new Student(
                1,
                "Rahul",
                "rahul@example.com"
        );
    }

    // GET all students
    @GetMapping("/students")
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    // POST - add a new student
    @PostMapping("/students")
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    // GET student by ID
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT - update an existing student
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable int id,
            @RequestBody Student updatedStudent) {

        return studentRepository.findById(id)
                .map(student -> {
                    student.setName(updatedStudent.getName());
                    student.setEmail(updatedStudent.getEmail());

                    Student savedStudent = studentRepository.save(student);

                    return ResponseEntity.ok(savedStudent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - delete a student
    @DeleteMapping("/students/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {

        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        studentRepository.deleteById(id);

        return ResponseEntity.ok("Student deleted successfully");
    }

    // Search students by name
    @GetMapping("/students/search/name/{name}")
    public List<Student> searchByName(@PathVariable String name) {
        return studentRepository.findByName(name);
    }

    // Search students by name containing keyword
    @GetMapping("/students/search/name/contains/{keyword}")
    public List<Student> searchByNameContaining(@PathVariable String keyword) {
        return studentRepository.findByNameContaining(keyword);
    }

    // Search students by email
    @GetMapping("/students/search/email/{email}")
    public List<Student> searchByEmail(@PathVariable String email) {
        return studentRepository.findByEmail(email);
    }

    // GET students with pagination
    @GetMapping("/students/page")
    public Page<Student> getStudentsPage(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    // GET students sorted by name
    @GetMapping("/students/sort/name")
    public List<Student> getStudentsSortedByName() {
        return studentRepository.findAll(Sort.by("name"));
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    @PostMapping(path = "/students/{studentId}/courses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Course> addCourseToStudent(
            @PathVariable int studentId,
            @RequestBody Course course) {

        return studentRepository.findById(studentId)
                .map(student -> {
                    course.setStudent(student);
                    Course savedCourse = courseRepository.save(course);
                    return ResponseEntity.ok(savedCourse);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
