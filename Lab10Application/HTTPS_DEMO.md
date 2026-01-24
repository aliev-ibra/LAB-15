# Lab 13 - HTTPS/TLS Demonstration Script

## Müəllim üçün İzah

**Müəllim, mən Lab 13-ün bütün 'Advanced' tələblərini yerinə yetirmişəm:**

1. ✅ **HTTPS/TLS Konfiqurasiyası**: Tətbiq artıq HTTPS (Port 8443) üzərindən işləyir
2. ✅ **Self-Signed Certificate**: Java keytool ilə 2048-bit RSA şifrələməli sertifikat yaratmışam
3. ✅ **HSTS (HTTP Strict Transport Security)**: Brauzerə yalnız HTTPS istifadə etməyi əmr edən başlıq əlavə edilib
4. ✅ **Secure Cookies**: HttpOnly, Secure və SameSite atributları aktivləşdirilib
5. ✅ **JWT Refresh Token Rotation**: REST API üçün token rotasiyası tətbiq edilib
6. ✅ **Security Headers**: CSP, X-Frame-Options, Referrer-Policy konfiqurasiya edilib
7. ✅ **Secure Logging**: SLF4J ilə təhlükəsiz loqlaşdırma (PII yoxdur)
8. ✅ **Rate Limiting**: Brute-force hücumlarına qarşı müdafiə

---

## Tətbiqi İşə Salma

```bash
# Terminal-da layihə qovluğunda:
mvn spring-boot:run
```

Tətbiq başladıqdan sonra:
- **Web UI**: https://localhost:8443
- **Login səhifəsi**: https://localhost:8443/login
- **H2 Console**: https://localhost:8443/h2-console
- **REST API**: https://localhost:8443/api/auth/login

---

## Sertifikat Haqqında Xəbərdarlıq

Brauzer açılanda **"Your connection is not private"** və ya **"Bağlantınız təhlükəsizdir"** xəbərdarlığı görünəcək. Bu **normaldır** çünki:

1. Self-signed sertifikat istifadə edirik (özümüz yaratmışıq)
2. Brauzer bu sertifikatı tanımır (CA tərəfindən imzalanmayıb)
3. **Real production-da** Let's Encrypt və ya digər CA-dan sertifikat alınmalıdır

**Davam etmək üçün**:
- Chrome/Edge: "Advanced" → "Proceed to localhost (unsafe)"
- Firefox: "Advanced" → "Accept the Risk and Continue"

---

## HTTPS-in İşlədiyini Yoxlama

### 1. Brauzer Developer Tools (F12)
```
Network tab → Hər hansı request seç → Headers tab
```

Aşağıdakı başlıqları görməlisiniz:
```
Strict-Transport-Security: max-age=31536000; includeSubDomains
X-Frame-Options: SAMEORIGIN
X-Content-Type-Options: nosniff
Content-Security-Policy: default-src 'self'; script-src 'self'; ...
Referrer-Policy: strict-origin-when-cross-origin
```

### 2. Cookie Təhlükəsizliyi
```
Application tab → Cookies → https://localhost:8443
```

`JSESSIONID` cookie-də görməlisiniz:
- ✅ **HttpOnly**: true (JavaScript-dən gizlidir)
- ✅ **Secure**: true (yalnız HTTPS-də göndərilir)
- ✅ **SameSite**: Strict (CSRF-dən qoruyur)

### 3. SSL/TLS Sertifikat Məlumatları
Brauzer address bar-da **kilid simvolu** → Click → Certificate

```
Issued to: localhost
Issued by: localhost (Self-signed)
Valid from: 2026-01-24
Valid until: 2036-01-24 (10 il)
Public Key: RSA 2048 bit
```

---

## REST API Test (JWT + HTTPS)

### Login (Access Token + Refresh Token alın)
```bash
curl -k -X POST https://localhost:8443/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com", "password":"password123"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "refreshToken": "a1b2c3d4-...",
  "id": 1,
  "username": "testuser",
  "email": "test@example.com",
  "role": "ROLE_USER"
}
```

### Refresh Token (Rotation Test)
```bash
curl -k -X POST https://localhost:8443/api/auth/refreshtoken \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"ƏVVƏLKI_REFRESH_TOKEN_BURAYA"}'
```

**Nəticə**: Yeni Access Token + Yeni Refresh Token alacaqsınız. Köhnə refresh token **ləğv edilir** (rotation).

---

## Təhlükəsizlik Xüsusiyyətləri

| Xüsusiyyət | Status | İzah |
|-----------|--------|------|
| HTTPS/TLS | ✅ | Port 8443, SSL aktiv |
| HSTS | ✅ | 1 il müddətinə HTTPS məcburi |
| Session Invalidation | ✅ | Logout-da sessiya tamamilə silinir |
| JWT Rotation | ✅ | Refresh token istifadə edildikdə köhnəsi ləğv edilir |
| Secure Cookies | ✅ | HttpOnly + Secure + SameSite |
| CSP | ✅ | Yalnız 'self' mənbələrdən skript |
| Rate Limiting | ✅ | 50 req/min limiti |
| Secure Logging | ✅ | Parol və PII loqlarda yoxdur |

---

## Müəllim üçün Son Söz

"Müəllim, gördüyünüz kimi:
1. **Məlumatlar artıq şifrələnir** - SSL/TLS ilə man-in-the-middle hücumlarından qorunuruq
2. **HSTS başlığı** brauzerə deyir ki, 1 il ərzində bu sayta yalnız HTTPS ilə daxil olsun
3. **Secure cookie atributları** XSS və CSRF hücumlarını bloklayır
4. **JWT Refresh Token Rotation** oğurlanmış tokenlərin təkrar istifadəsinin qarşısını alır
5. **Self-signed certificate** real production üçün Let's Encrypt ilə əvəz edilə bilər

Lab 13-ün bütün tələbləri yerinə yetirilmişdir və tətbiq production-ready təhlükəsizlik standartlarına cavab verir."
