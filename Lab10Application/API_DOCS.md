# API Documentation

## Authentication

### POST /auth/register
Register a new user.

- **Method**: `POST`
- **Path**: `/auth/register`
- **Headers**: `Content-Type: application/json`
- **Body**:
    ```json
    {
        "username": "newuser",
        "email": "new@example.com",
        "password": "Password1!"
    }
    ```
- **Responses**:
    - `201 Created`: Registration successful, returns token.
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
    ```

### POST /auth/login
Login to get a JWT token.

- **Method**: `POST`
- **Path**: `/auth/login`
- **Headers**: `Content-Type: application/json`
- **Body**:
    ```json
    {
        "email": "new@example.com",
        "password": "Password1!"
    }
    ```
- **Responses**:
    - `200 OK`: Login successful, returns token.
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiJ9..."
    }
    ```

## Notes

### GET /notes
Retrieve current user's notes.

- **Method**: `GET`
- **Path**: `/notes`
- **Headers**: `Authorization: Bearer <token>`
- **Responses**:
    - `200 OK`: List of notes.

### POST /notes
Create a new note.

- **Method**: `POST`
- **Path**: `/notes`
- **Headers**: 
    - `Authorization: Bearer <token>`
    - `Content-Type: application/json`
- **Body**:
    ```json
    {
        "title": "My Note",
        "content": "This is a secure note."
    }
    ```
- **Responses**:
    - `201 Created`: The created note.

### GET /notes/{id}
Get a specific note.

- **Method**: `GET`
- **Path**: `/notes/{id}`
- **Headers**: `Authorization: Bearer <token>`
- **Responses**:
    - `200 OK`: The note.
    - `403 Forbidden`: Access denied (not owner).
    - `404 Not Found`: Note not found.

### PUT /notes/{id}
Update a note.

- **Method**: `PUT`
- **Path**: `/notes/{id}`
- **Headers**: 
    - `Authorization: Bearer <token>`
    - `Content-Type: application/json`
- **Body**:
    ```json
    {
        "title": "Updated Title",
        "content": "Updated content"
    }
    ```
- **Responses**:
    - `200 OK`: Updated note.

### DELETE /notes/{id}
Delete a note.

- **Method**: `DELETE`
- **Path**: `/notes/{id}`
- **Headers**: `Authorization: Bearer <token>`
- **Responses**:
    - `204 No Content`: Successful deletion.

## Users

### GET /users
Retrieve all users.

- **Method**: `GET`
- **Path**: `/users`
- **Responses**:
    - `200 OK`: List of users.
    ```json
    [
        {
            "id": 1,
            "username": "user1",
            "email": "user1@example.com",
            "password": "Password1!"
        }
    ]
    ```

### POST /users
Create a new user.

- **Method**: `POST`
- **Path**: `/users`
- **Headers**: `Content-Type: application/json`
- **Body**:
    ```json
    {
        "username": "newuser",
        "email": "new@example.com",
        "password": "Password123!"
    }
    ```
- **Responses**:
    - `201 Created`: The created user.
    - `400 Bad Request`: Validation error.

### PUT /users/{id}
Update an existing user.

- **Method**: `PUT`
- **Path**: `/users/{id}`
- **Headers**: `Content-Type: application/json`
- **Path Variables**: `id` (Long)
- **Body**:
    ```json
    {
        "username": "updateduser",
        "email": "updated@example.com",
        "password": "NewPassword123!"
    }
    ```
- **Responses**:
    - `200 OK`: The updated user.
    - `404 Not Found`: User not found.
    - `400 Bad Request`: Validation error.

### PATCH /users/{id}
Partially update an existing user.

- **Method**: `PATCH`
- **Path**: `/users/{id}`
- **Headers**: `Content-Type: application/json`
- **Path Variables**: `id` (Long)
- **Body**: (Any subset of fields)
    ```json
    {
        "email": "newemail@example.com"
    }
    ```
- **Responses**:
    - `200 OK`: The updated user.
    - `404 Not Found`: User not found.

### DELETE /users/{id}
Delete a user.

- **Method**: `DELETE`
- **Path**: `/users/{id}`
- **Path Variables**: `id` (Long)
- **Responses**:
    - `204 No Content`: Successful deletion.
    - `404 Not Found`: User not found.
