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


object Grants {
    object Permissions {
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        // course/fine location later for wifi
    }

    object PersistentNotification {
        private const val permission = Permissions.POST_NOTIFICATIONS
        const val channelID = "PersistentNotification"

        fun isGranted(context: Context) =
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

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

        fun grantPermission(activity: Activity) {
            if (isGranted(activity)) return
            activity.requestPermissions(arrayOf(permission), R.id.persistent_notification_request)
        }
    }

    object BatteryOptimization {
        @SuppressLint("BatteryLife")
        fun grant(activity: Activity, launcher: ActivityResultLauncher<Intent>) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            launcher.launch(intent)
        }
    }
}
