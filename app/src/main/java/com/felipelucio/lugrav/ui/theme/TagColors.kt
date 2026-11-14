package com.felipelucio.lugrav.ui.theme

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.felipelucio.lugrav.R

const val DEFAULT_TAG_COLOR_HEX = "#475569"

data class TagColorOption(
    @param:StringRes val nameRes: Int,
    val hex: String,
    val color: Color,
    val containerColor: Color
)

val TagColorOptions = listOf(
    TagColorOption(R.string.tag_color_royal_blue, "#2563EB", Color(0xFF2563EB), Color(0xFFEFF6FF)),
    TagColorOption(R.string.tag_color_amber, "#D97706", Color(0xFFD97706), Color(0xFFFFFBEB)),
    TagColorOption(R.string.tag_color_emerald, "#059669", Color(0xFF059669), Color(0xFFECFDF5)),
    TagColorOption(R.string.tag_color_violet, "#7C3AED", Color(0xFF7C3AED), Color(0xFFF5F3FF)),
    TagColorOption(R.string.tag_color_teal, "#0D9488", Color(0xFF0D9488), Color(0xFFF0FDFA)),
    TagColorOption(R.string.tag_color_rose, "#E11D48", Color(0xFFE11D48), Color(0xFFFFF1F2)),
    TagColorOption(R.string.tag_color_sky, "#0284C7", Color(0xFF0284C7), Color(0xFFF0F9FF)),
    TagColorOption(R.string.tag_color_indigo, "#4F46E5", Color(0xFF4F46E5), Color(0xFFEEF2FF)),
    TagColorOption(R.string.tag_color_lime, "#65A30D", Color(0xFF65A30D), Color(0xFFF7FEE7)),
    TagColorOption(R.string.tag_color_slate, DEFAULT_TAG_COLOR_HEX, Slate, Color(0xFFF8FAFC))
)

fun tagColorFromHex(hex: String): Color =
    runCatching {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    }.getOrDefault(Slate)

fun tagContainerFromHex(hex: String): Color {
    val normalizedHex = hex.uppercase()
    return TagColorOptions
        .firstOrNull { it.hex.equals(normalizedHex, ignoreCase = true) }
        ?.containerColor
        ?: lerp(tagColorFromHex(hex), Color.White, 0.88f)
}
