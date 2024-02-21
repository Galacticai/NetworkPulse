package com.galacticai.networkpulse.ui.settings

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    val title: String? = null,
    val subtitle: String? = null,
    var grouped: Boolean = false,
    val onResetClick: ((context: Context) -> Unit)? = null,
    /** itemPadding should only be used if no title (custom content) */
    private val content: (@Composable (itemPadding: PaddingValues) -> Unit)? = null,
) {
    @Composable
    private fun Bones(itemPadding: PaddingValues) {
        if (title == null) {
            content?.invoke(itemPadding)
            return
        }
        val ctx = LocalContext.current
        Column(modifier = Modifier.padding(itemPadding)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.weight(1f))
                if (onResetClick != null)
                    IconButton(onClick = { onResetClick.invoke(ctx) }) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = (stringResource(R.string.reset))
                        )
                    }
            }
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp
                )
            }
            if (content != null) {
                Spacer(modifier = Modifier.height(5.dp))
                content.invoke(itemPadding)
            }
        }
    }

    @Composable
    fun Content(itemPadding: PaddingValues) {
        if (grouped) Bones(itemPadding)
        else Surface(
            color = colorResource(R.color.surface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Bones(itemPadding)
        }
    }
}