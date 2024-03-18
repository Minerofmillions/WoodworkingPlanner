package minerofmillions.utils

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color as ComposeColor
import java.awt.Color as JavaColor

fun ComposeColor.toJavaColor() = JavaColor(toArgb())
fun JavaColor.toComposeColor() = ComposeColor(rgb)