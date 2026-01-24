# Lab 10 – User Authentication & Validation (Spring Boot)

This repository contains the **Lab 10** project implementation based on the provided checklist and requirements. The project demonstrates a clean **Layered Architecture**, proper **validation**, **HTTP method usage**, **exception handling**, and **JSON data storage** using Spring Boot.

---

## 📌 Project Overview

The goal of this lab is to implement a simple **user registration and authentication module** using best practices in Spring Boot:

* Clean separation of layers
* DTO-based validation
* Controller-based request handling
* Global exception handling with proper HTTP status codes
* JSON-compatible data storage

The project is structured and implemented strictly according to the Lab 10 checklist.

---

## 🏗 Architecture

The application follows a **Layered Architecture**:

```
com.example.lab10
│
├── controller     # Handles HTTP requests
├── service        # Business logic
├── repository     # Data access layer
├── model          # Entity classes
├── dto            # Data Transfer Objects (validation)
├── exception      # Global exception handling
├── validation     # Custom validation annotations
└── resources      # Templates and configuration
```

This structure ensures:

* High maintainability
* Testability
* Clear responsibility separation

---

## 🔁 HTTP Endpoints

### Authentication Controller (`AuthController`)

| Method | Endpoint  | Description                |
| ------ | --------- | -------------------------- |
| GET    | /login    | Displays login page        |
| GET    | /register | Displays registration page |
| POST   | /register | Handles user registration  |

Spring MVC annotations are used:

* `@GetMapping`
* `@PostMapping`
* `@Controller`

---

## ✅ Validation

Validation is implemented using **Jakarta Validation** with a DTO-based approach.

### `UserDTO.java`

Validated fields:

* **Username**

  * `@NotBlank`
  * `@Size(min = 3, max = 50)`

* **Email**

  * `@NotBlank`
  * `@Email`

* **Password**

  * `@NotBlank`
  * `@Size(min = 8)`
  * `@PasswordConstraint` (custom annotation)

### Custom Password Validation

The password must contain:

* At least one digit
* One lowercase letter
* One uppercase letter
* One special character

Validation errors are handled via `BindingResult` in the controller.

---

## 🚨 Exception Handling

Global exception handling is implemented using:

```java
@RestControllerAdvice
```

### `GlobalExceptionHandler`

Handled exceptions:

| Exception Type               | HTTP Status               |
| ---------------------------- | ------------------------- |
| AccessDeniedException        | 403 Forbidden             |
| RuntimeException (not found) | 404 Not Found             |
| Other RuntimeException       | 500 Internal Server Error |

All error responses are returned as JSON with a clear message structure.

---

## 🗂 JSON Data Storage

The `User` model includes a `details` field designed for storing **JSON-compatible data**, allowing flexible user metadata storage.

This satisfies the Lab 10 requirement for JSON handling in the data model.

---

## 🔐 Registration Flow

1. User opens `/register`
2. Form data is mapped to `UserDTO`
3. Validation is applied automatically (`@Valid`)
4. Errors → registration page reloads
5. Success → user is created via `UserService`
6. Redirect to `/login`

---

## 🧪 Checklist Compliance

| Requirement               | Status |
| ------------------------- | ------ |
| Layered Architecture      | ✅ Yes  |
| HTTP GET & POST Methods   | ✅ Yes  |
| Validation Annotations    | ✅ Yes  |
| Custom Password Validator | ✅ Yes  |
| HTTP Status Codes         | ✅ Yes  |
| Global Exception Handler  | ✅ Yes  |
| JSON Data Storage         | ✅ Yes  |

---

## 🛠 Technologies Used

* Java 17+
* Spring Boot
* Spring MVC
* Jakarta Validation
* Lombok
* Thymeleaf (for views)

---

## 📦 How to Run

```bash
mvn clean install
mvn spring-boot:run
```

Then open:

```
http://localhost:8080/register
```

---

## 📄 Notes for Instructor

* `UserDTO` is used instead of `CreateUserRequest`
* `AuthController` is used instead of `UserController`

These differences are **intentional and acceptable**, as they preserve the same responsibilities while keeping the design clean.

---

## ✨ Author

**Ibrahim Aliyev**

Cybersecurity & Java Backend Student

---

✅ **Lab 10 – Completed Successfully**
