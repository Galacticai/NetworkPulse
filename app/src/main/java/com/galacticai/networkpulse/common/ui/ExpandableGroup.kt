package com.galacticai.networkpulse.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.networkpulse.R

@Composable
fun ExpandableGroup(
    title: String,
    withDivider: Boolean = true,
    vararg items: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    val padding = 20.dp
    val roundedCorner = RoundedCornerShape(padding)
    val primaryContainer = colorResource(R.color.primaryContainer)

    Surface(
        color = primaryContainer,
        shape = roundedCorner,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            Surface(
                onClick = { expanded = !expanded },
                color = primaryContainer,
                shape = roundedCorner,
            ) {
                AnimatedContent(
                    expanded,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    contentAlignment = Alignment.Center,
                    label = "headerExpansionState",
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                            vertical = if (it) padding / 4 else padding,
                            horizontal = padding,
                        )
                    ) {
                        Text(
                            text = title,
                            fontWeight = if (it) FontWeight.W200 else FontWeight.Bold,
                            fontSize =  18.sp,
                        )
                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            if (it) Icons.Rounded.KeyboardArrowUp
                            else Icons.Rounded.KeyboardArrowDown,
                            null
                        )
                    }
                }
            }

            Surface(
                color = colorResource(R.color.background),
                shape = roundedCorner,
                modifier = Modifier.padding(padding / 10),
            ) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                ) {
                    Column(modifier = Modifier.padding(padding)) {
                        for ((i, item) in items.withIndex()) {
                            if (withDivider) {
                                if (i > 0 && i < items.size) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = padding / 2),
                                        color = colorResource(R.color.primaryContainer)
                                    )
                                }
                            }
                            item()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExpandableGroup() {
    Column(modifier = Modifier
        .height(500.dp)
        .padding(10.dp)) {
        ExpandableGroup(
            "Title", true,
            { Text("Item 1") },
            { Text("Item 2") },
            { Text("Item 3") },
            { Text("Item 4") },
            { Text("Item 5") },
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExpandableGroup(
            "Title", true,
            { Text("Item 1") },
            { Text("Item 2") },
            { Text("Item 3") },
            { Text("Item 4") },
            { Text("Item 5") },
        )
    }
}