package com.example.uit067.studentmanagement.controller;

import com.example.uit067.studentmanagement.Student;
import com.example.uit067.studentmanagement.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * REST Controller exposing CRUD operations, derived query searches,
 * pagination, and sorting for Student records.
 */
@RestController
public class HelloController {

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "name", "email");

    private final StudentRepository studentRepository;

    /**
     * Constructor injection for StudentRepository.
     *
     * @param studentRepository the repository bean injected by Spring
     */
    public HelloController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // =========================================================================
    // Part A: Basic CRUD Endpoints
    // =========================================================================

    /**
     * A. Get all students.
     * GET /students
     *
     * @return List of all students in JSON format (HTTP 200)
     */
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * B. Get a student by ID.
     * GET /students/{id}
     *
     * @param id primary key identifier of the student
     * @return 200 OK with the student if found; 404 NOT FOUND if not
     */
    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * C. Create a student.
     * POST /students
     * Content-Type: application/json
     *
     * Automatically assigns a database-generated ID.
     *
     * @param student student payload containing name and email
     * @return 201 CREATED with the saved student entity
     */
    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        // Ensure ID is null so that database IDENTITY generation is used
        student.setId(null);
        Student savedStudent = studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }

    /**
     * D. Update an existing student.
     * PUT /students/{id}
     * Content-Type: application/json
     *
     * @param id             ID of the student to update
     * @param studentDetails updated name and email
     * @return 200 OK with updated student, or 404 NOT FOUND if student does not exist
     */
    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable Integer id, @RequestBody Student studentDetails) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setName(studentDetails.getName());
                    existingStudent.setEmail(studentDetails.getEmail());
                    Student updatedStudent = studentRepository.save(existingStudent);
                    return ResponseEntity.ok(updatedStudent);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * E. Delete a student.
     * DELETE /students/{id}
     *
     * @param id ID of the student to delete
     * @return 204 NO CONTENT on success, or 404 NOT FOUND if student does not exist
     */
    @DeleteMapping("/students/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Part B: Derived Query Endpoints
    // Notice: /students/search/... endpoints are explicitly distinguished from /students/{id}
    // =========================================================================

    /**
     * 1. Search students by exact name.
     * GET /students/search/name/{name}
     *
     * @param name exact name to query
     * @return list of matching student records (HTTP 200)
     */
    @GetMapping("/students/search/name/{name}")
    public List<Student> searchByName(@PathVariable String name) {
        return studentRepository.findByName(name);
    }

    /**
     * 2. Search students by name containing keyword.
     * GET /students/search/name/contains/{keyword}
     *
     * @param keyword substring to match
     * @return list of matching student records (HTTP 200)
     */
    @GetMapping("/students/search/name/contains/{keyword}")
    public List<Student> searchByNameContaining(@PathVariable String keyword) {
        return studentRepository.findByNameContaining(keyword);
    }

    /**
     * 3. Search students by email.
     * GET /students/search/email/{email}
     *
     * Accepts path variable email. Also allows optional query parameter 'email' as an alternative.
     *
     * @param email path variable email address
     * @return list of matching student records (HTTP 200)
     */
    @GetMapping("/students/search/email/{email:.+}")
    public List<Student> searchByEmail(@PathVariable String email) {
        return studentRepository.findByEmail(email);
    }

    // =========================================================================
    // Part C: Pagination Endpoint
    // =========================================================================

    /**
     * Get paginated students.
     * GET /students/page?page=0&size=2&sort=name,asc
     *
     * Automatically binds Spring Data Pageable query parameters:
     * - page: 0-based page index (default: 0)
     * - size: maximum items per page (default: 20)
     * - sort: field to sort by and direction (e.g., name,asc)
     *
     * @param pageable pageable request configuration
     * @return Page containing the slice of students and pagination metadata
     */
    @GetMapping("/students/page")
    public Page<Student> getStudentsPage(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    // =========================================================================
    // Part D: Sorting Endpoints
    // =========================================================================

    /**
     * GET /students/sort/name
     * Retrieves all students sorted in ascending order by name.
     *
     * @return list of students sorted by name
     */
    @GetMapping("/students/sort/name")
    public List<Student> getStudentsSortedByName() {
        return studentRepository.findAll(Sort.by("name"));
    }

    /**
     * Optional general sorting endpoint:
     * GET /students/sort?by=name&direction=asc
     *
     * Validates sort property against an allowlist ('id', 'name', 'email').
     * Returns HTTP 400 Bad Request if an invalid property is supplied.
     *
     * @param by        property name to sort by (default: 'name')
     * @param direction 'asc' or 'desc' (default: 'asc')
     * @return list of students sorted by the requested field
     */
    @GetMapping("/students/sort")
    public List<Student> getStudentsSorted(
            @RequestParam(defaultValue = "name") String by,
            @RequestParam(defaultValue = "asc") String direction) {

        if (!ALLOWED_SORT_PROPERTIES.contains(by.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid sort property: '" + by + "'. Allowed properties are: " + ALLOWED_SORT_PROPERTIES);
        }

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(by).descending()
                : Sort.by(by).ascending();

        return studentRepository.findAll(sort);
    }
}
