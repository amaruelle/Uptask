package org.leftbrained.uptaskapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colors = if (!useDarkTheme) {
        dynamicLightColorScheme(context)
    } else {
        dynamicDarkColorScheme(context)
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}