package com.example.uit067.studentmanagement.repository;

import com.example.uit067.studentmanagement.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit and integration tests for StudentRepository derived queries,
 * pagination, and sorting using an isolated @DataJpaTest environment.
 */
@DataJpaTest
@ActiveProfiles("test")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAllInBatch();
        studentRepository.saveAllAndFlush(List.of(
                new Student("Rahul", "rahul@example.com"),
                new Student("Priya", "priya@example.com"),
                new Student("Amit", "amit@example.com"),
                new Student("Neha", "neha@example.com"),
                new Student("Ravi", "ravi@example.com")
        ));
    }

    @Test
    @DisplayName("1. findByName should return students with exact matching name")
    void testFindByName() {
        List<Student> students = studentRepository.findByName("Rahul");

        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("Rahul");
        assertThat(students.get(0).getEmail()).isEqualTo("rahul@example.com");

        List<Student> notFound = studentRepository.findByName("NonExistent");
        assertThat(notFound).isEmpty();
    }

    @Test
    @DisplayName("2. findByNameContaining should return students whose name contains keyword")
    void testFindByNameContaining() {
        List<Student> students = studentRepository.findByNameContaining("Rah");

        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("Rahul");

        // Substring matching multiple names: "a" matches Rahul, Priya, Amit, Neha, Ravi
        List<Student> withA = studentRepository.findByNameContaining("a");
        assertThat(withA).isNotEmpty();
        assertThat(withA.stream().map(Student::getName)).contains("Rahul", "Priya", "Neha", "Ravi");
    }

    @Test
    @DisplayName("3. findByEmail should return students with matching email")
    void testFindByEmail() {
        List<Student> students = studentRepository.findByEmail("priya@example.com");

        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("Priya");
        assertThat(students.get(0).getEmail()).isEqualTo("priya@example.com");

        List<Student> emptyResult = studentRepository.findByEmail("unknown@example.com");
        assertThat(emptyResult).isEmpty();
    }

    @Test
    @DisplayName("4. findAll with Pageable should return the requested page size and metadata")
    void testPagination() {
        // Page 0 with size 2 sorted by name
        Page<Student> page0 = studentRepository.findAll(PageRequest.of(0, 2, Sort.by("name")));
        assertThat(page0.getContent()).hasSize(2);
        assertThat(page0.getTotalElements()).isEqualTo(5);
        assertThat(page0.getTotalPages()).isEqualTo(3);
        assertThat(page0.getNumber()).isEqualTo(0);
        assertThat(page0.getContent().get(0).getName()).isEqualTo("Amit");
        assertThat(page0.getContent().get(1).getName()).isEqualTo("Neha");

        // Page 1 with size 2
        Page<Student> page1 = studentRepository.findAll(PageRequest.of(1, 2, Sort.by("name")));
        assertThat(page1.getContent()).hasSize(2);
        assertThat(page1.getContent().get(0).getName()).isEqualTo("Priya");
        assertThat(page1.getContent().get(1).getName()).isEqualTo("Rahul");

        // Page 2 with size 2 (remaining 1 element)
        Page<Student> page2 = studentRepository.findAll(PageRequest.of(2, 2, Sort.by("name")));
        assertThat(page2.getContent()).hasSize(1);
        assertThat(page2.getContent().get(0).getName()).isEqualTo("Ravi");
    }

    @Test
    @DisplayName("5. findAll with Sort should return students in ascending name order")
    void testSortingByName() {
        List<Student> sortedStudents = studentRepository.findAll(Sort.by("name"));

        assertThat(sortedStudents).hasSize(5);
        List<String> sortedNames = sortedStudents.stream().map(Student::getName).toList();
        assertThat(sortedNames).containsExactly("Amit", "Neha", "Priya", "Rahul", "Ravi");
    }
}
