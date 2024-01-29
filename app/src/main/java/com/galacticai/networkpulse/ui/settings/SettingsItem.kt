package com.galacticai.networkpulse.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R

open class SettingsItem(
    val title: String,
    val subtitle: String,
    var grouped: Boolean = false,
    val onResetClick: ((context: Context) -> Unit)? = null,
    private val content: @Composable () -> Unit,
) {
    @Composable
    private fun Bones() {
        val ctx = LocalContext.current
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (onResetClick != null)
                    IconButton(onClick = { onResetClick!!(ctx) }) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = (stringResource(R.string.reset))
                        )
                    }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            content()
        }
    }

    @Composable
    fun Content() {
        if (grouped) Bones()
        else Surface(
            color = colorResource(R.color.primaryContainer),
            shape = RoundedCornerShape(20.dp),
        ) {
            Bones()
        }
    }
}