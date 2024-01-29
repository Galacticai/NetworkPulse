package com.galacticai.networkpulse.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties

@Composable
fun <T> CustomDropdownMenu(
    list: List<T>,
    defaultSelected: T,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    colorBG: Color = MaterialTheme.colorScheme.background,
    itemText: (T) -> String,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var stroke by remember { mutableIntStateOf(1) }
    Box(
        modifier
            .padding(8.dp)
            .border(
                border = BorderStroke(stroke.dp, color),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                expanded = !expanded
                stroke = if (expanded) 2 else 1
            },
        contentAlignment = Alignment.Center,
    ) {

        Text(
            text = itemText(defaultSelected),
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                stroke = if (expanded) 2 else 1
            },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            modifier = Modifier
                .background(colorBG)
                .padding(2.dp)
                .fillMaxWidth(.4f)
        ) {
            list.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemText(it),
                            color = color,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        expanded = false
                        stroke = if (expanded) 2 else 1
                        onSelected(it)
                    }
                )
            }
        }

    }
}