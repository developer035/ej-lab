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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for Student CRUD operations in HelloController with Spring Data JPA.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        studentRepository.save(new Student(1, "Rahul", "rahul@example.com"));
        studentRepository.save(new Student(2, "Priya", "priya@example.com"));
        studentRepository.save(new Student(3, "Amit", "amit@example.com"));
    }

    @Test
    @DisplayName("GET /students returns list of in-memory students")
    void testGetStudents() throws Exception {
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Rahul"))
                .andExpect(jsonPath("$[0].email").value("rahul@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Priya"))
                .andExpect(jsonPath("$[1].email").value("priya@example.com"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Amit"))
                .andExpect(jsonPath("$[2].email").value("amit@example.com"));
    }

    @Test
    @DisplayName("POST /students adds a new student and returns the added student")
    void testAddStudent() throws Exception {
        String studentJson = "{\"id\":4,\"name\":\"Neha\",\"email\":\"neha@example.com\"}";

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Neha"))
                .andExpect(jsonPath("$.email").value("neha@example.com"));

        // Verify the student is now in the list
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[3].name").value("Neha"));
    }

    @Test
    @DisplayName("GET /students/{id} returns student when ID exists")
    void testGetStudentByIdSuccess() throws Exception {
        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Rahul"))
                .andExpect(jsonPath("$.email").value("rahul@example.com"));
    }

    @Test
    @DisplayName("GET /students/{id} returns 404 when ID does not exist")
    void testGetStudentByIdNotFound() throws Exception {
        mockMvc.perform(get("/students/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /students/{id} updates student details and returns updated student")
    void testUpdateStudentSuccess() throws Exception {
        String updateJson = "{\"id\":2,\"name\":\"Priya Sharma\",\"email\":\"priya.sharma@example.com\"}";

        mockMvc.perform(put("/students/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Priya Sharma"))
                .andExpect(jsonPath("$.email").value("priya.sharma@example.com"));

        // Verify the change is reflected
        mockMvc.perform(get("/students/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Priya Sharma"))
                .andExpect(jsonPath("$.email").value("priya.sharma@example.com"));
    }

    @Test
    @DisplayName("PUT /students/{id} returns 404 when ID does not exist")
    void testUpdateStudentNotFound() throws Exception {
        String updateJson = "{\"id\":999,\"name\":\"Non Existent\",\"email\":\"none@example.com\"}";

        mockMvc.perform(put("/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /students/{id} deletes student and subsequent lookup returns 404")
    void testDeleteStudentSuccess() throws Exception {
        mockMvc.perform(delete("/students/3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Student deleted successfully"));

        // Verify that student 3 is no longer in the list
        mockMvc.perform(get("/students/3"))
                .andExpect(status().isNotFound());

        // Verify list size decreased to 2
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("DELETE /students/{id} returns 404 when ID does not exist")
    void testDeleteStudentNotFound() throws Exception {
        mockMvc.perform(delete("/students/999"))
                .andExpect(status().isNotFound());
    }
}
