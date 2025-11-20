# Implementasi Autentikasi Blui

## Fitur yang Telah Diimplementasikan

### 1. **Persistent Login (Remember User)**
- User yang sudah login akan tetap login meskipun aplikasi ditutup
- Menggunakan `SharedPreferences` untuk menyimpan token dan data user
- Saat aplikasi dibuka, sistem otomatis cek apakah user sudah login
- Jika sudah login, langsung masuk ke Main Screen
- Jika belum, diarahkan ke Login Screen

### 2. **Login System**
- Form login dengan email dan password
- Validasi input:
  - Email tidak boleh kosong dan harus format valid
  - Password tidak boleh kosong dan minimal 6 karakter
- Loading indicator saat proses login
- Error handling dengan snackbar
- Auto-navigate ke Main Screen setelah login berhasil

### 3. **Register System**
- Form registrasi lengkap:
  - Nama lengkap
  - Email
  - Tanggal lahir
  - Password
  - Konfirmasi password
- Validasi input:
  - Nama minimal 3 karakter
  - Email format valid
  - Password minimal 6 karakter
  - Password dan konfirmasi harus cocok
  - Tanggal lahir wajib diisi
- Auto-login setelah registrasi berhasil

### 4. **Profile Management**
- Menampilkan data user (nama, email, tanggal lahir)
- Edit profile (kecuali email)
- Logout functionality

## Struktur File

### Data Layer

#### API
- `TokenManager.kt` - Mengelola penyimpanan token dan data user di SharedPreferences
- `AuthInterceptor.kt` - Menambahkan JWT token ke setiap request
- `ApiConfig.kt` - Konfigurasi Retrofit dengan OkHttp client
- `ApiService.kt` - Interface untuk endpoint API

#### Request & Response Models
- `AuthRequests.kt` - Data class untuk login, register, update profile
- `AuthResponses.kt` - Data class untuk response dari API

#### Repository
- `AuthRepository.kt` - Mengelola logic autentikasi (login, register, logout, profile)

### Presentation Layer

#### ViewModels
- `LoginViewModel.kt` - State management untuk login
- `RegisterViewModel.kt` - State management untuk register
- `ProfileViewModel.kt` - State management untuk profile

#### Screens
- `LoginScreen.kt` - UI untuk login
- `RegisterScreen.kt` - UI untuk register
- `ProfileScreen.kt` - UI untuk profile dan logout

#### Navigation
- `Navigation.kt` - Mengelola routing dengan persistent login check

## Cara Kerja Persistent Login

### 1. **Saat Login/Register Berhasil**
```kotlin
// Token dan user data disimpan otomatis
tokenManager.saveToken(response.token)
tokenManager.saveUserId(response.user.id)
```

### 2. **Saat Aplikasi Dibuka**
```kotlin
// Navigation.kt mengecek status login
val startDestination = if (tokenManager.isLoggedIn()) {
    Screen.Main.route  // Langsung ke Main jika sudah login
} else {
    Screen.Login.route // Ke Login jika belum
}
```

### 3. **Setiap API Request**
```kotlin
// AuthInterceptor otomatis menambahkan token
val token = tokenManager.getToken()
if (token != null) {
    request.newBuilder()
        .addHeader("Authorization", "Bearer $token")
        .build()
}
```

### 4. **Saat Logout**
```kotlin
// Semua data dihapus dari SharedPreferences
tokenManager.clearToken()
// User diarahkan kembali ke Login Screen
```

## Konfigurasi API

### Base URL
Saat ini menggunakan: `https://blui.elginbrian.com/`

Untuk mengubah base URL, edit file:
```kotlin
// ApiConfig.kt
private const val BASE_URL = "https://your-api-url.com/"
```

## Dependencies yang Digunakan

- **Retrofit** - HTTP client untuk API calls
- **Gson** - JSON serialization/deserialization
- **OkHttp** - HTTP client dengan interceptor support
- **Coroutines** - Async operations
- **Jetpack Compose** - UI framework
- **Navigation Compose** - Navigation management
- **ViewModel & StateFlow** - State management

## Testing

### Test Login
1. Buka aplikasi
2. Masukkan email dan password
3. Klik tombol "Masuk"
4. Jika berhasil, otomatis masuk ke Main Screen
5. Tutup dan buka kembali aplikasi
6. **Seharusnya langsung masuk tanpa perlu login lagi**

### Test Register
1. Dari Login Screen, klik "Daftar"
2. Isi semua form
3. Klik tombol "Daftar"
4. Jika berhasil, otomatis masuk ke Main Screen
5. Data user sudah tersimpan

### Test Logout
1. Buka Profile Screen
2. Klik tombol "Sign Out"
3. Otomatis kembali ke Login Screen
4. Token dan data user terhapus
5. Aplikasi tidak akan auto-login lagi

## Keamanan

- Token disimpan dengan aman di SharedPreferences (MODE_PRIVATE)
- Password tidak disimpan di local, hanya token
- Token expired akan dihandle oleh backend
- HTTPS untuk semua API calls (pastikan API menggunakan SSL)

## Troubleshooting

### Masalah: Tidak bisa login
- Cek koneksi internet
- Pastikan API URL sudah benar
- Cek log untuk error message

### Masalah: Token hilang setelah restart
- Pastikan `TokenManager` menggunakan `MODE_PRIVATE`
- Cek apakah `saveToken()` dipanggil setelah login berhasil

### Masalah: Auto-login tidak bekerja
- Cek `TokenManager.isLoggedIn()` return value
- Pastikan token tersimpan dengan benar
- Cek Navigation.kt untuk logic startDestination

## Next Steps

### Implementasi Tambahan yang Disarankan:
1. **Forgot Password** - Reset password via email
2. **Biometric Authentication** - Login dengan fingerprint
3. **Token Refresh** - Auto refresh expired token
4. **Multiple Device Login** - Manage active sessions
5. **Social Login** - Login dengan Google/Facebook

## Catatan Penting

⚠️ **Jangan commit file yang berisi:**
- API keys
- Secret tokens
- Production credentials

✅ **Gunakan:**
- Environment variables
- `.gitignore` untuk sensitive files
- Different config untuk dev/prod

