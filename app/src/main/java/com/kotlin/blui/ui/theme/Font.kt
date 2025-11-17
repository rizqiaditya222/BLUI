package com.kotlin.blui.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.kotlin.blui.R

// Provider untuk Google Fonts
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// Plus Jakarta Sans font dari Google Fonts
val plusJakartaSansFont = GoogleFont("Plus Jakarta Sans")

// Font Family dengan berbagai weight
val PlusJakartaSans = FontFamily(
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = plusJakartaSansFont,
        fontProvider = provider,
        weight = FontWeight.Light
    ),
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = plusJakartaSansFont,
        fontProvider = provider,
        weight = FontWeight.Normal
    ),
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = plusJakartaSansFont,
        fontProvider = provider,
        weight = FontWeight.Medium
    ),
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = plusJakartaSansFont,
        fontProvider = provider,
        weight = FontWeight.SemiBold
    ),
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = plusJakartaSansFont,
        fontProvider = provider,
        weight = FontWeight.Bold
    ),
    androidx.compose.ui.text.googlefonts.Font(
        googleFont = plusJakartaSansFont,
        fontProvider = provider,
        weight = FontWeight.ExtraBold
    )
)
