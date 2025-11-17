# Blue Theme Colors - Dokumentasi Penggunaan

## 🎨 Palet Warna yang Tersedia

### Light Variants (Warna Terang)
```kotlin
BlueLight = #E7F4FA        // Background terang, surface
BlueLightHover = #DBEFF7   // Hover state untuk elemen terang
BlueLightActive = #B4DEEF  // Active/pressed state untuk elemen terang
```

### Normal Variants (Warna Utama)
```kotlin
BlueNormal = #0C95CB       // Primary color - tombol, link, accent
BlueNormalHover = #0B86B7  // Hover state untuk primary
BlueNormalActive = #0A77A2 // Active/pressed state untuk primary
```

### Dark Variants (Warna Gelap)
```kotlin
BlueDark = #097098         // Secondary color, text on light bg
BlueDarkHover = #07597A    // Hover state untuk dark elements
BlueDarkActive = #05435B   // Active/pressed state untuk dark elements
```

### Darker Variant (Warna Paling Gelap)
```kotlin
BlueDarker = #043447       // Text, icons, borders
```

---

## 📋 Cara Menggunakan di UI

### 1. Menggunakan Material Theme Colors (Recommended)
Theme sudah dikonfigurasi otomatis menggunakan warna biru custom:

```kotlin
@Composable
fun MyComponent() {
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary // BlueNormal
        )
    ) {
        Text("Login")
    }
}
```

### 2. Menggunakan Warna Langsung
Untuk kasus khusus, Anda bisa menggunakan warna secara langsung:

```kotlin
import com.kotlin.blui.ui.theme.*

@Composable
fun CustomCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlueLight), // Background biru terang
        border = BorderStroke(1.dp, BlueDark) // Border biru gelap
    ) {
        Text(
            text = "Balance",
            color = BlueDarker // Text biru paling gelap
        )
    }
}
```

### 3. Hover/Active States untuk Interaksi
```kotlin
@Composable
fun InteractiveButton() {
    var isPressed by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .background(
                if (isPressed) BlueNormalActive else BlueNormal
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            }
    ) {
        Text("Click Me", color = Color.White)
    }
}
```

---

## 🎯 Material Theme Color Mapping

Aplikasi sudah dikonfigurasi dengan mapping berikut:

### Light Mode
- **primary** = BlueNormal (#0C95CB) - Tombol utama, link
- **primaryContainer** = BlueLight (#E7F4FA) - Container/card background
- **secondary** = BlueNormalHover (#0B86B7) - Secondary actions
- **tertiary** = BlueDark (#097098) - Tertiary elements
- **background** = White (#FDFCFF) - App background
- **surface** = White (#FDFCFF) - Card/surface background
- **surfaceVariant** = BlueLight (#E7F4FA) - Variant surfaces

### Dark Mode
- **primary** = BlueNormal (#0C95CB) - Tombol utama, link
- **primaryContainer** = BlueDark (#097098) - Container/card background
- **secondary** = BlueDarkHover (#07597A) - Secondary actions
- **background** = Dark Gray (#1A1C1E) - App background
- **surface** = Dark Gray (#1A1C1E) - Card/surface background

---

## 💡 Contoh Penggunaan di Komponen

### Login Button
```kotlin
Button(
    onClick = { },
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary, // BlueNormal
        contentColor = Color.White
    )
) {
    Text("Login")
}
```

### Card dengan Background Biru Terang
```kotlin
Card(
    colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer // BlueLight
    )
) {
    Text(
        text = "Balance: Rp. 500.000",
        color = MaterialTheme.colorScheme.onPrimaryContainer // BlueDarker
    )
}
```

### Text dengan Warna Biru
```kotlin
Text(
    text = "Belum punya akun? Register",
    color = MaterialTheme.colorScheme.primary, // BlueNormal
    modifier = Modifier.clickable { }
)
```

### Icon dengan Warna Biru Gelap
```kotlin
Icon(
    imageVector = Icons.Default.Email,
    contentDescription = "Email",
    tint = BlueDark // atau MaterialTheme.colorScheme.tertiary
)
```

---

## 🔧 Konfigurasi Theme

Di `MainActivity.kt` atau root composable:
```kotlin
@Composable
fun MyApp() {
    BluiTheme {
        // Semua composable di sini akan otomatis menggunakan blue theme
        LoginScreen()
    }
}
```

Untuk force light/dark mode:
```kotlin
BluiTheme(darkTheme = false) { // Selalu light mode
    LoginScreen()
}

BluiTheme(darkTheme = true) { // Selalu dark mode
    LoginScreen()
}
```

---

## 📱 Preview dengan Blue Theme

```kotlin
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BluiTheme {
        LoginScreen()
    }
}
```

---

## ✅ Catatan Penting

1. **Gunakan MaterialTheme.colorScheme** untuk konsistensi
2. **BlueLight** cocok untuk background card/section
3. **BlueNormal** untuk tombol primary dan link
4. **BlueDark** untuk icon dan text secondary
5. **BlueDarker** untuk text utama di background terang
6. Theme otomatis support **Light & Dark Mode**

