package com.galacticai.networkpulse.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.galacticai.networkpulse.R

@Composable
fun GalacticTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colorScheme.copy(
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
        ),
    ) {
        content()
    }
}
