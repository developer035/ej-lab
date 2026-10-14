# Experiment 5: Spring Data JPA Repository

**College Laboratory Assignment — Enterprise Java Lab**  
**Base Package:** `com.example.uit067.studentmanagement`  
**Server Port:** `8081`

---

## 1. Experiment Title
**Experiment 5: Spring Data JPA Repository — Student Management System**

---

## 2. Experiment Objectives
By the end of this experiment, students will be able to:
1. Create and configure a **Spring Data JPA repository** by extending `JpaRepository`.
2. Implement and understand **Derived Query Methods** (`findByName`, `findByNameContaining`, `findByEmail`) where queries are generated automatically from method names.
3. Search database records without manually writing SQL queries or using `@Query` annotations.
4. Implement **Pagination** using Spring Data's `Pageable` and `Page` abstractions (including zero-based page numbering).
5. Implement **Sorting** using Spring Data's `Sort` abstraction at the database level.
6. Expose all CRUD, search, pagination, and sorting features through REST API endpoints.
7. Perform full CRUD operations via `JpaRepository` with correct HTTP status codes (`200 OK`, `201 Created`, `204 No Content`, `400 Bad Request`, `404 Not Found`, `409 Conflict`).

---

## 3. Technologies Used
- **Java:** JDK 17 (tested up to JDK 23)
- **Framework:** Spring Boot 3.4.3
- **Web Layer:** Spring MVC (`spring-boot-starter-web`)
- **Persistence Layer:** Spring Data JPA (`spring-boot-starter-data-jpa`)
- **Default Database:** In-memory H2 Database (zero external setup required)
- **Production / Optional Database:** MySQL (`mysql-connector-j`)
- **Build Tool:** Apache Maven with Maven Wrapper (`mvnw` / `mvnw.cmd`)
- **Testing:** JUnit 5, Spring Boot Test, Spring Data JPA Test, MockMvc, AssertJ

---

## 4. Project Structure
```text
studentmanagement-02/
├── pom.xml
├── README.md
├── mvnw
├── mvnw.cmd
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── example/
    │   │           └── uit067/
    │   │               └── studentmanagement/
    │   │                   ├── StudentManagementApplication.java
    │   │                   ├── Student.java
    │   │                   ├── controller/
    │   │                   │   ├── HelloController.java
    │   │                   │   └── GlobalExceptionHandler.java
    │   │                   └── repository/
    │   │                       └── StudentRepository.java
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           └── index.html
    └── test/
        └── java/
            └── com/
                └── example/
                    └── uit067/
                        └── studentmanagement/
                            ├── repository/
                            │   └── StudentRepositoryTest.java
                            └── controller/
                                └── HelloControllerTest.java
```

---

## 5. Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher installed (`java -version`).
- **Operating System:** Windows, macOS, or Linux.
- **Git** (optional).
- No external database installation is required for default execution (H2 in-memory is bundled).

---

## 6. How to Open the Project in IntelliJ IDEA
1. Launch **IntelliJ IDEA**.
2. Click **Open** (or **File > Open...**).
3. Navigate to the project root directory: `K:\College Study Material\EJ\Lab\studentmanagement-02` (or your local clone).
4. Select the folder and click **OK**.
5. IntelliJ will detect `pom.xml` as a Maven project. If prompted, select **Open as Project** and **Trust Project**.
6. Wait for Maven to download dependencies and index the project (status bar at bottom right).
7. Ensure Project SDK is set to JDK 17+ under **File > Project Structure > Project > SDK**.

---

## 7. How to Run the Application Using Maven
Open a terminal (Command Prompt, PowerShell, or Bash) in the project directory:

### Windows (PowerShell or CMD):
```powershell
.\mvnw.cmd clean spring-boot:run
```

### Linux / macOS:
```bash
./mvnw clean spring-boot:run
```

### Or using installed Maven:
```bash
mvn clean spring-boot:run
```
The application will start on **port 8081**:
```text
Tomcat started on port 8081 (http) with context path '/'
Started StudentManagementApplication in ... seconds
```

---

## 8. How to Run the Application from IntelliJ IDEA
1. In the Project tool window, navigate to:  
   `src/main/java/com/example/uit067/studentmanagement/StudentManagementApplication.java`
2. Right-click `StudentManagementApplication.java` and click **Run 'StudentManagementApplication'** (or click the green Play icon next to `public static void main`).
3. View runtime logs in the **Run** window at the bottom.

---

## 9. Database Configuration and H2 Console Access

### In-Memory H2 Configuration (`application.properties`)
```properties
spring.application.name=student-management
server.port=8081

# H2 In-Memory DataSource
spring.datasource.url=jdbc:h2:mem:studentdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate DDL & SQL logging
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Web Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.h2.console.settings.web-allow-others=true
```

### Accessing H2 Web Console:
1. Open your web browser and navigate to:  
   **`http://localhost:8081/h2-console`**
2. Fill in the login form:
   - **Driver Class:** `org.h2.Driver`
   - **JDBC URL:** `jdbc:h2:mem:studentdb`
   - **User Name:** `sa`
   - **Password:** *(leave blank)*
3. Click **Connect**.
4. You can run SQL queries directly, e.g.:
   ```sql
   SELECT * FROM students;
   ```

> [!IMPORTANT]
> **H2 Database Lifecycle:**  
> An in-memory H2 database (`jdbc:h2:mem:...`) stores data entirely in RAM. When the application stops or restarts, all data is discarded. On every fresh startup, sample students are re-initialized automatically by `StudentManagementApplication`.

---

## 10. Explanation of the `Student` Entity
The `Student` class is a plain Java object (POJO) annotated with Jakarta Persistence (`jakarta.persistence.*`) annotations to map it directly to a database table:

```java
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    ...
}
```

### Key Elements:
- `@Entity`: Marks this class as a persistent entity managed by JPA/Hibernate.
- `@Table(name = "students")`: Names the underlying relational table `students`.
- `@Id`: Designates `id` as the primary key.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Instructs the database to auto-increment the ID upon insertion (e.g. `AUTO_INCREMENT`). Clients do not supply IDs when creating new records.
- `@Column(unique = true)`: Enforces an index and unique constraint on email addresses at the database level.
- **Explicit constructors, getters, setters, and `toString()`:** Implemented directly without Lombok, making the code self-contained and transparent for college lab evaluation.

---

## 11. Explanation of `JpaRepository`
In `StudentRepository.java`:
```java
public interface StudentRepository extends JpaRepository<Student, Integer> { ... }
```
`JpaRepository<Student, Integer>` is a core interface from Spring Data JPA:
- **Type Arguments:**
  - `Student`: The domain entity type.
  - `Integer`: The primary key type (`Student.id`).
- **Inherited Methods:**
  - `save(Student entity)`: Saves or updates an entity.
  - `findById(Integer id)`: Returns an `Optional<Student>`.
  - `findAll()`: Retrieves all entities.
  - `deleteById(Integer id)`: Removes an entity by primary key.
  - `findAll(Pageable pageable)`: Returns a `Page<Student>` with pagination metadata.
  - `findAll(Sort sort)`: Returns entities ordered by specified properties.

Spring Data JPA generates the implementation class dynamically via runtime dynamic proxies — developers only define the interface.

---

## 12. Explanation of Derived Query Methods
Derived query methods allow querying the database simply by declaring method signatures following Spring Data naming conventions:

```java
List<Student> findByName(String name);
List<Student> findByNameContaining(String keyword);
List<Student> findByEmail(String email);
```

### How Spring Translates Method Names to SQL:
| Method Signature | Spring Data Keyword | Generated SQL Equivalent |
|---|---|---|
| `findByName(String name)` | Exact equality (`=`) | `SELECT * FROM students WHERE name = ?;` |
| `findByNameContaining(String keyword)` | Substring pattern (`LIKE %...%`) | `SELECT * FROM students WHERE name LIKE '%keyword%';` |
| `findByEmail(String email)` | Exact equality (`=`) | `SELECT * FROM students WHERE email = ?;` |

No SQL or `@Query` annotations are written manually. Spring parses the method name tokens (`findBy`, `Name`, `Containing`, `Email`), matches them to the entity's field names, and creates parameter-bound SQL queries automatically.

---

## 13. Explanation of Pagination
When working with hundreds or thousands of records, returning all records at once is slow and wastes memory. Pagination retrieves data in discrete chunks or "pages".

### Endpoint:
```java
@GetMapping("/students/page")
public Page<Student> getStudentsPage(Pageable pageable) {
    return studentRepository.findAll(pageable);
}
```

### Key Concepts:
1. **Zero-Based Page Numbering:**
   - `page=0` is the **first** page.
   - `page=1` is the **second** page.
   - `page=n` is the `(n+1)`th page.
2. **Page Size (`size`):** Controls the maximum number of items per page (e.g. `size=2`).
3. **Sorting (`sort`):** Controls the order of records within pages (e.g. `sort=name,asc`).
4. **`Page<T>` Response Metadata:**
   The returned JSON contains both the data slice and metadata:
   - `content`: Array of student records for the requested page.
   - `totalElements`: Total number of records across all pages (e.g. 5).
   - `totalPages`: Total number of available pages (e.g. 3 when size=2).
   - `number`: The current page index (0, 1, 2...).
   - `size`: The requested page size.
   - `first` / `last`: Booleans indicating if current page is the first/last page.

---

## 14. Explanation of Sorting
Sorting orders records based on one or more entity attributes.

### Endpoint:
```java
@GetMapping("/students/sort/name")
public List<Student> getStudentsSortedByName() {
    return studentRepository.findAll(Sort.by("name"));
}
```

### Key Concepts:
- The sorting is executed by the **database** using `ORDER BY name ASC`, not sorted in Java memory after retrieval.
- Default direction is ascending (`ASC`).
- Example alphabetical ordering of seeded students:
  `Amit` &rarr; `Neha` &rarr; `Priya` &rarr; `Rahul` &rarr; `Ravi`.

---

## 15. API Endpoint Reference Table

| HTTP Method | Route | Description | Success Code |
|---|---|---|---|
| **GET** | `/students` | Get all student records | `200 OK` |
| **GET** | `/students/{id}` | Get student by primary key ID | `200 OK` / `404 Not Found` |
| **POST** | `/students` | Create new student (auto-generated ID) | `201 Created` / `409 Conflict` |
| **PUT** | `/students/{id}` | Update existing student name and email | `200 OK` / `404 Not Found` |
| **DELETE** | `/students/{id}` | Delete student by ID | `204 No Content` / `404 Not Found` |
| **GET** | `/students/search/name/{name}` | Derived query: Search by exact name | `200 OK` |
| **GET** | `/students/search/name/contains/{keyword}` | Derived query: Search name containing keyword | `200 OK` |
| **GET** | `/students/search/email/{email}` | Derived query: Search by email | `200 OK` |
| **GET** | `/students/page` | Pagination endpoint (`?page=0&size=2&sort=name,asc`) | `200 OK` |
| **GET** | `/students/sort/name` | Sorting: Get students sorted by name ASC | `200 OK` |
| **GET** | `/students/sort` | General sort (`?by=name&direction=asc`) | `200 OK` / `400 Bad Request` |

---

## 16. Sample Requests and Expected Responses

### 1. Get All Students
```http
GET http://localhost:8081/students
```
**Response (200 OK):**
```json
[
  { "id": 1, "name": "Rahul", "email": "rahul@example.com" },
  { "id": 2, "name": "Priya", "email": "priya@example.com" },
  { "id": 3, "name": "Amit", "email": "amit@example.com" },
  { "id": 4, "name": "Neha", "email": "neha@example.com" },
  { "id": 5, "name": "Ravi", "email": "ravi@example.com" }
]
```

### 2. Search by Name (Exact)
```http
GET http://localhost:8081/students/search/name/Rahul
```
**Response (200 OK):**
```json
[
  { "id": 1, "name": "Rahul", "email": "rahul@example.com" }
]
```

### 3. Search by Name Containing Keyword
```http
GET http://localhost:8081/students/search/name/contains/Rah
```
**Response (200 OK):**
```json
[
  { "id": 1, "name": "Rahul", "email": "rahul@example.com" }
]
```

### 4. Search by Email
```http
GET http://localhost:8081/students/search/email/rahul@example.com
```
**Response (200 OK):**
```json
[
  { "id": 1, "name": "Rahul", "email": "rahul@example.com" }
]
```

### 5. Pagination (`page=0&size=2`)
```http
GET http://localhost:8081/students/page?page=0&size=2&sort=name,asc
```
**Response (200 OK):**
```json
{
  "content": [
    { "id": 3, "name": "Amit", "email": "amit@example.com" },
    { "id": 4, "name": "Neha", "email": "neha@example.com" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2
  },
  "totalElements": 5,
  "totalPages": 3,
  "last": false,
  "size": 2,
  "number": 0,
  "first": true,
  "empty": false
}
```

### 6. Sort by Name Ascending
```http
GET http://localhost:8081/students/sort/name
```
**Response (200 OK):**
```json
[
  { "id": 3, "name": "Amit", "email": "amit@example.com" },
  { "id": 4, "name": "Neha", "email": "neha@example.com" },
  { "id": 2, "name": "Priya", "email": "priya@example.com" },
  { "id": 1, "name": "Rahul", "email": "rahul@example.com" },
  { "id": 5, "name": "Ravi", "email": "ravi@example.com" }
]
```

---

## 17. How to Insert Sample Students

The application automatically seeds the 5 required students on startup if the database is empty:
- **Rahul** (`rahul@example.com`)
- **Priya** (`priya@example.com`)
- **Amit** (`amit@example.com`)
- **Neha** (`neha@example.com`)
- **Ravi** (`ravi@example.com`)

You can also create students manually using `curl`.

### Windows CMD (Command Prompt):
```cmd
curl -X POST http://localhost:8081/students -H "Content-Type: application/json" -d "{\"name\":\"Rahul\",\"email\":\"rahul@example.com\"}"
curl -X POST http://localhost:8081/students -H "Content-Type: application/json" -d "{\"name\":\"Priya\",\"email\":\"priya@example.com\"}"
curl -X POST http://localhost:8081/students -H "Content-Type: application/json" -d "{\"name\":\"Amit\",\"email\":\"amit@example.com\"}"
curl -X POST http://localhost:8081/students -H "Content-Type: application/json" -d "{\"name\":\"Neha\",\"email\":\"neha@example.com\"}"
curl -X POST http://localhost:8081/students -H "Content-Type: application/json" -d "{\"name\":\"Ravi\",\"email\":\"ravi@example.com\"}"
```

### Windows PowerShell:
```powershell
Invoke-RestMethod -Uri "http://localhost:8081/students" -Method POST -ContentType "application/json" -Body '{"name":"Rahul","email":"rahul@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8081/students" -Method POST -ContentType "application/json" -Body '{"name":"Priya","email":"priya@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8081/students" -Method POST -ContentType "application/json" -Body '{"name":"Amit","email":"amit@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8081/students" -Method POST -ContentType "application/json" -Body '{"name":"Neha","email":"neha@example.com"}'
Invoke-RestMethod -Uri "http://localhost:8081/students" -Method POST -ContentType "application/json" -Body '{"name":"Ravi","email":"ravi@example.com"}'
```

---

## 18. How to Run Automated Tests
Execute the full automated test suite containing 16 unit and integration tests across `StudentRepositoryTest` and `HelloControllerTest`:

### Windows:
```powershell
.\mvnw.cmd clean test
```

### Linux / macOS:
```bash
./mvnw clean test
```

### Expected Output:
```text
[INFO] Running com.example.uit067.studentmanagement.controller.HelloControllerTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.uit067.studentmanagement.repository.StudentRepositoryTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 19. Common Errors and Troubleshooting

### 1. `Port 8081 is already in use`
- **Cause:** Another instance of the application or process is occupying port 8081.
- **Fix (Windows):** Find and terminate the process holding port 8081:
  ```powershell
  Get-Process -Id (Get-NetTCPConnection -LocalPort 8081).OwningProcess | Stop-Process -Force
  ```
  Or change `server.port=8082` in `src/main/resources/application.properties`.

### 2. `HTTP 409 Conflict: A student with this email address already exists`
- **Cause:** Attempting to insert or update a student with an email that is already registered (`email` column has unique constraint).
- **Fix:** Use a unique email address for each student.

### 3. `HTTP 400 Bad Request: Invalid sort property`
- **Cause:** Requesting `/students/sort?by=xyz` where `xyz` is not in the allowed list (`id`, `name`, `email`).
- **Fix:** Supply a valid entity property name: `name`, `email`, or `id`.

### 4. URL Encoding for Email in Path Variable
- **Issue:** Some HTTP clients or browsers may treat `@` or `.` in `/students/search/email/rahul@example.com` unexpectedly.
- **Fix:** Use standard URL encoding if needed (`%40` for `@`):
  ```http
  GET /students/search/email/rahul%40example.com
  ```

---

## 20. How to Switch to MySQL
To run the project with a persistent MySQL database instead of the default in-memory H2:

1. **Install and Start MySQL Server:**  
   Ensure MySQL is running on `localhost:3306`.
2. **Create the Database:**
   ```sql
   CREATE DATABASE studentdb;
   ```
3. **Update `src/main/resources/application.properties`:**  
   Comment out the H2 settings and uncomment the MySQL block:
   ```properties
   # H2 Configuration (Commented out):
   # spring.datasource.url=jdbc:h2:mem:studentdb...

   # MySQL Configuration (Active):
   spring.datasource.url=jdbc:mysql://localhost:3306/studentdb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password

   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   ```
4. **Re-run the Application:**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   Hibernate will automatically create the `students` table in your MySQL `studentdb` schema.
