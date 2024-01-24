package com.galacticai.networkpulse.common

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE


@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val running = manager.getRunningServices(Integer.MAX_VALUE)
    return running.any { it.service.className == service.name }
}