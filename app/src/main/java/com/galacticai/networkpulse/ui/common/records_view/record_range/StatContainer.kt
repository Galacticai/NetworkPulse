package com.galacticai.networkpulse.ui.common.records_view.record_range

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.common.Consistent

@Composable
fun StatContainer(title: String? = null, rowsContent: List<@Composable RowScope.() -> Unit>) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colorResource(R.color.background),
        shape = Consistent.shape,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            if (title != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = LocalContentColor.current.copy(.8f)
                )
                Spacer(modifier = Modifier.height(5.dp))
            }
            for (rowContent in rowsContent) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) { rowContent() }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatContainerPreview() {
    Surface {
        StatContainer(
            title = "Title",
            rowsContent = listOf(
                {
                    Stat(title = "Title", value = 123)
                    Stat(title = "Title", value = 123)
                    Stat(title = "Title", value = 123.09)
                },
                {
                    Stat(title = "Title", value = 123)
                    Stat(title = "Title", value = 123)
                    Stat(title = "Title", value = 123.09)
                },
                {
                    Stat(title = "Title", value = 123)
                    Stat(title = "Title", value = 123)
                    Stat(title = "Title", value = 123.09)
                },
            )
        )
    }
}