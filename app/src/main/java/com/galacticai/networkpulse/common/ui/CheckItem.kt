package com.galacticai.networkpulse.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CheckItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = RectangleShape,
    onCheckedListener: ItemCheckedListener? = null,
) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    fun onClick(newState: Boolean) {
        isChecked = newState
        onCheckedListener?.invoke(newState)
    }
    Surface(
        color = color,
        shape = shape,
        onClick = { onClick(!isChecked) },
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isChecked, onCheckedChange = { onClick(it) })
                Text(title, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = subtitle
            )
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Preview(showBackground = true)
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
