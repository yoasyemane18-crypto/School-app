package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoContainerLight,
    onPrimaryContainer = IndigoOnContainerLight,
    secondary = EmeraldSecondary,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainerLight,
    onSecondaryContainer = EmeraldOnContainerLight,
    tertiary = AmberTertiary,
    onTertiary = Color.White,
    tertiaryContainer = AmberContainerLight,
    onTertiaryContainer = AmberOnContainerLight,
    error = RoseError,
    onError = Color.White,
    errorContainer = RoseContainerLight,
    onErrorContainer = RoseOnContainerLight,
    background = SlateBackgroundLight,
    onBackground = SlateTextPrimary,
    surface = SlateSurfaceLight,
    onSurface = SlateTextPrimary,
    surfaceVariant = SlateSurfaceVariantLight,
    onSurfaceVariant = SlateTextSecondary,
    outline = SlateOutlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = IndigoDeep,
    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoContainerLight,
    secondary = EmeraldSecondaryDark,
    onSecondary = Color(0xFF022C22),
    secondaryContainer = EmeraldContainerDark,
    onSecondaryContainer = EmeraldContainerLight,
    tertiary = AmberTertiaryDark,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = AmberContainerDark,
    onTertiaryContainer = AmberContainerLight,
    error = RoseErrorDark,
    onError = Color(0xFF450A0A),
    errorContainer = RoseContainerDark,
    onErrorContainer = RoseContainerLight,
    background = SlateBackgroundDark,
    onBackground = Color(0xFFF8FAFC),
    surface = SlateSurfaceDark,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = SlateSurfaceVariantDark,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SlateOutlineDark
)

val SafeRideShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun SafeRideEduPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SafeRideTypography,
        shapes = SafeRideShapes,
        content = content
    )
}
