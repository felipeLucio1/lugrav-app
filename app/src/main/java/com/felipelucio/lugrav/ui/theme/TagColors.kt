package com.felipelucio.lugrav.ui.theme

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.felipelucio.lugrav.R

const val DEFAULT_TAG_COLOR_HEX = "#FFFFFF"

data class TagColorOption(
    @param:StringRes val nameRes: Int,
    val hex: String,
    val color: Color
)

val TagColorOptions = listOf(
    TagColorOption(R.string.tag_color_white, "#FFFFFF", Color(0xFFFFFFFF)),
    TagColorOption(R.string.tag_color_blue, "#29B6F6", Color(0xFF29B6F6)),
    TagColorOption(R.string.tag_color_dark_blue, "#0D47A1", Color(0xFF0D47A1)),
    TagColorOption(R.string.tag_color_purple, "#7E57C2", Color(0xFF7E57C2)),
    TagColorOption(R.string.tag_color_pink, "#EC407A", Color(0xFFEC407A)),
    TagColorOption(R.string.tag_color_red, "#B3261E", Color(0xFFB3261E)),
    TagColorOption(R.string.tag_color_orange, "#F57C00", Color(0xFFF57C00)),
    TagColorOption(R.string.tag_color_amber, "#FFB300", Color(0xFFFFB300)),
    TagColorOption(R.string.tag_color_green, "#2E7D32", Color(0xFF2E7D32)),
    TagColorOption(R.string.tag_color_teal, "#00897B", Color(0xFF00897B))
)

fun tagColorFromHex(hex: String): Color =
    runCatching {
        Color(hex.removePrefix("#").toLong(16) or 0xFF000000)
    }.getOrDefault(Color.White)
