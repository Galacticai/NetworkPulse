package com.galacticai.networkpulse.ui

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.isIgnoringBatteryOptimization
import com.galacticai.networkpulse.services.PulseService
import com.galacticai.networkpulse.ui.prepare.PrepareItem
import com.galacticai.networkpulse.ui.theme.GalacticTheme
import com.galacticai.networkpulse.util.RequiredInit
import com.galacticai.networkpulse.util.getRequiredInits


class PrepareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PrepareActivityContent(this) }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrepareActivityContent(context: PrepareActivity) {

    val requiredInitsList = getRequiredInits(LocalContext.current)
    if (requiredInitsList.isEmpty()) {
        finish(context)
        return
    }

    val requiredInits = requiredInitsList
        .associateWith { false }
        .toMutableMap()

    fun isReady() = requiredInits.all { it.value }
    fun finishIfReady() {
        if (isReady()) finish(context)
    }

    GalacticTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(stringResource(R.string.prepare_activity_title))
                })
            }, bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = { finishIfReady() }) {
                            Text(stringResource(R.string.done))
                        }
                    }
                }
            }
        ) { scaffoldPadding ->
            Column(modifier = Modifier.padding(scaffoldPadding)) {
                Divider(
                    modifier = Modifier
                        .size(width = 64.dp, height = 2.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )


                LazyColumn {
                    for (required in requiredInits) {
                        item {
                            PrepareItem(
                                title = stringResource(required.key.title),
                                subtitle = stringResource(required.key.message)
                            ) {
                                val granted = takePermission(context, required.key)
                                requiredInits[required.key] = granted
                                return@PrepareItem granted
                            }
                        }
                    }
                }

                Divider(
                    modifier = Modifier
                        .size(width = 64.dp, height = 2.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private fun finish(context: PrepareActivity) {
    context.startActivity(
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

private fun takePermission(context: PrepareActivity, key: RequiredInit): Boolean =
    when (key) {
        RequiredInit.NotificationPermission -> takeNotificationPermission(context)
        RequiredInit.BatteryOptimization -> takeBatteryOptimizationPermission(context)
    }

@SuppressLint("BatteryLife")
fun takeBatteryOptimizationPermission(context: PrepareActivity): Boolean {
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PrepareActivityDefaultPreview() {
    GalacticTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Text(stringResource(R.string.prepare_activity_title))
                })
            }, bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(onClick = {
                            /*TODO*/
                        }) {
                            Text(stringResource(R.string.done))
                        }
                    }
                }
            }
        ) { scaffoldPadding ->
            Column(modifier = Modifier.padding(scaffoldPadding)) {
                Divider(
                    modifier = Modifier
                        .size(width = 64.dp, height = 2.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Row {
                    for (required in listOf(
                        RequiredInit.BatteryOptimization,
                        RequiredInit.NotificationPermission
                    )) {
                        PrepareItem(
                            title = stringResource(required.title),
                            subtitle = stringResource(required.message),
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .size(width = 64.dp, height = 2.dp)
                        .padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}