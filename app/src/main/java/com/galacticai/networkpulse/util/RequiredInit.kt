package com.galacticai.networkpulse.util

import com.galacticai.networkpulse.R

sealed class RequiredInit(val title: Int, val message: Int) {
    data object NotificationPermission : RequiredInit(
        R.string.notification_permission_title,
        R.string.notification_permission_text,
    )

    data object BatteryOptimization : RequiredInit(
        R.string.battery_optimization_title,
        R.string.battery_optimization_text,
    )
}