package com.galacticai.networkpulse.ui.main.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.galacticai.networkpulse.R

enum class MainScreen(val labelID: Int, val icon: ImageVector) {
    Overview(R.string.overview, Icons.Rounded.Home),
    Dashboard(R.string.dashboard, Icons.Rounded.Info),
    Settings(R.string.settings, Icons.Rounded.Settings);

    val route get() = labelID.toString()
}

