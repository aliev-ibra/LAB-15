# Spring Boot Security Labs (Lab 10–13)

This repository contains a **complete multi‑lab Spring Boot project** covering the evolution from **basic HTTP handling** to **advanced security hardening**.
Each lab builds on the previous one and demonstrates progressively more **real‑world, production‑grade security concepts**.

---

## 🎯 Project Scope

The goal of this project is to **prove security competence step‑by‑step**, not just implement features.

Covered topics:

* HTTP & REST basics
* Validation & exception handling
* Authentication & authorization
* Password hashing
* Access control & data isolation (IDOR prevention)
* HTTPS & TLS
* JWT & refresh token rotation
* Security headers
* Logging, rate limiting, and hardening

---

## 🟢 Lab 10 – HTTP & Spring Boot Basics

**Focus:** Correct request handling and proper HTTP responses.

### Demonstrated Concepts

* GET / POST mappings
* `ResponseEntity` usage
* DTO‑based validation
* Centralized exception handling

### Key Files

| File                                    | Purpose                                  |
| --------------------------------------- | ---------------------------------------- |
| `controller/NoteController.java`        | GET/POST endpoints, ResponseEntity usage |
| `dto/NoteDTO.java`                      | Note data transfer + validation          |
| `dto/UserDTO.java`                      | User input validation (`@Valid`)         |
| `exception/GlobalExceptionHandler.java` | 404 / 400 / 500 error handling           |

This lab ensures **clean HTTP semantics** and predictable API behavior.

---

## 🔵 Lab 11 – Authentication & Authorization

**Focus:** User identity, login flow, and password protection.

### Demonstrated Concepts

* Form‑based authentication
* Custom user loading from database
* Secure password hashing

### Key Files

| File                                     | Purpose                     |
| ---------------------------------------- | --------------------------- |
| `config/SecurityConfig.java`             | Login, logout, access rules |
| `security/CustomUserDetailsService.java` | Load users from DB          |
| `service/UserService.java`               | BCrypt password hashing     |

### Password Security

```java
new BCryptPasswordEncoder(10)
```

* Explicit strength configuration
* Resistant to brute‑force attacks
* Matches real‑world security standards

---

## 🟡 Lab 12 – Access Control & Data Isolation

**Focus:** Preventing users from accessing or modifying others’ data.

⚠️ **This lab addresses one of the most common real‑world vulnerabilities: IDOR.**

### Demonstrated Concepts

* Row‑level security
* Ownership validation
* Defense‑in‑depth (Service + SQL)

### Key Files

| File                             | Purpose                                     |
| -------------------------------- | ------------------------------------------- |
| `model/Note.java`                | `@ManyToOne` relationship with User         |
| `repository/NoteRepository.java` | SQL isolation (`WHERE user_id = ?`)         |
| `service/NoteService.java`       | Current user ID via `SecurityContextHolder` |

### Critical SQL Pattern

```sql
WHERE id = ? AND user_id = ?
```

This guarantees:

* No horizontal privilege escalation
* Safe behavior even if IDs are guessed

---

## 🔴 Lab 13 – Security Hardening (Professional Level)

**Focus:** Production‑grade security controls.

### 1️⃣ HTTPS / TLS

* Enabled via `application.properties`
* Uses `keystore.p12`

Key files:

* `application.properties`
* `keystore.p12`
* `HTTPS_DEMO.md`

---

### 2️⃣ JWT & Refresh Token Rotation

* Stateless authentication
* Token expiration & renewal

| File                               | Purpose                     |
| ---------------------------------- | --------------------------- |
| `security/JwtUtils.java`           | JWT generation & validation |
| `service/RefreshTokenService.java` | Token rotation logic        |
| `model/RefreshToken.java`          | Refresh token entity        |

---

### 3️⃣ Security Headers

Configured in `SecurityConfig.java`:

* Content Security Policy (CSP)
* HTTP Strict Transport Security (HSTS)
* X‑Frame‑Options

Protects against:

* XSS
* Clickjacking
* SSL stripping

---

### 4️⃣ Authentication Logging

| File                                 | Purpose                         |
| ------------------------------------ | ------------------------------- |
| `security/AuthenticationEvents.java` | Logs successful & failed logins |

* Sensitive data masked
* Audit‑friendly logging

---

### 5️⃣ Rate Limiting

| File                          | Purpose                          |
| ----------------------------- | -------------------------------- |
| `filter/RateLimitFilter.java` | Blocks excessive requests per IP |

Protects against:

* Brute‑force login attempts
* DoS abuse

---

## 🗂 Database & Migration

* Flyway migrations located in `db/migration`
* User & Note ownership enforced via foreign keys

```sql
user_id BIGINT NOT NULL
```

---

## 🧪 Testing & Verification

Scripts included:

* `test_endpoints.ps1`
* `test_security_notes.ps1`
* `verify_lab.ps1`

Build logs:

* `build_log_final.txt`

---

## 🧠 Security Philosophy

> **Never trust the client. Never trust the URL. Never trust the ID.**

Security controls are applied:

* At controller level
* At service level
* At database level

---

## ▶️ How to Run

```bash
mvn clean install
mvn spring-boot:run
```

Access:

```
https://localhost:8443
```

---

## 📚 Documentation

* `API_DOCS.md`
* `SECURITY_NOTES.md`
* `LAB13_SUMMARY.md`
* `PROJECT_DEMO.md`

---

## ✨ Author

**Ibrahim Aliyev**
Cybersecurity & Backend Developer

---

✅ **Labs 10–13 Successfully Implemented**
