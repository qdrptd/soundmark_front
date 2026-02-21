package com.example.soundmark.util

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TextStyle Extension Functions for cleaner chaining
 */

fun TextStyle.size(sp: Int): TextStyle = this.copy(fontSize = sp.sp)

fun TextStyle.size(unit: TextUnit): TextStyle = this.copy(fontSize = unit)

fun TextStyle.color(color: Color): TextStyle = this.copy(color = color)

fun TextStyle.bold(): TextStyle = this.copy(fontWeight = FontWeight.Bold)

fun TextStyle.medium(): TextStyle = this.copy(fontWeight = FontWeight.Medium)

fun TextStyle.light(): TextStyle = this.copy(fontWeight = FontWeight.Light)

fun TextStyle.italic(): TextStyle = this.copy(fontStyle = FontStyle.Italic)

fun TextStyle.lineHeight(sp: Int): TextStyle = this.copy(lineHeight = sp.sp)

fun TextStyle.weight(weight: FontWeight): TextStyle = this.copy(fontWeight = weight)

fun TextStyle.alpha(value: Float): TextStyle = this.copy(color = this.color.copy(alpha = value))


/**
 * Modifier Extension Functions for SoundMark specific logic
 */

/**
 * Applies blur only if the condition is met.
 * Useful for hiding information from non-owner profiles.
 */
fun Modifier.blurIf(condition: Boolean, radius: Dp = 10.dp): Modifier {
    return if (condition) {
        this.blur(radius)
    } else {
        this
    }
}

/**
 * Extension for quick horizontal padding
 */
fun Modifier.paddingX(value: Dp): Modifier = this.padding(horizontal = value)

/**
 * Extension for quick vertical padding
 */
fun Modifier.paddingY(value: Dp): Modifier = this.padding(vertical = value)

/**
 * Conditional alpha
 */
fun Modifier.alphaIf(condition: Boolean, value: Float): Modifier {
    return if (condition) this.alpha(value) else this
}
