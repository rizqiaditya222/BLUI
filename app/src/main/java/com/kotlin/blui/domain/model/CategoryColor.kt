package com.kotlin.blui.domain.model

import androidx.compose.ui.graphics.Color

enum class CategoryColor(val color: Color) {
    RED(Color(0xFFE74C3C)),
    GREEN(Color(0xFF27AE60)),
    BLUE(Color(0xFF3498DB)),
    ORANGE(Color(0xFFF39C12)),
    PINK(Color(0xFFE91E63)),
    PURPLE(Color(0xFF9B59B6)),
    TURQUOISE(Color(0xFF1ABC9C)),
    CARROT(Color(0xFFE67E22)),
    DARK_BLUE_GRAY(Color(0xFF34495E)),
    YELLOW(Color(0xFFF1C40F));

    companion object {
        fun getAllColors(): List<Color> = entries.map { it.color }
    }
}
