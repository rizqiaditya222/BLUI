# API Configuration - Blui Expense Tracker

## 📁 Struktur File API

```
data/api/
├── ApiConfig.kt           # Konfigurasi Retrofit (Singleton)
├── ApiService.kt          # Interface endpoint API
├── AuthInterceptor.kt     # Interceptor untuk JWT token
├── TokenManager.kt        # Manager untuk simpan/ambil token
├── request/               # Request DTOs
│   ├── AuthRequests.kt
│   ├── CategoryRequests.kt
│   └── TransactionRequests.kt
└── response/              # Response DTOs
    ├── AuthResponses.kt
    ├── CategoryResponses.kt
    ├── SummaryResponses.kt
    └── TransactionResponses.kt
```

## 🔧 Cara Menggunakan

### 1. **Update Base URL** di `ApiConfig.kt`

```kotlin
// Ganti dengan URL backend Anda
private const val BASE_URL = "https://your-api-url.com/api/"
```

### 2. **Cara Memanggil API dari Repository**

```kotlin
class YourRepository(private val context: Context) {
    
    // Buat instance ApiService menggunakan ApiConfig
    private val apiService: ApiService = ApiConfig.getApiService(context)
    private val tokenManager: TokenManager = TokenManager(context)
    
    // Contoh fungsi
    suspend fun getData(): Result<YourResponse> {
        return try {
            val response = apiService.yourEndpoint()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 3. **Flow Autentikasi**

#### Login:
```kotlin
val authRepository = AuthRepository(context)
val result = authRepository.login("email@example.com", "password")

result.onSuccess { authResponse ->
    // Token otomatis tersimpan di SharedPreferences
    // Navigasi ke home screen
}
result.onFailure { exception ->
    // Handle error
}
```

#### Menggunakan API yang Memerlukan Token:
```kotlin
// Token otomatis ditambahkan ke header oleh AuthInterceptor
// Anda tidak perlu manual menambahkan Authorization header

val transactions = apiService.getTransactions(month = 11, year = 2025)
// Request akan otomatis include: Authorization: Bearer {token}
```

#### Logout:
```kotlin
authRepository.logout()
// Token dan semua data user dihapus dari SharedPreferences
```

## 🔐 Cara Kerja Autentikasi

1. **User Login/Register** → Backend return JWT token
2. **TokenManager** simpan token di SharedPreferences
3. **AuthInterceptor** otomatis tambahkan token ke setiap request:
   ```
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```
4. **Backend** extract userId dari token
5. **Data** otomatis ter-filter per user

## 📝 Contoh Penggunaan di ViewModel

```kotlin
class LoginViewModel(private val context: Context) : ViewModel() {
    
    private val authRepository = AuthRepository(context)
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            
            result.onSuccess { authResponse ->
                // Navigate to home
                println("Login sukses: ${authResponse.user.fullName}")
            }
            result.onFailure { exception ->
                // Show error
                println("Login gagal: ${exception.message}")
            }
        }
    }
}
```

## 🛠️ Dependencies yang Dibutuhkan

Sudah ditambahkan di `app/build.gradle.kts`:

```kotlin
// Retrofit & Gson
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

## 🔍 Logging

Untuk development, semua request/response akan terlihat di Logcat:
- Tag: `OkHttp`
- Level: `BODY` (menampilkan seluruh request dan response)

Untuk production, ubah level di `ApiConfig.kt`:
```kotlin
level = HttpLoggingInterceptor.Level.NONE
```

## 📱 Contoh Output Logcat

```
D/OkHttp: --> POST https://your-api.com/api/auth/login
D/OkHttp: Content-Type: application/json
D/OkHttp: {"email":"user@example.com","password":"..."}
D/OkHttp: --> END POST

D/OkHttp: <-- 200 OK https://your-api.com/api/auth/login
D/OkHttp: {"token":"eyJhbGci...","user":{...}}
D/OkHttp: <-- END HTTP
```

## ⚠️ Catatan Penting

1. **BASE_URL harus diakhiri dengan `/`**
2. **Token otomatis tersimpan** setelah login/register
3. **Token otomatis dikirim** di setiap request (kecuali login/register)
4. **Jangan lupa panggil `logout()`** saat user logout
5. **Error handling** menggunakan `Result<T>` (success/failure)

