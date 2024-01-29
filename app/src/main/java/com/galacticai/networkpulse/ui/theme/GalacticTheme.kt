package com.galacticai.networkpulse.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R

const val FontName = "Quicksand"

@Composable
fun GalacticTheme(content: @Composable () -> Unit) {
    val fontFamily = FontFamily(
        Font(
            googleFont = GoogleFont(FontName),
            fontProvider = GoogleFont.Provider(
                providerAuthority = "com.google.android.gms.fonts",
                providerPackage = "com.google.android.gms",
                certificates = R.array.com_google_android_gms_fonts_certs
            ),
        )
    )
    val mt = MaterialTheme.typography
    fun TextStyle.withFont(): TextStyle = copy(fontFamily = fontFamily)
    val typography = Typography(
        displayLarge = mt.displayLarge.withFont(),
        displayMedium = mt.displayMedium.withFont(),
        displaySmall = mt.displaySmall.withFont(),
        headlineLarge = mt.headlineLarge.withFont(),
        headlineMedium = mt.headlineMedium.withFont(),
        headlineSmall = mt.headlineSmall.withFont(),
        titleLarge = mt.titleLarge.withFont(),
        titleMedium = mt.titleMedium.withFont(),
        titleSmall = mt.titleSmall.withFont(),
        bodyLarge = mt.bodyLarge.withFont(),
        bodyMedium = mt.bodyMedium.withFont(),
        bodySmall = mt.bodySmall.withFont(),
        labelLarge = mt.labelLarge.withFont(),
        labelMedium = mt.labelMedium.withFont(),
        labelSmall = mt.labelSmall.withFont(),
    )

    val shapes = MaterialTheme.shapes.copy(
        extraSmall = MaterialTheme.shapes.extraSmall.copy(CornerSize(5.dp)),
        small = MaterialTheme.shapes.small.copy(CornerSize(10.dp)),
        medium = MaterialTheme.shapes.medium.copy(CornerSize(20.dp)),
        large = MaterialTheme.shapes.large.copy(CornerSize(30.dp)),
        extraLarge = MaterialTheme.shapes.extraLarge.copy(CornerSize(40.dp)),
    )

    val colors = colorScheme.copy(
        primary = colorResource(R.color.primary),
        onPrimary = colorResource(R.color.onPrimary),
        primaryContainer = colorResource(R.color.primaryContainer),
        onPrimaryContainer = colorResource(R.color.onPrimaryContainer),
        secondary = colorResource(R.color.secondary),
        onSecondary = colorResource(R.color.onSecondary),
        secondaryContainer = colorResource(R.color.secondaryContainer),
        onSecondaryContainer = colorResource(R.color.onSecondaryContainer),
        background = colorResource(R.color.background),
        onBackground = colorResource(R.color.onBackground),
        surface = colorResource(R.color.surface),
        onSurface = colorResource(R.color.onSurface),
        error = colorResource(R.color.error),
        onError = colorResource(R.color.onError),
        errorContainer = colorResource(R.color.errorContainer),
        onErrorContainer = colorResource(R.color.onErrorContainer),
        //            success = colorResource(R.color.success),
        //            onSuccess = colorResource(R.color.onSuccess),
        //            warning = colorResource(R.color.warning),
        //            onWarning = colorResource(R.color.onWarning),
    )

    MaterialTheme(
        typography = typography,
        shapes = shapes,
        colorScheme = colors,
    ) { content() }
}