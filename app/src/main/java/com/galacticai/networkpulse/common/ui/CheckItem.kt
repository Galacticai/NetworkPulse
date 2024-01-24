package com.galacticai.networkpulse.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CheckItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    onCheckedListener: ItemCheckedListener? = null,
) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    fun onClick(newState: Boolean) {
        isChecked = newState
        onCheckedListener?.invoke(newState)
    }
    Surface(
        onClick = { onClick(!isChecked) },
        modifier = modifier.fillMaxWidth(1f)
    ) {
        Column {
            Row {
                Checkbox(checked = isChecked, onCheckedChange = { onClick(it) })
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = 16.dp),
                    text = title,
                )
            }
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                text = subtitle
            )
        }
    }
}

@Preview
@Composable
fun CheckItemPreview() {
    CheckItem(
        title = "Not a very long title",
        subtitle = "Subtitle",
    )
}
/** Callback for when a check is changed
 * @param isChecked current state
 * @return new state
 */
typealias ItemCheckedListener = (isChecked: Boolean) -> Boolean
