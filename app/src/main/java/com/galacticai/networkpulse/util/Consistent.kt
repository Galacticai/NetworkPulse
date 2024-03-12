package com.galacticai.networkpulse.util

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Common UI consistency runtime values */
object Consistent {
    /** Corner radius */
    val radius = 20.dp

    /** Rounded corners (using [radius] as value) */
    val shape = RoundedCornerShape(radius)

    /** Rounded start corners (using [radius] as value) */
    val shapeStart = RoundedCornerShape(topStart = radius, bottomStart = radius)

    /** Rounded end corners (using [radius] as value) */
    val shapeEnd = RoundedCornerShape(topEnd = radius, bottomEnd = radius)

    /** Rounded top corners (using [radius] as value) */
    val shapeTop = RoundedCornerShape(topStart = radius, topEnd = radius)

    /** Rounded bottom corners (using [radius] as value) */
    val shapeBottom = RoundedCornerShape(bottomStart = radius, bottomEnd = radius)

    /** Screen horizontal padding */
    val screenHorizontalPadding = 10.dp

    /** Screen horizontal padding [Modifier] (using [screenHorizontalPadding] as value) */
    fun Modifier.screenHPadding() = this.padding(horizontal = screenHorizontalPadding)
}