# Secure Notes Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Security](https://img.shields.io/badge/Security-Spring%20Security%206-red.svg)](https://spring.io/projects/spring-security)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> A production-ready secure web application demonstrating enterprise-grade authentication, authorization, and access control mechanisms. Built for academic coursework (Lab 10, 11-12) covering HTTP implementation, Spring Security, and secure CRUD operations.

---

## 📚 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Security Implementation](#-security-implementation)
- [Database Schema](#-database-schema)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Lab Requirements Coverage](#-lab-requirements-coverage)

---

## 🎯 Project Overview

**Secure Notes Management System** is a full-stack web application that allows users to create, manage, and securely store private notes. The application demonstrates industry-standard security practices including:

- ✅ **Session-Based Authentication** with Spring Security
- ✅ **BCrypt Password Hashing** (strength: 10 rounds)
- ✅ **User-Owned Resource Isolation** (horizontal privilege escalation prevention)
- ✅ **CSRF Protection** on all state-changing operations
- ✅ **SQL Injection Prevention** via prepared statements
- ✅ **Input Validation** with Bean Validation (JSR-380)

### Core Security Principle

> **Each user can ONLY access their own data.** The application enforces strict access control at the service layer, preventing users from viewing, editing, or deleting notes created by other users.

---

## ✨ Features

### User Management
- **Secure Registration**: Email validation, password strength enforcement
- **Session-Based Login**: Spring Security form authentication
- **Password Security**: BCrypt hashing with salt (10 rounds)
- **Session Management**: Automatic timeout, secure logout

### Notes Management
- **Create Notes**: Add new notes with title and content
- **View Notes**: See all your notes in a dashboard
- **Update Notes**: Edit existing note content
- **Delete Notes**: Remove unwanted notes
- **Access Control**: Automatic filtering by authenticated user

### Security Features
- **CSRF Tokens**: Protection against cross-site request forgery
- **Prepared Statements**: SQL injection prevention
- **Input Validation**: Server-side validation on all inputs
- **Error Handling**: Safe error messages (no stack traces exposed)
- **Session Fixation Protection**: Automatic session regeneration on login

---

## 💻 Technology Stack

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 21 | Application development |
| **Framework** | Spring Boot | 3.4.1 | Application framework |
| **Security** | Spring Security | 6.x | Authentication & Authorization |
| **Database** | H2 Database | Latest | In-memory data persistence |
| **Data Access** | Spring JDBC | - | Database operations with `JdbcTemplate` |
| **Template Engine** | Thymeleaf | - | Server-side HTML rendering |
| **Validation** | Hibernate Validator | - | Bean Validation (JSR-380) |
| **Build Tool** | Maven | - | Dependency management |
| **Password Hashing** | BCrypt | - | Secure password storage |

---

## 🏗️ Architecture

### Layered Architecture Pattern
```
┌─────────────────────────────────────────────────────────┐
│                  Presentation Layer                      │
│  Controllers + Thymeleaf Templates                       │
│  - Handles HTTP requests/responses                       │
│  - Form validation                                       │
│  - Maps DTOs                                             │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                   Service Layer                          │
│  - Business logic                                        │
│  - Access control enforcement                            │
│  - User ownership verification                           │
└────────────────────┬────────────────────────────────────┘
                     │
┌────────────────────▼────────────────────────────────────┐
│                 Repository Layer                         │
│  - JdbcTemplate with prepared statements                 │
│  - SQL injection prevention                              │
│  - Database operations                                   │
└────────────────────┬────────────────────────────────────┘
                     │
              ┌──────▼──────┐
              │ H2 Database │
              │  (In-Memory)│
              └─────────────┘
```

### Design Principles

- **Separation of Concerns**: Each layer has single responsibility
- **Dependency Injection**: Spring manages all beans
- **Security by Default**: All routes protected unless explicitly public
- **Fail-Safe Defaults**: Deny access unless explicitly granted

---

## 🔐 Security Implementation

### 1. Authentication Flow
```
User Registration
    ↓
Password Validation (min 8 chars, uppercase, lowercase, digit, special char)
    ↓
BCrypt Hashing (10 rounds + random salt)
    ↓
Store in Database (only hash stored, never plain text)
    ↓
User Login
    ↓
Spring Security validates credentials (BCrypt.matches())
    ↓
Create Session (JSESSIONID cookie)
    ↓
User can access protected resources
```

### 2. Password Security

**Registration Process:**
```java
@Service
public class UserService implements UserDetailsService {
    
    private final BCryptPasswordEncoder passwordEncoder = 
        new BCryptPasswordEncoder(10);
    
    public void registerUser(String username, String email, String password) {
        // Validate password strength
        validatePassword(password);
        
        // Hash password (NEVER store plain text)
        String hashedPassword = passwordEncoder.encode(password);
        
        // Save to database
        userRepository.save(username, email, hashedPassword);
    }
}
```

**Database Storage:**
```sql
-- ❌ WRONG: Plain text password (NEVER DO THIS)
INSERT INTO users (email, password) 
VALUES ('user@example.com', 'MyPassword123');

-- ✅ CORRECT: BCrypt hash
INSERT INTO users (email, password) 
VALUES ('user@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');
```

### 3. Access Control (Preventing Unauthorized Access)

**Service Layer Security Check:**
```java
@Service
public class NoteService {
    
    public Note getNoteById(Long noteId, String username) {
        // Step 1: Fetch note from database
        Note note = noteRepository.findById(noteId);
        
        if (note == null) {
            throw new ResourceNotFoundException("Note not found");
        }
        
        // Step 2: Get current user
        User currentUser = userRepository.findByUsername(username);
        
        // Step 3: CRITICAL SECURITY CHECK
        if (!note.getUserId().equals(currentUser.getId())) {
            // User is trying to access someone else's note
            throw new AccessDeniedException("You do not have permission to access this note");
        }
        
        // Step 4: User owns the note, return it
        return note;
    }
}
```

**What This Prevents:**

| Attack Scenario | How It's Blocked |
|----------------|------------------|
| User A tries to view User B's note by guessing ID | `AccessDeniedException` thrown → 403 Forbidden |
| User manipulates URL `/dashboard/delete/5` (not their note) | Ownership check fails → Note not deleted |
| Attacker with stolen session tries to access data | Limited to victim's own notes only |

### 4. SQL Injection Prevention

**Vulnerable Code (What We DON'T Do):**
```java
// ❌ DANGER: String concatenation allows SQL injection
String sql = "SELECT * FROM notes WHERE id = " + noteId;
jdbcTemplate.query(sql, rowMapper);

// Attacker can send: noteId = "1 OR 1=1" 
// Result: Returns ALL notes in database!
```

**Secure Implementation (What We DO):**
```java
// ✅ SAFE: Prepared statements with parameterized queries
String sql = "SELECT * FROM notes WHERE id = ?";
jdbcTemplate.query(sql, rowMapper, noteId);

// Input is treated as data, not SQL code
// Special characters are automatically escaped
```

### 5. CSRF Protection
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            );
        
        return http.build();
    }
}
```

**How It Works:**

1. Server generates unique token for each session
2. Token embedded in forms as hidden field:
```html
   
```
3. On form submission, server validates token
4. If tokens don't match → `403 Forbidden`

### 6. Input Validation
```java
// Entity-level validation
public class Note {
    
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;
    
    @Size(max = 10000, message = "Content cannot exceed 10000 characters")
    private String content;
}

// Custom password validator
public void validatePassword(String password) {
    if (password.length() < 8) {
        throw new ValidationException("Password must be at least 8 characters");
    }
    if (!password.matches(".*[A-Z].*")) {
        throw new ValidationException("Password must contain uppercase letter");
    }
    if (!password.matches(".*[a-z].*")) {
        throw new ValidationException("Password must contain lowercase letter");
    }
    if (!password.matches(".*[0-9].*")) {
        throw new ValidationException("Password must contain digit");
    }
    if (!password.matches(".*[!@#$%^&*].*")) {
        throw new ValidationException("Password must contain special character");
    }
}
```

---

## 🗄️ Database Schema

### Entity Relationship Diagram
```
┌─────────────────────┐
│       USERS         │
├─────────────────────┤
│ id (PK)             │◄──────────┐
│ username            │           │
│ email (UNIQUE)      │           │
│ password (HASHED)   │           │ ONE-TO-MANY
│ role                │           │ (One user, many notes)
└─────────────────────┘           │
                                  │
┌─────────────────────┐           │
│       NOTES         │           │
├─────────────────────┤           │
│ id (PK)             │           │
│ user_id (FK)        │───────────┘
│ title               │
│ content             │
│ created_at          │
└─────────────────────┘
```

### Table Definitions

**USERS Table:**
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,  -- BCrypt hash length
    role VARCHAR(20) DEFAULT 'ROLE_USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster lookups
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
```

**NOTES Table:**
```sql
CREATE TABLE notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key with cascade delete
    CONSTRAINT fk_notes_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE
);

-- Index for access control queries
CREATE INDEX idx_notes_user_id ON notes(user_id);
```

**Key Design Decisions:**

- `ON DELETE CASCADE`: When user is deleted, all their notes are automatically removed
- `email UNIQUE`: Prevents duplicate accounts
- `password VARCHAR(60)`: BCrypt hashes are exactly 60 characters
- Indexed columns for performance on frequent queries

---

## 🚀 Getting Started

### Prerequisites
```bash
# Check Java version
java -version
# Output: java version "21" or higher

# Check Maven
mvn -version
# Output: Apache Maven 3.8+
```

### Installation

**Step 1: Clone Repository**
```bash
git clone https://github.com/yourusername/secure-notes-app.git
cd secure-notes-app
```

**Step 2: Build Project**
```bash
mvn clean install
```

**Step 3: Run Application**
```bash
mvn spring-boot:run
```

**Step 4: Access Application**

Open your browser to: **http://localhost:8080**

You should see the login page.

### First-Time Setup

1. Click **"Register"** link
2. Fill in registration form:
   - **Username**: `testuser`
   - **Email**: `test@example.com`
   - **Password**: `SecurePass123!`
3. Click **"Sign Up"**
4. You'll be redirected to login page
5. Login with your credentials
6. Start creating notes!

---

## 📡 API Documentation

### Public Endpoints (No Authentication Required)

| Method | Route | Description | Request Body |
|--------|-------|-------------|--------------|
| `GET` | `/login` | Display login form | - |
| `POST` | `/login` | Process login credentials | `username`, `password` |
| `GET` | `/register` | Display registration form | - |
| `POST` | `/register` | Create new user account | `username`, `email`, `password` |

### Protected Endpoints (Authentication Required)

| Method | Route | Description | Access Control |
|--------|-------|-------------|----------------|
| `GET` | `/dashboard` | View all user's notes | Only shows current user's notes |
| `POST` | `/dashboard` | Create new note | Automatically tied to current user |
| `GET` | `/dashboard/edit/{id}` | Edit note form | Only if user owns the note |
| `POST` | `/dashboard/edit/{id}` | Update note | Only if user owns the note |
| `POST` | `/dashboard/delete/{id}` | Delete note | Only if user owns the note |
| `GET` | `/logout` | End session | - |

### HTTP Status Codes

| Code | Meaning | When It's Returned |
|------|---------|-------------------|
| `200 OK` | Success | Successful GET, POST, edit |
| `302 Found` | Redirect | After login, registration, logout |
| `400 Bad Request` | Validation failed | Invalid form input |
| `401 Unauthorized` | Not authenticated | Accessing protected page without login |
| `403 Forbidden` | Access denied | CSRF token mismatch, accessing another user's note |
| `404 Not Found` | Resource not found | Non-existent note ID |

---

## 🧪 Testing

### Manual Testing Guide

**Test 1: Password Validation**

1. Go to `/register`
2. Try weak password: `123`
   - **Expected:** Error message "Password must be at least 8 characters"
3. Try password without uppercase: `password123`
   - **Expected:** Error message "Password must contain uppercase letter"
4. Use strong password: `SecurePass123!`
   - **Expected:** Registration succeeds

**Test 2: Access Control**

1. Register User A: `userA@example.com`
2. Login as User A and create a note (note ID = 1)
3. Logout
4. Register User B: `userB@example.com`
5. Login as User B
6. Try to access: `/dashboard/edit/1` (User A's note)
   - **Expected:** Error page or 403 Forbidden
7. Check dashboard
   - **Expected:** User B sees ONLY their own notes (empty if they haven't created any)

**Test 3: CSRF Protection**

1. Login to application
2. Open browser console
3. Try to submit form without CSRF token:
```javascript
   fetch('/dashboard', {
     method: 'POST',
     headers: {'Content-Type': 'application/x-www-form-urlencoded'},
     body: 'title=Test&content=Content'
   });
```
   - **Expected:** 403 Forbidden

**Test 4: SQL Injection Prevention**

1. Login to application
2. Create a note with malicious title:
```
   Title: '; DROP TABLE notes; --
```
3. Check if note is created normally
   - **Expected:** Title is stored as literal string, no SQL execution
4. Verify in H2 Console that `notes` table still exists

**Test 5: Session Management**

1. Login to application
2. Open dashboard
3. Copy `JSESSIONID` cookie value
4. Open incognito window
5. Manually set the same cookie
6. Try to access `/dashboard`
   - **Expected:** Access granted (session is valid)
7. In original window, click Logout
8. In incognito window, refresh page
   - **Expected:** Redirected to login (session invalidated)

### Database Inspection (H2 Console)

**Access H2 Console:**

1. Navigate to: `http://localhost:8080/h2-console`
2. **JDBC URL:** `jdbc:h2:mem:testdb`
3. **Username:** `sa`
4. **Password:** (leave empty)
5. Click **"Connect"**

**Verify Password Hashing:**
```sql
SELECT username, email, password FROM users;
```

**Expected Output:**
```
USERNAME  | EMAIL             | PASSWORD
----------|-------------------|------------------------------------------
testuser  | test@example.com  | $2a$10$N9qo8uLOickgx2ZMRZoMy...
```

> ✅ Password is hashed (starts with `$2a$10$`), NOT plain text

**Verify User-Note Relationship:**
```sql
SELECT 
    n.id AS note_id,
    n.title,
    n.user_id,
    u.username,
    u.email
FROM notes n
JOIN users u ON n.user_id = u.id;
```

**Expected Output:**
```
NOTE_ID | TITLE      | USER_ID | USERNAME | EMAIL
--------|------------|---------|----------|------------------
1       | My Note    | 1       | testuser | test@example.com
2       | Another    | 1       | testuser | test@example.com
```

> ✅ All notes are correctly linked to their owner via `user_id`

---

## 📁 Project Structure
```
secure-notes-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/securenotes/
│   │   │   ├── SecureNotesApplication.java
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java         # Spring Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java         # Registration, login
│   │   │   │   └── NoteController.java         # CRUD operations
│   │   │   ├── service/
│   │   │   │   ├── UserService.java            # User management + UserDetailsService
│   │   │   │   └── NoteService.java            # Business logic + access control
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java         # User data access (JDBC)
│   │   │   │   └── NoteRepository.java         # Note data access (JDBC)
│   │   │   ├── model/
│   │   │   │   ├── User.java                   # User entity
│   │   │   │   └── Note.java                   # Note entity
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java
│   │   │       └── AccessDeniedException.java
│   │   └── resources/
│   │       ├── application.properties           # App configuration
│   │       ├── schema.sql                       # Database schema
│   │       ├── data.sql                         # Initial data (optional)
│   │       └── templates/                       # Thymeleaf templates
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── dashboard.html
│   │           └── edit-note.html
│   └── test/
│       └── java/com/example/securenotes/
│           └── SecurityTests.java               # Security tests
├── pom.xml                                      # Maven dependencies
├── .gitignore
└── README.md                                    # This file
```

---

## 📋 Lab Requirements Coverage

### ✅ Lab 10: HTTP Implementation

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| API route design | ✅ | All routes documented with methods, params, status codes |
| GET/POST endpoints | ✅ | `/login`, `/register`, `/dashboard`, `/dashboard/edit/{id}` |
| Read request headers | ✅ | CSRF tokens, session cookies |
| Parse JSON/form data | ✅ | Form data with `@ModelAttribute` in controllers |
| DTO validation | ✅ | `@NotBlank`, `@Email`, `@Size` on entities |
| Custom validator | ✅ | Password strength validation in `UserService` |
| Proper status codes | ✅ | 200, 302, 400, 401, 403, 404 |
| Structured error responses | ✅ | Error pages with user-friendly messages |

### ✅ Lab 11: Authentication

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| Custom UserDetailsService | ✅ | `UserService implements UserDetailsService` |
| SecurityFilterChain | ✅ | Configured in `SecurityConfig.java` |
| Public routes | ✅ | `/login`, `/register` permit all |
| Protected routes | ✅ | `/dashboard/**` requires authentication |
| Login/logout | ✅ | Spring Security form login + logout |
| CSRF protection | ✅ | Enabled by default, tokens in forms |
| Password hashing | ✅ | BCrypt with strength 10 |
| Session management | ✅ | Session timeout configured |

### ✅ Lab 12: Authorization & Access Control

| Requirement | Status | Implementation |
|-------------|--------|----------------|
| New entity (Note) | ✅ | `Note.java` with title, content, user_id |
| Database schema | ✅ | `schema.sql` with users and notes tables |
| Repository layer | ✅ | `NoteRepository` using `JdbcTemplate` |
| Prepared statements | ✅ | All queries use parameterized SQL |
| Service layer | ✅ | `NoteService` with business logic |
| Access control | ✅ | Ownership checks in every method |
| Foreign key | ✅ | `notes.user_id` references `users.id` |
| CRUD operations | ✅ | Create, Read, Update, Delete all implemented |
| User-owned data | ✅ | Users can only access their own notes |
| Validation | ✅ | Input validation on all fields |
| SQL injection prevention | ✅ | Prepared statements prevent injection |

---

## 🛡️ Security Best Practices Demonstrated

### 1. Defense in Depth

- **Client-side validation**: HTML5 form constraints
- **Server-side validation**: Bean Validation in controllers
- **Service-layer checks**: Access control enforcement
- **Database constraints**: Foreign keys, unique constraints

### 2. Least Privilege

- Users only see their own data
- No admin functionality exposed
- Minimal database permissions needed

### 3. Secure by Default

- CSRF enabled automatically
- All routes protected unless explicitly public
- Sessions have timeout
- Passwords always hashed

### 4. Fail-Safe Defaults

- Unknown users cannot login
- Invalid note IDs return 404
- Accessing others' notes returns 403
- Database errors don't expose SQL

### 5. Don't Trust User Input

- All inputs validated
- SQL parameters escaped
- HTML output escaped by Thymeleaf
- File paths not exposed

---

## 🚨 Common Vulnerabilities PREVENTED

| Vulnerability | How We Prevent It |
|--------------|-------------------|
| **SQL Injection** | Prepared statements with `JdbcTemplate` |
| **XSS (Cross-Site Scripting)** | Thymeleaf auto-escapes HTML output |
| **CSRF** | Synchronizer token pattern enabled |
| **Session Fixation** | Spring Security regenerates session on login |
| **Broken Authentication** | BCrypt hashing, strong password policy |
| **Broken Access Control** | Service-layer ownership checks |
| **Sensitive Data Exposure** | Passwords never logged or displayed |
| **Security Misconfiguration** | Secure defaults, no unnecessary features |

---

## 🔮 Future Enhancements

- [ ] **Password Reset**: Email-based token system
- [ ] **Remember Me**: Persistent login functionality
- [ ] **Multi-Factor Authentication (MFA)**: TOTP support
- [ ] **Rate Limiting**: Prevent brute-force attacks
- [ ] **Audit Logging**: Track all security events
- [ ] **API Version**: REST API with JWT tokens
- [ ] **Rich Text Editor**: Markdown support for notes
- [ ] **Note Sharing**: Share notes with specific users
- [ ] **Tags/Categories**: Organize notes
- [ ] **Search**: Full-text search across notes

---

## 🤝 Contributing

This is an academic project, but suggestions are welcome:

1. Fork the repository
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

For security vulnerabilities, please email privately instead of opening a public issue.

---

## 📚 Learning Resources

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [BCrypt Explained](https://en.wikipedia.org/wiki/Bcrypt)
- [CSRF Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Cross-Site_Request_Forgery_Prevention_Cheat_Sheet.html)

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**[Your Name]**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- **Spring Security Team** for comprehensive documentation
- **OWASP** for security guidelines and best practices
- **Academic Advisors** for project requirements and feedback

---

**⚠️ Educational Disclaimer**

This application is built for educational purposes. Before deploying to production:

- [ ] Replace H2 with production database (PostgreSQL, MySQL)
- [ ] Enable HTTPS/TLS
- [ ] Implement proper logging and monitoring
- [ ] Conduct security audit
- [ ] Set up backup and recovery procedures
- [ ] Configure production-grade session management
- [ ] Implement rate limiting and DDoS protection

---
