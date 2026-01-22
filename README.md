# Secure Notes Management System

## Project Overview
This project is a **Secure Notes Management System** built with **Spring Boot** and **Thymeleaf**. It checks all the boxes for a secure web application, including robust user authentication, password hashing, CSRF protection, and role-based access control.

The system allows users to register, log in, and manage their private notes. Each note is strictly isolated; users can only view, edit, or delete notes they created.

## Features
- **User Registration**: Secure sign-up/login flow with validation.
- **Session-Based Authentication**: Uses Spring Security's `DaoAuthenticationProvider`.
- **Private Notes**: Create, Read, Update, and Delete (CRUD) notes.
- **Access Control**: Users are strictly prevented from accessing other users' data.
- **Security Best Practices**:
    -   **Password Hashing**: Uses **BCrypt** (strength 10) to salt and hash passwords.
    -   **CSRF Protection**: Enabled by default to prevent Cross-Site Request Forgery.
    -   **Input Validation**: Strict validation on usernames, emails, and passwords (min 8 chars, mixed case, special chars).
    -   **SQL Injection Prevention**: Uses `JdbcTemplate` with parameterized queries.

## Technology Stack
-   **Language**: Java 21
-   **Framework**: Spring Boot 3.4.1
-   **Security**: Spring Security 6
-   **Database**: H2 Database (In-Memory)
-   **Data Access**: Spring JDBC (`JdbcTemplate`)
-   **Frontend**: Thymeleaf (Server-Side Rendering)
-   **Build Tool**: Maven

## Security Implementation Details
### 1. Authentication
We implemented a custom `UserService` that implements `UserDetailsService`. Spring Security uses this to load users from our H2 database during login.
-   **Provider**: `DaoAuthenticationProvider` wired in `SecurityConfig`.
-   **Mechanism**: Form Login (`/login`).

### 2. Password Security
We use `BCryptPasswordEncoder`. 
> **Why BCrypt?** It uses a random salt for every password, making it resistant to rainbow table attacks. It is also slow by design, hindering brute-force attempts.

### 3. Authorization (Ownership Checks)
In `NoteService.java`, every data access method performs a check:
```java
if (!note.getUserId().equals(currentUser.getId())) {
    throw new AccessDeniedException("You do not have permission to access this note");
}
```
This ensures logical isolation of user data.

## Database Schema
The application uses two tables with a One-to-Many relationship:

### Tables
1.  **USERS**
    -   `id` (PK)
    -   `username`
    -   `email` (Unique)
    -   `password` (Hashed)
    -   `role` (Default: ROLE_USER)

2.  **NOTES**
    -   `id` (PK)
    -   `title`
    -   `content`
    -   `created_at`
    -   `user_id` (FK -> USERS.id)

## API / Route Documentation

| Method | Route | Description | auth Required |
| :--- | :--- | :--- | :--- |
| `GET` | `/login` | Shows login form | No |
| `POST` | `/login` | Processes login credentials | No |
| `GET` | `/register` | Shows registration form | No |
| `POST` | `/register` | Creates new user account | No |
| `GET` | `/dashboard` | View user's notes | **Yes** |
| `POST` | `/dashboard` | Create a new note | **Yes** |
| `POST` | `/dashboard/delete/{id}` | Delete a note | **Yes** |

## How to Run
1.  **Prerequisites**: Java 21+ and Maven.
2.  **Start Application**:
    ```bash
    mvn spring-boot:run
    ```
3.  **Access Application**:
    -   Open browser to: [http://localhost:8080/login](http://localhost:8080/login)
4.  **H2 Console** (For Debugging):
    -   URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    -   JDBC URL: `jdbc:h2:mem:testdb`
    -   User: `sa`, Password: *(empty)*
