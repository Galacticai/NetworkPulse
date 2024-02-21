package com.galacticai.networkpulse.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.isIgnoringBatteryOptimization
import com.galacticai.networkpulse.services.PulseService
import com.galacticai.networkpulse.ui.common.TopBar
import com.galacticai.networkpulse.ui.prepare.PrepareItem
import com.galacticai.networkpulse.ui.theme.GalacticTheme
import com.galacticai.networkpulse.util.RequiredInit
import com.galacticai.networkpulse.util.getRequiredInits


class PrepareActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PrepareActivityContent() }
    }
}


@Composable
private fun PrepareActivityContent() {
    val context = LocalContext.current

    val requiredInitsList = getRequiredInits(LocalContext.current)
    if (requiredInitsList.isEmpty()) {
        finish(context)
        return
    }

    val requiredInits = requiredInitsList.associateWith { false }.toMutableMap()

    fun isReady() = requiredInits.all { it.value }
    fun finishIfReady() {
        if (isReady()) finish(context)
    }
    GalacticTheme {
        Scaffold(
            topBar = {
                TopBar(stringResource(R.string.prepare_activity_title))
            }, bottomBar = {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colorResource(R.color.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { finishIfReady() },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            Text(stringResource(R.string.done))
                        }
                    }
                }
            }
        ) { scaffoldPadding ->
            Surface(
                color = colorResource(R.color.background),
                border = BorderStroke(2.dp, colorResource(R.color.surface)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(scaffoldPadding)
            ) {
                LazyColumn {
                    items(requiredInitsList.size) {
                        if (it > 0) {
                            Divider(
                                color = colorResource(R.color.surface),
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                        val required = requiredInitsList[it]
                        PrepareItem(
                            title = stringResource(required.title),
                            subtitle = stringResource(required.message)
                        ) {
                            val granted = takePermission(context, required)
                            requiredInits[required] = granted
                            return@PrepareItem granted
                        }
                    }
                }
            }
        }
    }
}

private fun finish(context: Context) {
    context.startActivity(
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

private fun takePermission(context: Context, key: RequiredInit): Boolean =
    when (key) {
        RequiredInit.NotificationPermission -> takeNotificationPermission(context as PrepareActivity)
        RequiredInit.BatteryOptimization -> takeBatteryOptimizationPermission(context)
    }

@SuppressLint("BatteryLife")
fun takeBatteryOptimizationPermission(context: Context): Boolean {
    startActivity(
        context,
        Intent(
            Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Uri.parse("package:${context.packageName}")
        ),
        null
    )
    return isIgnoringBatteryOptimization(context)
}

private fun takeNotificationPermission(context: PrepareActivity): Boolean {
    ActivityCompat.requestPermissions(
        context,
        arrayOf(PulseService.POST_NOTIFICATIONS),
        R.id.persistent_notification
    )
    val granted = ActivityCompat.checkSelfPermission(
        context,
        "android.permission.POST_NOTIFICATIONS"
    ) == PackageManager.PERMISSION_GRANTED

    if (granted) PulseService.setupNotificationChannel(context)
    return granted
}

@Preview
@Composable
private fun PrepareActivityDefaultPreview() {
    val requiredInits = mutableListOf(
        RequiredInit.NotificationPermission,
        RequiredInit.BatteryOptimization,
    )
    GalacticTheme {
        Scaffold(
            topBar = {
                TopBar(stringResource(R.string.prepare_activity_title))
            }, bottomBar = {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = colorResource(R.color.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            Text(stringResource(R.string.done))
                        }
                    }
                }
            }
        ) { scaffoldPadding ->
            Surface(
                color = colorResource(R.color.background),
                border = BorderStroke(2.dp, colorResource(R.color.surface)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(scaffoldPadding)
            ) {
                LazyColumn {
                    items(requiredInits.size) {
                        if (it > 0) {
                            Divider(
                                color = colorResource(R.color.surface),
                                modifier = Modifier.padding(horizontal = 20.dp),
                            )
                        }
                        val required = requiredInits[it]
                        PrepareItem(
                            title = stringResource(required.title),
                            subtitle = stringResource(required.message),
                        ) {
                            val granted = false
                            return@PrepareItem granted
                        }
                    }
                }
            }
        }
    }
}