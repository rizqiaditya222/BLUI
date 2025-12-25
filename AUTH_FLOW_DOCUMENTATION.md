# Dokumentasi Alur Data Authentication - Aplikasi Blui

## Daftar Isi
1. [Overview](#overview)
2. [Arsitektur Authentication](#arsitektur-authentication)
3. [Komponen-Komponen Utama](#komponen-komponen-utama)
4. [Alur Login](#alur-login)
5. [Alur Register](#alur-register)
6. [Alur Logout](#alur-logout)
7. [Token Management](#token-management)
8. [Protected Routes](#protected-routes)
9. [Error Handling](#error-handling)

---

## Overview

Aplikasi Blui menggunakan **JWT (JSON Web Token)** untuk sistem authentication. Sistem ini dibangun dengan arsitektur **Clean Architecture** yang memisahkan concern antara:
- **Presentation Layer** (UI & ViewModel)
- **Data Layer** (Repository & API)
- **Domain Layer** (Model & Use Case)

Base URL API: `https://blui.elginbrian.com/api/v1/`

---

## Arsitektur Authentication

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  ┌──────────────┐         ┌──────────────┐                  │
│  │ LoginScreen  │────────▶│ LoginViewModel│                  │
│  └──────────────┘         └───────┬──────┘                  │
│  ┌──────────────┐         ┌───────┴──────┐                  │
│  │RegisterScreen│────────▶│RegisterViewModel│                │
│  └──────────────┘         └───────┬──────┘                  │
└────────────────────────────────────┼──────────────────────────┘
                                     │
                                     ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATA LAYER                              │
│                  ┌───────────────┐                           │
│                  │AuthRepository │                           │
│                  └───────┬───────┘                           │
│                          │                                   │
│        ┌─────────────────┼─────────────────┐                │
│        ▼                 ▼                 ▼                 │
│  ┌──────────┐    ┌──────────┐      ┌────────────┐          │
│  │ApiService│    │ApiConfig │      │TokenManager│          │
│  └────┬─────┘    └────┬─────┘      └─────┬──────┘          │
│       │               │                   │                 │
│       │         ┌─────▼─────┐            │                 │
│       │         │OkHttpClient│            │                 │
│       │         └─────┬─────┘            │                 │
│       │               │                   │                 │
│       │      ┌────────▼────────┐         │                 │
│       │      │AuthInterceptor  │◀────────┘                 │
│       │      └─────────────────┘                           │
└───────┼──────────────────────────────────────────────────────┘
        │
        ▼
┌─────────────────────────────────────────────────────────────┐
│                    BACKEND API                               │
│  POST /auth/login       POST /auth/register                  │
│  GET  /user/profile     PUT  /user/profile                   │
└─────────────────────────────────────────────────────────────┘
```

---

## Komponen-Komponen Utama

### 1. TokenManager
**Location:** `data/api/TokenManager.kt`

**Fungsi:** Mengelola penyimpanan dan pengambilan token JWT menggunakan SharedPreferences.

**Key Methods:**
```kotlin
- saveToken(token: String)              // Simpan JWT token
- getToken(): String?                   // Ambil JWT token
- saveUserData(id, name, email, dob)    // Simpan data user
- getUserId(): String?                  // Ambil user ID
- getUserName(): String?                // Ambil nama user
- getUserEmail(): String?               // Ambil email user
- getUserDateOfBirth(): String?         // Ambil tanggal lahir
- isLoggedIn(): Boolean                 // Cek status login
- clearToken()                          // Hapus token (logout)
- clearAll()                            // Hapus semua data
```

**SharedPreferences Keys:**
- `PREFS_NAME`: "blui_prefs"
- `KEY_TOKEN`: "auth_token"
- `KEY_USER_ID`: "user_id"
- `KEY_USER_NAME`: "user_name"
- `KEY_USER_EMAIL`: "user_email"
- `KEY_USER_DOB`: "user_dob"
- `KEY_IS_LOGGED_IN`: "is_logged_in"

### 2. AuthInterceptor
**Location:** `data/api/AuthInterceptor.kt`

**Fungsi:** Interceptor OkHttp yang otomatis menambahkan JWT token ke setiap HTTP request.

**Cara Kerja:**
1. Mengambil token dari TokenManager
2. Jika token ada, tambahkan header: `Authorization: Bearer {token}`
3. Jika token tidak ada (untuk endpoint login/register), lanjutkan tanpa header

```kotlin
override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()
    val token = tokenManager.getToken()
    
    val newRequest = if (token != null) {
        originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
    } else {
        originalRequest
    }
    
    return chain.proceed(newRequest)
}
```

### 3. ApiConfig
**Location:** `data/api/ApiConfig.kt`

**Fungsi:** Konfigurasi Retrofit dan OkHttpClient.

**Features:**
- Base URL: `https://blui.elginbrian.com/api/v1/`
- Timeout: 30 detik (connect, read, write)
- Logging Interceptor untuk debugging
- Auth Interceptor untuk JWT token
- Gson Converter untuk JSON parsing

**Method:**
```kotlin
fun getApiService(context: Context): ApiService
```

### 4. ApiService
**Location:** `data/api/ApiService.kt`

**Authentication Endpoints:**
```kotlin
@POST("auth/register")
suspend fun register(@Body request: RegisterRequest): AuthResponse

@POST("auth/login")
suspend fun login(@Body request: LoginRequest): AuthResponse

@GET("user/profile")
suspend fun getProfile(): UserResponse

@PUT("user/profile")
suspend fun updateProfile(@Body request: UpdateProfileRequest): UserResponse
```

### 5. AuthRepository
**Location:** `data/repository/AuthRepository.kt`

**Fungsi:** Layer antara ViewModel dan API Service. Menangani business logic terkait authentication.

**Key Methods:**
```kotlin
suspend fun register(fullName, email, password, dateOfBirth): Result<AuthResponse>
suspend fun login(email, password): Result<AuthResponse>
suspend fun getProfile(): Result<UserResponse>
suspend fun updateProfile(...): Result<UserResponse>
fun logout()
fun isLoggedIn(): Boolean
```

### 6. LoginViewModel
**Location:** `presentation/auth/login/LoginViewModel.kt`

**Fungsi:** Mengelola state dan logic untuk LoginScreen.

**UI State:**
```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
```

**Methods:**
- `onEmailChange(email: String)`
- `onPasswordChange(password: String)`
- `login()` - Validasi input dan panggil repository
- `validateInput()` - Validasi email dan password
- `clearError()` - Clear error message

### 7. LoginScreen
**Location:** `presentation/auth/login/LoginScreen.kt`

**Fungsi:** UI untuk halaman login.

**Components:**
- Email input field
- Password input field (dengan show/hide)
- Login button dengan loading state
- Link ke register screen
- Snackbar untuk error message
- LaunchedEffect untuk navigate setelah sukses

---

## Alur Login

### Flow Diagram
```
┌──────────────┐
│ User Input   │
│ Email & Pass │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ LoginScreen      │
│ - User click     │
│   Login button   │
└──────┬───────────┘
       │
       ▼
┌──────────────────────────┐
│ LoginViewModel           │
│ 1. Validate input        │
│ 2. Set isLoading = true  │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ AuthRepository           │
│ - Create LoginRequest    │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ ApiService               │
│ POST /auth/login         │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Backend API              │
│ - Validate credentials   │
│ - Generate JWT token     │
│ - Return user data       │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ AuthRepository           │
│ - Save token             │
│ - Save user ID           │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ LoginViewModel           │
│ - Set isSuccess = true   │
│ - Set isLoading = false  │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ LoginScreen              │
│ - LaunchedEffect detects │
│   isSuccess = true       │
│ - Navigate to MainScreen │
└──────────────────────────┘
```

### Step-by-Step Detail

#### 1. User Input
User memasukkan email dan password di LoginScreen.

#### 2. Click Login Button
```kotlin
PrimaryButton(
    text = "Masuk",
    onClick = { viewModel.login() },
    isLoading = uiState.isLoading
)
```

#### 3. LoginViewModel - Validate Input
```kotlin
fun login() {
    val email = _uiState.value.email.trim()
    val password = _uiState.value.password
    
    if (!validateInput(email, password)) {
        return
    }
    
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true)
        val result = authRepository.login(email, password)
        // ...
    }
}

private fun validateInput(email: String, password: String): Boolean {
    return when {
        email.isEmpty() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email tidak boleh kosong"
            )
            false
        }
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Format email tidak valid"
            )
            false
        }
        password.isEmpty() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password tidak boleh kosong"
            )
            false
        }
        password.length < 6 -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password minimal 6 karakter"
            )
            false
        }
        else -> true
    }
}
```

#### 4. AuthRepository - Call API
```kotlin
suspend fun login(email: String, password: String): Result<AuthResponse> {
    return withContext(Dispatchers.IO) {
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            
            // Simpan token dan userId setelah login sukses
            tokenManager.saveToken(response.token)
            tokenManager.saveUserId(response.user.id)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### 5. API Request
**Request:**
```
POST https://blui.elginbrian.com/api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user_123",
    "fullName": "John Doe",
    "email": "user@example.com",
    "dateOfBirth": "1990-01-01",
    "photoUrl": null
  }
}
```

#### 6. Save Token & User Data
```kotlin
tokenManager.saveToken(response.token)
tokenManager.saveUserId(response.user.id)
```

Data disimpan ke SharedPreferences:
- `auth_token`: JWT token
- `user_id`: User ID
- `is_logged_in`: true

#### 7. Update UI State
```kotlin
result.onSuccess {
    _uiState.value = _uiState.value.copy(
        isLoading = false,
        isSuccess = true,
        errorMessage = null
    )
}
```

#### 8. Navigate to Main Screen
```kotlin
// Di LoginScreen
LaunchedEffect(uiState.isSuccess) {
    if (uiState.isSuccess) {
        onNavigateToMain()
    }
}
```

---

## Alur Register

### Flow Diagram
```
┌──────────────────┐
│ User Input       │
│ Name, Email,     │
│ Password, DOB    │
└──────┬───────────┘
       │
       ▼
┌──────────────────────────┐
│ RegisterScreen           │
│ - User click Register    │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ RegisterViewModel        │
│ 1. Validate all inputs   │
│ 2. Set isLoading = true  │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ AuthRepository           │
│ - Create RegisterRequest │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ ApiService               │
│ POST /auth/register      │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Backend API              │
│ - Create new user        │
│ - Generate JWT token     │
│ - Return user data       │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ AuthRepository           │
│ - Save token             │
│ - Save user ID           │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ RegisterViewModel        │
│ - Set isSuccess = true   │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ RegisterScreen           │
│ - Navigate to MainScreen │
└──────────────────────────┘
```

### Step-by-Step Detail

#### 1. User Input Fields
- Full Name
- Email
- Date of Birth
- Password
- Confirm Password

#### 2. Validation Rules
```kotlin
- Full name: tidak boleh kosong
- Email: format valid & tidak boleh kosong
- Password: minimal 6 karakter
- Confirm Password: harus sama dengan password
- Date of Birth: tidak boleh kosong
```

#### 3. API Request
**Request:**
```
POST https://blui.elginbrian.com/api/v1/auth/register
Content-Type: application/json

{
  "full_name": "John Doe",
  "email": "user@example.com",
  "password": "password123",
  "date_of_birth": "1990-01-01",
  "photo_url": null
}
```

**Response (Success):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "user_123",
    "fullName": "John Doe",
    "email": "user@example.com",
    "dateOfBirth": "1990-01-01",
    "photoUrl": null
  }
}
```

#### 4. Save Token & Navigate
Sama seperti login, token dan user ID disimpan, kemudian navigate ke MainScreen.

---

## Alur Logout

### Flow Diagram
```
┌──────────────────┐
│ ProfileScreen    │
│ - User click     │
│   Logout button  │
└──────┬───────────┘
       │
       ▼
┌──────────────────────────┐
│ ProfileViewModel         │
│ - Call logout()          │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ AuthRepository           │
│ - Call tokenManager      │
│   .clearToken()          │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ TokenManager             │
│ - Clear SharedPrefs      │
│ - Remove token           │
│ - Remove user data       │
│ - Set isLoggedIn = false │
└──────┬───────────────────┘
       │
       ▼
┌──────────────────────────┐
│ Navigate to LoginScreen  │
│ - Clear back stack       │
└──────────────────────────┘
```

### Implementation
```kotlin
// Di ProfileScreen
Button(onClick = onLogout) {
    Text("Logout")
}

// Di Navigation.kt - MainScreen
MainScreen(
    onNavigateToLogin = {
        navController.navigate(Screen.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }
)

// Di AuthRepository
fun logout() {
    tokenManager.clearToken()
}

// Di TokenManager
fun clearAll() {
    prefs.edit().clear().apply()
}
```

---

## Token Management

### JWT Token Storage
Token disimpan di **SharedPreferences** dengan key `auth_token`.

```kotlin
// Simpan token
tokenManager.saveToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")

// Ambil token
val token = tokenManager.getToken() // Returns: String?

// Cek login status
val isLoggedIn = tokenManager.isLoggedIn() // Returns: Boolean
```

### Automatic Token Injection
Token otomatis ditambahkan ke setiap HTTP request melalui **AuthInterceptor**.

```kotlin
// Tidak perlu manual add header
// ❌ TIDAK PERLU
request.addHeader("Authorization", "Bearer $token")

// ✅ OTOMATIS
// AuthInterceptor akan menambahkan header secara otomatis
```

### Token Lifecycle
```
┌─────────────┐
│   Login/    │
│  Register   │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│ Save Token to   │
│ SharedPrefs     │
└──────┬──────────┘
       │
       ▼
┌─────────────────────────┐
│ Token Valid Until:      │
│ - User logout           │
│ - Token expired         │
│ - App uninstall         │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────┐
│ Clear Token     │
│ Navigate Login  │
└─────────────────┘
```

---

## Protected Routes

### Navigation Guard
Di `NavigationGraph`, aplikasi cek login status untuk menentukan start destination:

```kotlin
@Composable
fun NavigationGraph(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    
    // Check if user is already logged in
    val startDestination = if (tokenManager.isLoggedIn()) {
        Screen.Main.route  // User sudah login → ke Home
    } else {
        Screen.Login.route // User belum login → ke Login
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ...routes
    }
}
```

### Protected Screens
Screens yang membutuhkan authentication:
- ✅ MainScreen (Home, Profile)
- ✅ DetailScreen
- ✅ TransactionScreen
- ✅ AddCategory

Screens yang tidak membutuhkan authentication:
- ❌ LoginScreen
- ❌ RegisterScreen

### API Endpoints Protection
**Tidak Perlu Token:**
- `POST /auth/login`
- `POST /auth/register`

**Perlu Token (Auto inject via AuthInterceptor):**
- `GET /user/profile`
- `PUT /user/profile`
- `GET /categories`
- `POST /categories`
- `GET /transactions`
- `POST /transactions`
- `GET /summary`
- Dan semua endpoint lainnya

---

## Error Handling

### 1. Network Errors
```kotlin
try {
    val response = apiService.login(request)
    Result.success(response)
} catch (e: Exception) {
    Result.failure(e)
}
```

### 2. Validation Errors
```kotlin
private fun validateInput(email: String, password: String): Boolean {
    return when {
        email.isEmpty() -> {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Email tidak boleh kosong"
            )
            false
        }
        // ...other validations
        else -> true
    }
}
```

### 3. API Errors
**401 Unauthorized:**
- Token invalid atau expired
- Action: Logout user, navigate ke Login

**400 Bad Request:**
- Input validation error
- Show error message dari API

**500 Server Error:**
- Backend error
- Show generic error message

### 4. Error Display
```kotlin
// Di Screen - Show Snackbar
LaunchedEffect(uiState.errorMessage) {
    uiState.errorMessage?.let { message ->
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }
}
```

---

## Best Practices Implementation

### ✅ Security
1. **Token disimpan di SharedPreferences** (MODE_PRIVATE)
2. **Token tidak di-log** di production
3. **Password tidak di-log** (hanya 3 karakter pertama untuk debug)
4. **HTTPS** untuk semua API calls

### ✅ Architecture
1. **Separation of Concerns**: Screen → ViewModel → Repository → API
2. **Single Source of Truth**: TokenManager
3. **Reactive UI**: StateFlow di ViewModel
4. **Dependency Injection**: Context-based injection

### ✅ User Experience
1. **Loading States**: Show loading saat API call
2. **Error Messages**: Snackbar untuk error
3. **Input Validation**: Real-time validation
4. **Navigation**: Auto navigate setelah sukses
5. **Persistent Login**: User tetap login setelah app restart

### ✅ Performance
1. **Coroutines**: Asynchronous API calls
2. **Timeout Configuration**: 30 detik timeout
3. **Lazy Initialization**: API service dibuat saat dibutuhkan
4. **Memory Efficient**: SharedPreferences untuk storage

---

## Troubleshooting

### Problem: Token tidak tersimpan setelah login
**Solution:**
```kotlin
// Pastikan save token dipanggil SEBELUM navigate
tokenManager.saveToken(response.token)
tokenManager.saveUserId(response.user.id)
// Kemudian navigate
onNavigateToMain()
```

### Problem: Request 401 Unauthorized
**Kemungkinan Penyebab:**
1. Token tidak ada di SharedPreferences
2. Token expired
3. AuthInterceptor tidak dijalankan

**Solution:**
```kotlin
// Cek token
val token = tokenManager.getToken()
println("Current token: $token")

// Cek isLoggedIn
val isLoggedIn = tokenManager.isLoggedIn()
println("Is logged in: $isLoggedIn")
```

### Problem: User langsung ke Login setelah app restart
**Solution:**
```kotlin
// Pastikan flag is_logged_in di-set saat login
tokenManager.saveToken(token)
prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()

// Pastikan isLoggedIn() cek token tidak null
fun isLoggedIn(): Boolean {
    return prefs.getBoolean(KEY_IS_LOGGED_IN, false) 
        && !getToken().isNullOrEmpty()
}
```

---

## Summary

### Alur Singkat Authentication:

**Login:**
1. User input email & password
2. Validate input
3. Call API login
4. Save token & user ID
5. Navigate to Home

**Register:**
1. User input data lengkap
2. Validate all inputs
3. Call API register
4. Save token & user ID
5. Navigate to Home

**Auto-Login (App Restart):**
1. Check TokenManager.isLoggedIn()
2. Jika true → Start di MainScreen
3. Jika false → Start di LoginScreen

**Protected API Calls:**
1. AuthInterceptor ambil token dari TokenManager
2. Add header: `Authorization: Bearer {token}`
3. Send request dengan token
4. Backend validate token
5. Return data jika valid

**Logout:**
1. Clear token & user data
2. Navigate to Login
3. Clear navigation back stack

---

## File Structure

```
app/src/main/java/com/kotlin/blui/
│
├── data/
│   ├── api/
│   │   ├── ApiConfig.kt           # Retrofit configuration
│   │   ├── ApiService.kt          # API endpoints
│   │   ├── AuthInterceptor.kt     # JWT token injector
│   │   └── TokenManager.kt        # Token storage manager
│   │
│   ├── repository/
│   │   └── AuthRepository.kt      # Auth business logic
│   │
│   └── request/
│       └── AuthRequests.kt        # Login/Register request models
│   
│   └── response/
│       └── AuthResponses.kt       # Auth response models
│
└── presentation/
    ├── auth/
    │   ├── login/
    │   │   ├── LoginScreen.kt     # Login UI
    │   │   └── LoginViewModel.kt  # Login state & logic
    │   │
    │   └── register/
    │       ├── RegisterScreen.kt  # Register UI
    │       └── RegisterViewModel.kt
    │
    └── navigation/
        └── Navigation.kt          # Navigation graph & routes
```

---

## Contact & Support

Untuk pertanyaan atau issue terkait authentication:
1. Check logs di Logcat (tag: API Request/Response)
2. Verify token di SharedPreferences
3. Test API endpoints di Postman
4. Check backend API documentation

Base URL: `https://blui.elginbrian.com/api/v1/`

---

**Dokumentasi dibuat:** November 20, 2025
**Aplikasi:** Blui - Personal Finance Tracker
**Architecture:** Clean Architecture + MVVM

