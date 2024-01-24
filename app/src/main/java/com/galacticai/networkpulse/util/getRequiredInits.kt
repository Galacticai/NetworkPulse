package com.galacticai.networkpulse.util

import android.content.Context
import com.galacticai.networkpulse.common.hasNotificationPermission
import com.galacticai.networkpulse.common.isIgnoringBatteryOptimization

fun getRequiredInits(context: Context): List<RequiredInit> {
    val list = mutableListOf<RequiredInit>()

    if (!hasNotificationPermission(context))
        list.add(RequiredInit.NotificationPermission)
    if (!isIgnoringBatteryOptimization(context))
        list.add(RequiredInit.BatteryOptimization)

    return list
}
