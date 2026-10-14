package com.example.uit067.studentmanagement.controller;

import com.example.uit067.studentmanagement.Student;
import com.example.uit067.studentmanagement.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration and endpoint tests for HelloController using MockMvc.
 * Tests CRUD operations, derived query search endpoints, pagination, and sorting.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    private Student rahul;
    private Student priya;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAllInBatch();
        List<Student> saved = studentRepository.saveAllAndFlush(List.of(
                new Student("Rahul", "rahul@example.com"),
                new Student("Priya", "priya@example.com"),
                new Student("Amit", "amit@example.com"),
                new Student("Neha", "neha@example.com"),
                new Student("Ravi", "ravi@example.com")
        ));
        rahul = saved.get(0);
        priya = saved.get(1);
    }

    @Test
    @DisplayName("1. GET /students returns JSON array of all students")
    void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].name", hasItems("Rahul", "Priya", "Amit", "Neha", "Ravi")));
    }

    @Test
    @DisplayName("2. GET /students/{id} returns student if found, 404 if missing")
    void testGetStudentById() throws Exception {
        // Valid ID
        mockMvc.perform(get("/students/" + rahul.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rahul.getId()))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email").value("rahul@example.com"));

        // Missing ID
        mockMvc.perform(get("/students/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("3. POST /students creates a new student and returns HTTP 201 Created")
    void testCreateStudent() throws Exception {
        String newStudentJson = "{\"name\":\"Karan\",\"email\":\"karan@example.com\"}";

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStudentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Karan"))
                .andExpect(jsonPath("$.email").value("karan@example.com"));
    }

    @Test
    @DisplayName("4. PUT /students/{id} updates existing student and returns HTTP 200")
    void testUpdateStudent() throws Exception {
        String updateJson = "{\"name\":\"Rahul Kumar\",\"email\":\"rahul.kumar@example.com\"}";

        mockMvc.perform(put("/students/" + rahul.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rahul.getId()))
                .andExpect(jsonPath("$.name").value("Rahul Kumar"))
                .andExpect(jsonPath("$.email").value("rahul.kumar@example.com"));

        // Missing ID returns 404
        mockMvc.perform(put("/students/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("5. DELETE /students/{id} deletes student and returns HTTP 204")
    void testDeleteStudent() throws Exception {
        mockMvc.perform(delete("/students/" + priya.getId()))
                .andExpect(status().isNoContent());

        // Verify it is gone
        mockMvc.perform(get("/students/" + priya.getId()))
                .andExpect(status().isNotFound());

        // Deleting non-existent student returns 404
        mockMvc.perform(delete("/students/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("6. GET /students/search/name/{name} returns matching student")
    void testSearchByName() throws Exception {
        mockMvc.perform(get("/students/search/name/Rahul"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Rahul"))
                .andExpect(jsonPath("$[0].email").value("rahul@example.com"));

        // Search with no matches returns empty array
        mockMvc.perform(get("/students/search/name/NonExistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    @DisplayName("7. GET /students/search/name/contains/{keyword} returns substring matches")
    void testSearchByNameContaining() throws Exception {
        mockMvc.perform(get("/students/search/name/contains/Rah"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Rahul"));
    }

    @Test
    @DisplayName("8. GET /students/search/email/{email} returns matching student")
    void testSearchByEmail() throws Exception {
        mockMvc.perform(get("/students/search/email/rahul@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Rahul"))
                .andExpect(jsonPath("$[0].email").value("rahul@example.com"));
    }

    @Test
    @DisplayName("9. GET /students/page returns paginated data and metadata")
    void testPagination() throws Exception {
        mockMvc.perform(get("/students/page")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Amit"))
                .andExpect(jsonPath("$.content[1].name").value("Neha"));

        // Page 1
        mockMvc.perform(get("/students/page")
                        .param("page", "1")
                        .param("size", "2")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name").value("Priya"))
                .andExpect(jsonPath("$.content[1].name").value("Rahul"));
    }

    @Test
    @DisplayName("10. GET /students/sort/name returns students in alphabetical order")
    void testSortByName() throws Exception {
        mockMvc.perform(get("/students/sort/name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].name").value("Amit"))
                .andExpect(jsonPath("$[1].name").value("Neha"))
                .andExpect(jsonPath("$[2].name").value("Priya"))
                .andExpect(jsonPath("$[3].name").value("Rahul"))
                .andExpect(jsonPath("$[4].name").value("Ravi"));
    }

    @Test
    @DisplayName("11. GET /students/sort with invalid property returns HTTP 400 Bad Request")
    void testInvalidSortParameter() throws Exception {
        mockMvc.perform(get("/students/sort").param("by", "invalidField"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("Invalid sort property")));
    }
}
