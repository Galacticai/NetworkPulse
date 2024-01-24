package com.galacticai.networkpulse.util

class RequiredPermissions {
    companion object {
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"

        const val BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
        const val QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
        const val LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED"
    }
}