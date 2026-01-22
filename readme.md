# 🔐 Secure Notes Management System

A **production-ready secure web application** demonstrating enterprise-grade **authentication**, **authorization**, and **access control** mechanisms.

Built for academic coursework (**Lab 10, 11–12**) covering HTTP implementation, Spring Security, and secure CRUD operations.

---

## 📚 Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#️-architecture)
- [Security Implementation](#-security-implementation)
- [Database Schema](#️-database-schema)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Testing](#-testing)
- [Project Structure](#-project-structure)
- [Lab Requirements Coverage](#-lab-requirements-coverage)
- [Security Best Practices](#-security-best-practices-demonstrated)
- [Future Enhancements](#-future-enhancements)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🎯 Project Overview

**Secure Notes Management System** is a full-stack web application that allows users to create, manage, and securely store private notes.

The application demonstrates **industry-standard security practices**, including:

- ✅ Session-Based Authentication (Spring Security)
- ✅ BCrypt Password Hashing (10 rounds)
- ✅ User-Owned Resource Isolation
- ✅ CSRF Protection
- ✅ SQL Injection Prevention
- ✅ Server-Side Input Validation (JSR-380)

### 🔑 Core Security Principle

> **Each user can ONLY access their own data.**

All access control checks are enforced at the **service layer**, preventing horizontal privilege escalation.

---

## ✨ Features

### 👤 User Management

- Secure Registration with email validation
- Session-Based Login (Spring Security)
- BCrypt password hashing with salt
- Automatic session timeout
- Secure logout handling

### 📝 Notes Management

- Create notes (title + content)
- View all owned notes
- Edit existing notes
- Delete notes
- Automatic filtering by authenticated user

### 🛡️ Security Features

- CSRF tokens on all state-changing operations
- Prepared statements (SQL injection prevention)
- Server-side input validation
- Safe error handling (no stack traces exposed)
- Session fixation protection

---

## 💻 Technology Stack

| Category | Technology | Version | Purpose |
|--------|-----------|---------|--------|
| Language | Java | 21 | Application development |
| Framework | Spring Boot | 3.4.1 | Application framework |
| Security | Spring Security | 6.x | Authentication & authorization |
| Database | H2 | Latest | In-memory database |
| Data Access | Spring JDBC | — | Database access |
| Template Engine | Thymeleaf | — | Server-side rendering |
| Validation | Hibernate Validator | — | JSR-380 validation |
| Build Tool | Maven | — | Dependency management |
| Hashing | BCrypt | — | Secure password storage |

---

## 🏗️ Architecture

### Layered Architecture Pattern

┌────────────────────────────────────────────┐
│ Presentation Layer │
│ Controllers + Thymeleaf Templates │
└──────────────────┬─────────────────────────┘
│
┌──────────────────▼─────────────────────────┐
│ Service Layer │
│ Business logic + access control │
└──────────────────┬─────────────────────────┘
│
┌──────────────────▼─────────────────────────┐
│ Repository Layer │
│ JdbcTemplate + prepared statements │
└──────────────────┬─────────────────────────┘
│
┌──────▼──────┐
│ H2 Database │
└─────────────┘


### Design Principles

- Separation of Concerns
- Dependency Injection
- Secure-by-default routing
- Fail-safe defaults

---

## 🔐 Security Implementation

### 1️⃣ Authentication Flow

Registration
→ Password validation
→ BCrypt hashing
→ Store hash in DB
→ Login
→ Session creation


### 2️⃣ Password Security

```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
String hash = encoder.encode(password);
Only hashed passwords are stored:

INSERT INTO users (email, password)
VALUES ('user@example.com', '$2a$10$N9qo8uLO...');
3️⃣ Access Control (Ownership Enforcement)
if (!note.getUserId().equals(currentUser.getId())) {
    throw new AccessDeniedException("Forbidden");
}
Prevents:
Attack	Result
Guessing note IDs	403 Forbidden
URL manipulation	Access denied
Stolen session	Limited to owner data
4️⃣ SQL Injection Prevention
❌ Vulnerable

String sql = "SELECT * FROM notes WHERE id = " + id;
✅ Secure

String sql = "SELECT * FROM notes WHERE id = ?";
jdbcTemplate.query(sql, rowMapper, id);
5️⃣ CSRF Protection
http.csrf(csrf ->
    csrf.csrfTokenRepository(
        CookieCsrfTokenRepository.withHttpOnlyFalse()
    )
);
Forms include:

<input type="hidden" name="_csrf" value="${_csrf.token}">
6️⃣ Input Validation
@NotBlank
@Size(max = 255)
private String title;
Custom password validation enforces:

Minimum 8 characters

Uppercase

Lowercase

Digit

Special character

🗄️ Database Schema
Entity Relationship Diagram
USERS (1) ──────── (M) NOTES
USERS Table
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  email VARCHAR(100) UNIQUE,
  password VARCHAR(60),
  role VARCHAR(20),
  created_at TIMESTAMP
);
NOTES Table
CREATE TABLE notes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title VARCHAR(255),
  content TEXT,
  created_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
🚀 Getting Started
Prerequisites
java -version   # Java 21+
mvn -version    # Maven 3.8+
Installation
git clone https://github.com/yourusername/secure-notes-app.git
cd secure-notes-app
mvn clean install
mvn spring-boot:run
Visit: http://localhost:8080

📡 API Documentation
Public Endpoints
Method	Route	Description
GET	/login	Login page
POST	/login	Authenticate
GET	/register	Registration page
POST	/register	Create user
Protected Endpoints
Method	Route	Description
GET	/dashboard	View notes
POST	/dashboard	Create note
GET	/dashboard/edit/{id}	Edit note
POST	/dashboard/delete/{id}	Delete note
🧪 Testing
Security Tests Included
Password strength enforcement

Access control (403 on чуж data)

CSRF protection

SQL injection prevention

Session invalidation on logout

📁 Project Structure
secure-notes-app/
├── controller/
├── service/
├── repository/
├── model/
├── config/
├── exception/
└── templates/
📋 Lab Requirements Coverage
✅ Lab 10 — HTTP
Routing & status codes

Form handling

Validation

CSRF tokens

✅ Lab 11 — Authentication
Spring Security

BCrypt hashing

Session management

✅ Lab 12 — Authorization
User-owned data

Service-layer enforcement

Secure CRUD

🛡️ Security Best Practices Demonstrated
Defense in depth

Least privilege

Secure defaults

Fail-safe access control

Input distrust by default

🔮 Future Enhancements
Password reset via email

Multi-factor authentication

Rate limiting

Audit logging

REST API with JWT

Note sharing

Full-text search

🤝 Contributing
Academic project — suggestions welcome via pull requests.

📝 License
MIT License

⚠️ Educational Disclaimer
Before production deployment:

Replace H2 with PostgreSQL/MySQL

Enable HTTPS

Add logging & monitoring

Perform security audits
