package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.util.Consistent

@Composable
fun RowScope.Stat(title: String?, value: Any, color: Color = Color.Unspecified) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp),
        border = if (color == Color.Unspecified) null
        else BorderStroke(1.dp, color = color.copy(.25f)),
        shape = Consistent.shape,
        color = colorResource(R.color.background),
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (title != null) {
                val secondary = colorResource(R.color.secondary)
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = secondary,
                )
                HorizontalDivider(
                    color = secondary.copy(.25f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                        .widthIn(max = 100.dp)
                )
            }
            Text(
                text = value.toString(),
                color = color,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp,
                fontSize = 14.sp,
            )
        }
    }
}

@Preview
@Composable
fun StatPreview() {
    Row {
        Stat(title = "Title", value = "12.05\nKB/s")
        Stat(title = "Title", value = "Value\nValue")
        Stat(title = "Title", value = "Value\n")
    }
}