package com.kelsos.mbrc.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColors = darkColors(
  primary = Primary,
  primaryVariant = PrimaryDark,
  secondary = Accent
)

@Composable
fun RemoteTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colors = DarkColors,
    content = content
  )
}
