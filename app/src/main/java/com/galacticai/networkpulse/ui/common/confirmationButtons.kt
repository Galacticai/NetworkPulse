package com.galacticai.networkpulse.ui.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R

@Composable
fun ConfirmationButtons(
    text: String,
    modifier: Modifier = Modifier,
    onConfirmed: () -> Unit
) {
    var confirming by remember { mutableStateOf(false) }

    AnimatedContent(
        modifier = modifier,
        targetState = confirming, label = "animation_$text",
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) {
        val elevation = if (it) ButtonDefaults.buttonElevation(5.dp) else null
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { confirming = !confirming },
                colors = if (it) ButtonDefaults.buttonColors()
                else ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorResource(R.color.primary),
                ),
                elevation = elevation,
            ) {
                Text(if (it) stringResource(R.string.cancel) else text)
            }

            if (it) {
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {
                        onConfirmed()
                        confirming = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.errorContainer),
                        contentColor = colorResource(R.color.onErrorContainer),
                    ),
                    elevation = elevation,
                ) {
                    Text(stringResource(R.string.are_you_sure))
                }
            }
        }
    }
}