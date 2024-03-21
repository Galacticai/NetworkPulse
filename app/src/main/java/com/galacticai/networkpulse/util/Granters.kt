package com.galacticai.networkpulse.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.galacticai.networkpulse.R

interface Granter //? No body, used only for grouping
abstract class PermissionGranter(
    val requestID: Int,
    val permission: String,
    vararg val permissionsAdditional: String
) : Granter {
    fun isGranted(context: Context) =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    fun grant(activity: Activity) {
        if (isGranted(activity)) return
        activity.requestPermissions(
            arrayOf(permission, *permissionsAdditional),
            requestID
        )
    }
}

object Granters {
    object Permissions {
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        const val ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION"
        const val ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"
    }

    object PersistentNotification : PermissionGranter(
        R.id.persistent_notification_request,
        Permissions.POST_NOTIFICATIONS
    ) {
        const val channelID = "PersistentNotification"

        fun setupChannel(context: Context) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            if (manager.getNotificationChannel(channelID) != null)
                return

            NotificationChannel(
                channelID,
                context.getString(R.string.persistent_notification),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                manager.createNotificationChannel(this)
            }
        }
    }

    object Location : PermissionGranter(
        R.id.location_request,
        Permissions.ACCESS_FINE_LOCATION,
        Permissions.ACCESS_COARSE_LOCATION
    )

    object BatteryOptimization : Granter {
        @SuppressLint("BatteryLife")
        fun grant(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            launcher.launch(intent)
        }
    }

}
