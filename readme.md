Secure Web Application - Complete Lab Implementation (Lab 10, 11-12)
Show Image
Show Image
Show Image
Show Image

Academic Project: A comprehensive implementation of HTTP protocol, Spring Security authentication/authorization, and secure CRUD operations demonstrating enterprise-grade security practices for university coursework.


📚 Table of Contents

Project Overview
Architecture
Lab Requirements Coverage
Security Features
Technology Stack
Getting Started
API Documentation
Database Schema
Security Implementation
Testing Guide
Presentation Guide


🎯 Project Overview
This project is a secure note-taking application that demonstrates:

✅ HTTP Protocol Implementation (Lab 10)
✅ Spring Security Authentication (Lab 11)
✅ Authorization & Access Control (Lab 12)
✅ Complete CRUD Operations with security enforcement
✅ Input Validation & Error Handling
✅ SQL Injection Prevention
✅ Password Security with BCrypt

Why This Project?
This application showcases real-world security scenarios where:

Users register and login securely
Each user can only access their own notes
Passwords are encrypted before storage
All inputs are validated
Database queries use prepared statements
Access control prevents horizontal privilege escalation


🏗️ Architecture
Layered Architecture Pattern
┌──────────────────────────────────────────────────────────┐
│                    HTTP Layer                             │
│  Controllers (@RestController / @Controller)              │
│  - Handles HTTP requests/responses                        │
│  - Maps DTOs, validates input                             │
│  - Returns proper status codes                            │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                  Service Layer                            │
│  - Business logic implementation                          │
│  - Access control enforcement (USER ID checks)            │
│  - Transaction management                                 │
│  - Authentication verification                            │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                Repository Layer                           │
│  - Data access with JdbcTemplate / JPA                    │
│  - Prepared statements (SQL injection prevention)         │
│  - Database operations                                    │
└───────────────────────┬──────────────────────────────────┘
                        │
┌───────────────────────▼──────────────────────────────────┐
│                  Model Layer                              │
│  - Entities: User, Note                                   │
│  - DTOs: RegisterRequest, NoteRequest                     │
│  - Validation constraints                                 │
└───────────────────────┬──────────────────────────────────┘
                        │
                ┌───────▼────────┐
                │   Database     │
                │  (H2 / SQLite) │
                └────────────────┘
Key Design Principles
PrincipleImplementationSeparation of ConcernsEach layer has single responsibilityDependency InjectionSpring manages all beans and dependenciesSecurity by DefaultAll routes protected unless explicitly publicFail-Safe DefaultsDeny access unless explicitly grantedDefense in DepthMultiple validation layers (DTO → Service → DB)

📋 Lab Requirements Coverage
✅ Lab 10: HTTP Implementation
RequirementImplementationLocationHTTP routingGET, POST, PUT, DELETE endpointscontroller/Request headers@RequestHeader for authorizationAuthController.javaJSON parsing@RequestBody with JacksonAll controllersInput validation@Valid with Bean ValidationDTOs in model/dto/Custom validatorPassword strength validatorPasswordValidator.javaStatus codes200, 201, 400, 401, 403, 404All controllersError handling@ControllerAdvice global handlerGlobalExceptionHandler.java
✅ Lab 11: Authentication
RequirementImplementationLocationUserDetailsServiceCustom implementation loading users from DBCustomUserDetailsService.javaSecurityFilterChainPublic: /login, /register; Protected: all othersSecurityConfig.javaLogin/LogoutSpring Security session handlingBuilt-in + custom configCSRF ProtectionEnabled for all POST/PUT/DELETESecurityConfig.javaPassword hashingBCrypt with strength=12PasswordEncoder beanSession timeoutConfigured in application.properties30 minutes default
✅ Lab 12: Authorization & Access Control
RequirementImplementationLocationNew EntityNote entity with CRUDmodel/Note.javaFlyway migrationV2__create_notes.sqlresources/db/migration/RepositoryJdbcTemplate with prepared statementsNoteRepository.javaAccess controlUser can only access own notesNoteService.javaForeign keyuser_id references users(id)Database schemaValidationDTO validation for all inputsNoteRequest.javaSQL injection preventionPrepared statements for all queriesJdbcTemplate usage

🔐 Security Features
1. Authentication Flow
┌─────────┐                                     ┌──────────────┐
│ Browser │                                     │   Server     │
└────┬────┘                                     └──────┬───────┘
     │                                                 │
     │  POST /register                                 │
     │  {email, password}                              │
     ├────────────────────────────────────────────────>│
     │                                                 │
     │                                      Validate input
     │                                      Hash password (BCrypt)
     │                                      Save to database
     │                                                 │
     │  200 OK                                         │
     │<────────────────────────────────────────────────┤
     │                                                 │
     │  POST /login                                    │
     │  {email, password}                              │
     ├────────────────────────────────────────────────>│
     │                                                 │
     │                              Load user from database
     │                              Compare hashed passwords
     │                              Create session (JSESSIONID)
     │                                                 │
     │  302 Redirect to /notes                         │
     │  Set-Cookie: JSESSIONID=...                     │
     │<────────────────────────────────────────────────┤
2. Access Control Implementation
Critical Security Check (executed on every request):
javapublic Note getNoteById(Long noteId, String userEmail) {
    // Step 1: Fetch note from database
    Note note = noteRepository.findById(noteId);
    
    // Step 2: Get current authenticated user
    User currentUser = userRepository.findByEmail(userEmail);
    
    // Step 3: SECURITY CHECK - Does this note belong to current user?
    if (!note.getUserId().equals(currentUser.getId())) {
        // Attacker trying to access someone else's data
        throw new AccessDeniedException("Unauthorized access to note");
    }
    
    // Step 4: User owns this note, return it
    return note;
}
What This Prevents:
AttackHow It's BlockedHorizontal Privilege EscalationUser A cannot access User B's notes even if they know the note IDDirect Object ReferenceChanging /notes/5 to /notes/6 in URL fails if note 6 belongs to another userSession Hijacking ImpactEven with stolen session, attacker only sees victim's own data
3. Password Security
java// Registration - Password is hashed BEFORE storage
@Service
public class UserService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    
    public void registerUser(RegisterRequest request) {
        // Validate password strength
        validatePasswordStrength(request.getPassword()); // Min 8 chars, complexity rules
        
        // Hash password with BCrypt (strength: 12 rounds)
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Store ONLY the hash in database
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword); // Never store plain text!
        
        userRepository.save(user);
    }
}
Database Storage:
sql-- BEFORE hashing (NEVER DONE)
INSERT INTO users (email, password) VALUES 
('user@example.com', 'MyPassword123'); -- ❌ DANGER

-- AFTER hashing (CORRECT)
INSERT INTO users (email, password) VALUES 
('user@example.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/'); -- ✅ SAFE
4. SQL Injection Prevention
Vulnerable Code (What We DON'T Do):
java// ❌ DANGER: String concatenation allows injection
public Note findById(Long id) {
    String sql = "SELECT * FROM notes WHERE id = " + id;
    // Attacker sends: id = "1 OR 1=1" → Returns ALL notes!
}
Secure Implementation (What We DO):
java// ✅ SAFE: Prepared statements treat input as data, not code
public Note findById(Long id) {
    String sql = "SELECT * FROM notes WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, new Object[]{id}, noteMapper);
    // Input is escaped automatically → Injection impossible
}
5. Input Validation
java// DTO with Bean Validation
public class RegisterRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
             message = "Password must contain uppercase, lowercase, and number")
    private String password;
}
```

**Validation Flow:**
```
HTTP Request
    ↓
Controller (@Valid annotation)
    ↓
Bean Validation (JSR-380)
    ↓
Custom Validators
    ↓
✅ Valid → Continue to Service
❌ Invalid → Return 400 Bad Request with error details

💻 Technology Stack
CategoryTechnologyPurposeFrameworkSpring Boot 3.2+Application frameworkSecuritySpring Security 6+Authentication & authorizationDatabaseH2 / SQLiteData persistence (in-memory/file)MigrationFlywayVersion-controlled schema changesValidationHibernate ValidatorBean Validation (JSR-380)Template EngineThymeleaf (optional)Server-side rendering for MVCBuild ToolMaven / GradleDependency managementPassword HashingBCryptSecure password storageData AccessJdbcTemplate / JPADatabase operations

🚀 Getting Started
Prerequisites
bash# Check Java version
java -version  # Should be 17+

# Check Maven
mvn -version   # Should be 3.8+
Installation
Step 1: Clone Repository
bashgit clone https://github.com/yourusername/secure-web-app.git
cd secure-web-app
Step 2: Configure Environment Variables
Create .env file in project root:
properties# Database Configuration (SQLite example)
DB_URL=jdbc:sqlite:database.db
DB_USERNAME=
DB_PASSWORD=

# H2 Configuration (alternative)
# DB_URL=jdbc:h2:mem:testdb
# DB_USERNAME=sa
# DB_PASSWORD=

# Session Configuration
SESSION_TIMEOUT=1800
Create .env.example (for documentation):
propertiesDB_URL=
DB_USERNAME=
DB_PASSWORD=
SESSION_TIMEOUT=
Step 3: Update application.properties
properties# Import environment variables
spring.config.import=optional:file:.env[.properties]

# Database
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.sqlite.JDBC

# JPA/Hibernate
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=validate

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Session
server.servlet.session.timeout=${SESSION_TIMEOUT:30m}

# H2 Console (development only)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
Step 4: Build & Run
bash# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Application starts at http://localhost:8080
Quick Verification
bash# Test server is running
curl http://localhost:8080/login

# Should return login page HTML or redirect

📡 API Documentation
Public Endpoints (No Authentication Required)
1. User Registration
httpPOST /register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePass123"
}
Success Response (201 Created):
json{
  "message": "User registered successfully",
  "userId": 1
}
Error Response (400 Bad Request):
json{
  "timestamp": "2026-01-22T10:30:00",
  "status": 400,
  "errors": [
    "Invalid email format",
    "Password must be at least 8 characters"
  ]
}
2. User Login
httpPOST /login
Content-Type: application/x-www-form-urlencoded

username=user@example.com&password=SecurePass123
Success: Redirects to /notes with session cookie
Error: Redirects to /login?error

Protected Endpoints (Authentication Required)

Note: All requests must include JSESSIONID cookie from login

3. Get All User's Notes
httpGET /notes
Cookie: JSESSIONID=ABC123...
Success Response (200 OK):
json[
  {
    "id": 1,
    "userId": 5,
    "title": "My First Note",
    "content": "This is the note content",
    "createdAt": "2026-01-22T09:00:00",
    "updatedAt": "2026-01-22T09:00:00"
  },
  {
    "id": 2,
    "userId": 5,
    "title": "Another Note",
    "content": "More content here",
    "createdAt": "2026-01-22T10:00:00",
    "updatedAt": "2026-01-22T10:00:00"
  }
]
4. Get Single Note
httpGET /notes/1
Cookie: JSESSIONID=ABC123...
Success Response (200 OK):
json{
  "id": 1,
  "userId": 5,
  "title": "My First Note",
  "content": "This is the note content",
  "createdAt": "2026-01-22T09:00:00",
  "updatedAt": "2026-01-22T09:00:00"
}
Error Responses:
http403 Forbidden - Note belongs to another user
404 Not Found - Note does not exist
401 Unauthorized - Not logged in
5. Create Note
httpPOST /notes
Content-Type: application/json
Cookie: JSESSIONID=ABC123...

{
  "title": "New Note",
  "content": "Note content goes here"
}
Success Response (201 Created):
json{
  "id": 3,
  "userId": 5,
  "title": "New Note",
  "content": "Note content goes here",
  "createdAt": "2026-01-22T11:00:00",
  "updatedAt": "2026-01-22T11:00:00"
}
Validation Error (400 Bad Request):
json{
  "timestamp": "2026-01-22T11:00:00",
  "status": 400,
  "errors": [
    "Title is required",
    "Title must be between 1 and 255 characters"
  ]
}
6. Update Note
httpPUT /notes/1
Content-Type: application/json
Cookie: JSESSIONID=ABC123...

{
  "title": "Updated Title",
  "content": "Updated content"
}
Success Response (200 OK): Returns updated note
7. Delete Note
httpDELETE /notes/1
Cookie: JSESSIONID=ABC123...
Success Response (204 No Content): Empty body

HTTP Status Codes Used
CodeMeaningWhen It's Returned200 OKSuccessGET, PUT successful201 CreatedResource createdPOST successful204 No ContentSuccess, no bodyDELETE successful400 Bad RequestInvalid inputValidation failed401 UnauthorizedNot authenticatedMissing/invalid session403 ForbiddenNo permissionAccessing another user's resource404 Not FoundResource not existsInvalid note ID500 Internal Server ErrorServer errorUnexpected exception

🗄️ Database Schema
Flyway Migrations
V1__create_users.sql:
sqlCREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(60) NOT NULL,  -- BCrypt hash length
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index for faster login queries
CREATE INDEX idx_users_email ON users(email);
V2__create_notes.sql:
sqlCREATE TABLE IF NOT EXISTS notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key with cascade delete
    CONSTRAINT fk_notes_user 
        FOREIGN KEY (user_id) 
        REFERENCES users(id) 
        ON DELETE CASCADE
);

-- Index for access control queries
CREATE INDEX idx_notes_user_id ON notes(user_id);
```

### Entity Relationships
```
┌─────────────────┐
│     users       │
├─────────────────┤
│ id (PK)         │◄──────┐
│ email (UNIQUE)  │       │
│ password        │       │ ONE-TO-MANY
│ created_at      │       │ (One user has many notes)
│ updated_at      │       │
└─────────────────┘       │
                          │
                          │
┌─────────────────┐       │
│     notes       │       │
├─────────────────┤       │
│ id (PK)         │       │
│ user_id (FK)    │───────┘
│ title           │
│ content         │
│ created_at      │
│ updated_at      │
└─────────────────┘

🛡️ Security Implementation
SecurityConfig.java
java@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/register", "/login", "/h2-console/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/notes", true)
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**") // Dev only
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1) // One session per user
            );
        
        // Allow H2 console in frames (dev only)
        http.headers(headers -> headers.frameOptions().disable());
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 rounds for security/performance balance
    }
}
CustomUserDetailsService.java
java@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword()) // Already hashed
            .authorities("ROLE_USER") // All users get USER role
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(false)
            .build();
    }
}
NoteService.java (Access Control)
java@Service
public class NoteService {
    
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    
    /**
     * Get all notes belonging to the authenticated user
     */
    public List<Note> getUserNotes(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Only return notes owned by this user
        return noteRepository.findByUserId(user.getId());
    }
    
    /**
     * Get single note with ownership verification
     */
    public Note getNoteById(Long noteId, String userEmail) {
        Note note = noteRepository.findById(noteId)
            .orElseThrow(() -> new ResourceNotFoundException("Note not found"));
        
        User currentUser = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // CRITICAL SECURITY CHECK
        if (!note.getUserId().equals(currentUser.getId())) {
            // Return 403 or 404 to prevent info disclosure
            throw new AccessDeniedException("You do not have permission to access this note");
        }
        
        return note;
    }
    
    /**
     * Create note tied to authenticated user
     */
    @Transactional
    public Note createNote(NoteRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Note note = new Note();
        note.setUserId(user.getId()); // Bind to current user
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        
        return noteRepository.save(note);
    }
    
    /**
     * Update note with ownership verification
     */
    @Transactional
    public Note updateNote(Long noteId, NoteRequest request, String userEmail) {
        // Verify ownership first
        Note existingNote = getNoteById(noteId, userEmail);
        
        existingNote.setTitle(request.getTitle());
        existingNote.setContent(request.getContent());
        existingNote.setUpdatedAt(LocalDateTime.now());
        
        return noteRepository.save(existingNote);
    }
    
    /**
     * Delete note with ownership verification
     */
    @Transactional
    public void deleteNote(Long noteId, String userEmail) {
        // Verify ownership first
        Note note = getNoteById(noteId, userEmail);
        
        noteRepository.delete(note.getId());
    }
}

🧪 Testing Guide
Manual Testing with cURL
1. Register New User:
bashcurl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "SecurePass123"
  }'
2. Login (Save Session Cookie):
bashcurl -X POST http://localhost:8080/login \
  -d "username=test@example.com&password=SecurePass123" \
  -c cookies.txt \
  -L
3. Create Note (Using Session):
bashcurl -X POST http://localhost:8080/notes \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "title": "Test Note",
    "content": "This is a test note"
  }'
4. Get All Notes:
bashcurl http://localhost:8080/notes \
  -b cookies.txt
5. Test Access Control (Should Fail):
bash# Login as user A, create note (ID = 1)
# Login as user B, try to access note 1

curl http://localhost:8080/notes/1 \
  -b cookies_user_b.txt

# Expected: 403 Forbidden
Database Verification (H2 Console)

Navigate to: http://localhost:8080/h2-console
Enter connection details:

JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (empty)


Click "Connect"

Verify Password Hashing:
sqlSELECT email, password FROM users;

-- Output should show:
-- test@example.com | $2a$12$KVXL... (BCrypt hash, NOT plain text)
Verify Note Ownership:
sqlSELECT n.id, n.title, n.user_id, u.email
FROM notes n
JOIN users u ON n.user_id = u.id;

-- Verify user_id matches correct user

🎤 Presentation Guide
Presentation Structure (15-20 minutes)
1. Introduction (2 minutes)
What to Say:

"Salam, bu gün sizə təhlükəsiz veb tətbiq nümayiş etdirəcəyəm. Bu layihə Lab 10, 11 və 12-nin bütün tələblərini əhatə edir: HTTP protokolu, Spring Security ilə autentifikasiya, və access control mexanizmi."

What to Show:

Open GitHub repository
Show professional README file
Highlight project structure


2. Architecture & Technology Stack (3 minutes)
What to Say:

"Layihə Layered Architecture prinsipinə əsaslanır:

Controller layer: HTTP sorğularını idarə edir
Service layer: biznes məntiq və təhlükəsizlik yoxlamaları
Repository layer: verilənlər bazası ilə prepared statements
Model layer: Entity və DTO-lar validasiya ilə"


What to Show:

Display architecture diagram
Show folder structure in IDE
Point out controller/, service/, repository/, model/ packages


3. Registration & Validation (Lab 10) (3 minutes)
What to Say:

"Əvvəlcə istifadəçi qeydiyyatını göstərirəm. Burada DTO səviyyəsində validasiya tətbiq olunur."

What to Demonstrate:
java// Show RegisterRequest.java
@Email(message = "Invalid email format")
private String email;

@Size(min = 8, message = "Password must be at least 8 characters")
private String password;
Live Demo:

Open Postman/cURL
Send valid registration:

json   POST /register
   {
     "email": "demo@example.com",
     "password": "SecurePass123"
   }
✅ Show 201 Created response

Send invalid registration:

json   {
     "email": "invalid-email",
     "password": "123"
   }
❌ Show 400 Bad Request with error details
Key Point to Emphasize:

"Sistemdə @Valid annotasiyası və Bean Validation istifadə edilir. Xətalı məlumat göndərsək, 400 status kodu qaytarır."


4. Security & Password Encryption (Lab 11) (4 minutes)
What to Say:

"İndi ən vacib hissə: şifrələrin təhlükəsizliyi. Heç bir sistem şifrəni açıq şəkildə saxlamamalıdır."

What to Show:
Step 1: Code Explanation
java// Show SecurityConfig.java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // 12 rounds
}

// Show UserService.java
String hashedPassword = passwordEncoder.encode(plainPassword);
user.setPassword(hashedPassword); // Only hash is stored
