# Lab 13: Security Hardening & Advanced Transport Security

## Overview
This document explains the security improvements and the HSTS configuration implemented in Lab 13.

## Implemented Features

### 1. Session and Token Management
- **Session Invalidation**: Users are fully logged out by invalidating the HttpSession, clearing the SecurityContext, and deleting the `JSESSIONID` cookie.
- **REST API Security (JWT)**:
    - Implemented a separate `SecurityFilterChain` for `/api/**` endpoints.
    - Added **JWT Refresh Token Rotation**:
        - **Access Token**: Short-lived (15 mins).
        - **Refresh Token**: Long-lived (24 hrs).
        - **Rotation**: using a Refresh Token immediately invalidates it and issues a new pair, preventing replay attacks if a refresh token is stolen.
- **Expiration**: Tokens are checked for expiration and removed if invalid.

### 2. Security Headers
The following headers are reinforced in `SecurityConfig`:
- **X-Frame-Options**: `SAMEORIGIN` (Protects against Clickjacking).
- **Content-Security-Policy (CSP)**: Limits script sources to `'self'`.
- **Referrer-Policy**: `STRICT_ORIGIN_WHEN_CROSS_ORIGIN` (Controls user privacy during navigation).
- **X-Content-Type-Options**: Defaults to `nosniff`.

### 3. Secure Cookies
Configured in `application.properties`:
- `HttpOnly`: Prevents JavaScript access to session cookies (mitigates XSS).
- `SameSite`: Set to `Strict` to prevent CSRF.

### 4. Logging & Monitoring
- Replaced `System.out.println` with **SLF4J**.
- Logs do **not** contain PII (Personally Identifiable Information).
- Success/Failure events are logged via `AuthenticationEvents` listener.

### 5. Transport Security (HSTS) - **IMPLEMENTED**
**HTTP Strict Transport Security (HSTS)** is a web security policy mechanism that helps to protect websites against man-in-the-middle attacks such as protocol downgrade attacks and cookie hijacking.

#### Implementation Details:
- **Self-Signed Certificate**: Created using Java's `keytool` utility with 2048-bit RSA encryption, valid for 10 years.
- **HTTPS Port**: Application now runs on port **8443** (standard HTTPS port for development).
- **SSL/TLS Configuration**:
  ```properties
  server.port=8443
  server.ssl.key-store=classpath:keystore.p12
  server.ssl.key-store-type=PKCS12
  server.ssl.enabled=true
  ```

#### HSTS Header Configuration:
```java
.httpStrictTransportSecurity(hsts -> hsts
    .includeSubDomains(true)
    .maxAgeInSeconds(31536000)) // 1 year
```

**How HSTS Works**:
- The server sends the `Strict-Transport-Security` header to the browser.
- The browser remembers (for 1 year in our case) that this site must ONLY be accessed via HTTPS.
- If a user types `http://localhost:8443` or clicks an HTTP link, the browser **automatically upgrades** it to HTTPS **before** sending the request.
- This prevents SSL stripping attacks where an attacker downgrades the connection to HTTP.

**Security Benefits**:
1. **Prevents Protocol Downgrade**: Attackers cannot force users to use insecure HTTP.
2. **Cookie Protection**: Session cookies with `Secure` flag are now fully protected.
3. **Man-in-the-Middle Prevention**: Encrypted communication ensures data integrity.

**Access URLs**:
- Web Application: `https://localhost:8443`
- H2 Console: `https://localhost:8443/h2-console`
- REST API Login: `https://localhost:8443/api/auth/login`

*Note: Browsers will show a security warning for self-signed certificates. This is expected in development. In production, use a certificate from a trusted Certificate Authority (e.g., Let's Encrypt).*

### 6. Rate Limiting
- A simple `RateLimitFilter` (In-Memory) is added to block IPs exceeding 50 requests/minute.
