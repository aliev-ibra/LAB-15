$baseUrl = "http://localhost:8081/users"

Write-Host "--- 1. GET /users (Initial) ---"
Invoke-RestMethod -Uri $baseUrl -Method Get -ErrorAction SilentlyContinue

Write-Host "`n--- 2. POST /users (Create Valid User) ---"
$randomInt = Get-Random -Minimum 1000 -Maximum 9999
$email = "test${randomInt}@example.com"
$body = @{
    username = "testuser"
    email = $email
    password = "Password1!"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method Post -Body $body -ContentType "application/json"
    Write-Host "Response: $($response | ConvertTo-Json -Depth 2)"
    $userId = $response.id
} catch {
    Write-Host "Error: $_"
    $_.Exception.Response.GetResponseStream() | %{ $_.ReadToEnd() }
}

Write-Host "`n--- 3. POST /users (Create Invalid User) ---"
$invalidBody = @{
    username = "ab" # too short
    email = "not-an-email"
    password = "pass" # too short, no complexity
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri $baseUrl -Method Post -Body $invalidBody -ContentType "application/json"
} catch {
    Write-Host "Caught expected error: $($_.Exception.Message)"
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $errorResponse = $reader.ReadToEnd()
    Write-Host "Error Body: $errorResponse"
}

Write-Host "`n--- 4. GET /users (After Create) ---"
Invoke-RestMethod -Uri $baseUrl -Method Get

if ($userId) {
    Write-Host "`n--- 5. PUT /users/$userId (Update User) ---"
    $updateBody = @{
        username = "updateduser"
        email = "updated@example.com"
        password = "NewPassword1!"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/$userId" -Method Put -Body $updateBody -ContentType "application/json"
    Write-Host "Response: $($response | ConvertTo-Json -Depth 2)"

    Write-Host "`n--- 6. PATCH /users/$userId (Patch User) ---"
    $patchBody = @{
        email = "patched@example.com"
    } | ConvertTo-Json

    $response = Invoke-RestMethod -Uri "$baseUrl/$userId" -Method Patch -Body $patchBody -ContentType "application/json"
    Write-Host "Response: $($response | ConvertTo-Json -Depth 2)"

    Write-Host "`n--- 7. DELETE /users/$userId (Delete User) ---"
    Invoke-RestMethod -Uri "$baseUrl/$userId" -Method Delete
    Write-Host "Deleted user $userId"

    Write-Host "`n--- 8. GET /users/$userId (Verify Delete) ---"
    try {
        Invoke-RestMethod -Uri "$baseUrl/$userId" -Method Get
    } catch {
        Write-Host "Caught expected error (User not found): $($_.Exception.Message)"
    }
}
