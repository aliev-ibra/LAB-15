# Secure App Server Project - Demonstration Notes

This document provides a summary of the security implementation details for the student demonstration.

## 1. HTTP Routes & Methods
The following HTTP routes and methods have been implemented in the application:

| Controller | Method | Route | Description | Auth Required |
|------------|--------|-------|-------------|---------------|
| `AuthController` | `GET` | `/login` | Displays the login page. | No |
| `AuthController` | `GET` | `/register` | Displays the registration page. | No |
| `AuthController` | `POST` | `/register` | Processes new user registration. | No |
| `NoteController` | `GET` | `/dashboard` | Displays the user's dashboard and notes. | **Yes** |
| `NoteController` | `POST` | `/dashboard` | Creates a new note for the logged-in user. | **Yes** |
| `NoteController` | `POST` | `/dashboard/delete/{id}` | Deletes a specific note by ID. | **Yes** |
| `Security` | `POST` | `/login` | Processes authentication credentials (Spring Security). | No |
| `Security` | `POST` | `/logout` | Invalidates session and logs user out. | **Yes** |

## 2. Authentication Type
The application uses **Session-based Authentication** (Server-side sessions).

*   **Mechanism**: Spring Security's `DaoAuthenticationProvider`.
*   **Token**: `JSESSIONID` Cookie.
*   **Storage**: The session ID is stored in the browser's cookies and referenced on the server to identify the authenticated context `SecurityContextHolder`.

## 3. Authentication Demonstration (Success)
**Scenario**: A valid user logs in successfully.

**Request:**
```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=demonstrator&password=securepass
```

**Response:**
```http
HTTP/1.1 302 Found
Location: http://localhost:8080/dashboard
Set-Cookie: JSESSIONID=FA8B0C990D42C9AD7F037D2F8340E1D; Path=/; HttpOnly
```
*   **Explanation**: The server validates credentials, creates a session, sets the `JSESSIONID` cookie, and redirects (`302`) the user to the protected `/dashboard` route.

## 4. Error Case Demonstration (Failure)
**Scenario**: A user attempts to login with invalid credentials.

**Request:**
```http
POST /login HTTP/1.1
Content-Type: application/x-www-form-urlencoded

username=demonstrator&password=wrongpass
```

**Response:**
```http
HTTP/1.1 302 Found
Location: http://localhost:8080/login?error
```
*   **Explanation**: The server rejects the credentials and redirects the user back to the login page with an error parameter (`?error`), which triggers an error message display on the frontend.
