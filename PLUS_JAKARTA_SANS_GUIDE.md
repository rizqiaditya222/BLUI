# Plus Jakarta Sans Font - Dokumentasi

## ✅ Yang Sudah Diterapkan

Plus Jakarta Sans telah diterapkan sebagai font utama aplikasi Blui. Font ini akan otomatis digunakan di seluruh UI aplikasi.

## 📋 File yang Dibuat/Diupdate

### 1. **build.gradle.kts**
Menambahkan dependency Google Fonts:
```kotlin
implementation("androidx.compose.ui:ui-text-google-fonts:1.5.4")
```

### 2. **Font.kt** (Baru)
Mendefinisikan Plus Jakarta Sans dengan berbagai weight:
- Light (300)
- Normal (400)
- Medium (500)
- SemiBold (600)
- Bold (700)
- ExtraBold (800)

### 3. **Type.kt** (Updated)
Menerapkan Plus Jakarta Sans ke seluruh typography system Material 3:
- Display styles (Large, Medium, Small)
- Headline styles (Large, Medium, Small)
- Title styles (Large, Medium, Small)
- Body styles (Large, Medium, Small)
- Label styles (Large, Medium, Small)

### 4. **font_certs.xml** (Baru)
File sertifikat untuk Google Fonts Provider (diperlukan untuk download font dari Google).

## 🎨 Cara Menggunakan

### Otomatis (Recommended)
Semua komponen Material 3 akan otomatis menggunakan Plus Jakarta Sans:

```kotlin
@Composable
fun MyScreen() {
    Column {
        // Otomatis menggunakan Plus Jakarta Sans
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Text(
            text = "Masukkan email Anda",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Button(onClick = {}) {
            Text("Login") // Juga menggunakan Plus Jakarta Sans
        }
    }
}
```

### Manual (Custom Style)
```kotlin
Text(
    text = "Custom Text",
    fontFamily = PlusJakartaSans,
    fontWeight = FontWeight.Bold,
    fontSize = 24.sp
)
```

## 📱 Typography Styles yang Tersedia

### Display (Untuk judul besar/hero text)
- `displayLarge` - Bold, 57sp
- `displayMedium` - Bold, 45sp
- `displaySmall` - SemiBold, 36sp

### Headline (Untuk judul section)
- `headlineLarge` - Bold, 32sp ✅ *Dipakai di "Login" header*
- `headlineMedium` - SemiBold, 28sp
- `headlineSmall` - SemiBold, 24sp

### Title (Untuk sub-judul)
- `titleLarge` - SemiBold, 22sp
- `titleMedium` - Medium, 16sp
- `titleSmall` - Medium, 14sp

### Body (Untuk konten/paragraf)
- `bodyLarge` - Normal, 16sp ✅ *Dipakai untuk label "Email", "Password"*
- `bodyMedium` - Normal, 14sp ✅ *Dipakai untuk link "Belum punya akun?"*
- `bodySmall` - Normal, 12sp

### Label (Untuk label kecil)
- `labelLarge` - Medium, 14sp
- `labelMedium` - Medium, 12sp
- `labelSmall` - Medium, 11sp

## 🔧 Troubleshooting

### Jika Font Tidak Muncul:

1. **Sync Gradle**
   - Klik "Sync Now" di Android Studio
   - Atau: File → Sync Project with Gradle Files

2. **Clean & Rebuild**
   - Build → Clean Project
   - Build → Rebuild Project

3. **Pastikan Device/Emulator memiliki Google Play Services**
   - Font akan di-download dari Google saat runtime
   - Emulator harus menggunakan "with Google APIs"

4. **Check Internet Connection**
   - Saat pertama kali run, aplikasi perlu download font dari Google

### Fallback Font
Jika Google Fonts gagal load, sistem akan menggunakan font default Android (Roboto).

## 💡 Contoh di LoginScreen

LoginScreen Anda sekarang sudah menggunakan Plus Jakarta Sans:

```kotlin
// Header "Login" - headlineLarge (Bold, 32sp)
Text(
    text = "Login",
    style = MaterialTheme.typography.headlineLarge
)

// Label "Email" dan "Password" - bodyLarge (Normal, 16sp)
Text(
    text = "Email",
    style = MaterialTheme.typography.bodyLarge,
    fontWeight = FontWeight.Medium
)

// Button text - labelLarge
Button(onClick = {}) {
    Text("Login", fontWeight = FontWeight.SemiBold)
}

// Link register - bodyMedium (Normal, 14sp)
Text(
    text = "Belum punya akun? Register",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.primary
)
```

## ✅ Hasil

- ✅ Font modern dan clean
- ✅ Konsisten di seluruh aplikasi
- ✅ Support berbagai weight (Light hingga ExtraBold)
- ✅ Terintegrasi dengan Material 3 Design System
- ✅ Kompatibel dengan Blue Theme yang sudah dibuat sebelumnya

