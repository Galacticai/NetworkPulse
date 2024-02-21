package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
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

@Composable
fun RowScope.Stat(title: String?, value: Any, color: Color = Color.Unspecified) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp),
        border = BorderStroke(
            1.dp,
            color = (
                    if (color == Color.Unspecified) colorResource(R.color.surface)
                    else color
                    ).copy(.25f)
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (title != null) {
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
                Divider(color = colorResource(R.color.surface))
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