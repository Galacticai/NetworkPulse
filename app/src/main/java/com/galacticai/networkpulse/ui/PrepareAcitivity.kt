package com.galacticai.networkpulse.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.isIgnoringBatteryOptimization
import com.galacticai.networkpulse.common.ui.CheckItem
import com.galacticai.networkpulse.ui.common.ConfirmationButtons
import com.galacticai.networkpulse.ui.common.Consistent
import com.galacticai.networkpulse.ui.common.Consistent.screenHPadding
import com.galacticai.networkpulse.ui.common.Grants
import com.galacticai.networkpulse.ui.common.ScreenTitle
import com.galacticai.networkpulse.ui.theme.GalacticTheme

class PrepareActivity : AppCompatActivity() {
    private lateinit var launcherBatteryOptimization: ActivityResultLauncher<Intent>

    private var doneNotificationState = mutableStateOf(false)
    private var doneBatteryOptimizationState = mutableStateOf(false)
    private val isReady = derivedStateOf { doneNotificationState.value && doneBatteryOptimizationState.value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup() //? before showing the activity, to allow instant skipping
        setContent { PrepareActivityContent() }
    }


    private fun setup() {
        doneNotificationState.value = Grants.PersistentNotification.isGranted(this)
        doneBatteryOptimizationState.value = isIgnoringBatteryOptimization(this)
        if (isReady.value) done()

        launcherBatteryOptimization = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            //! if (it.resultCode == Activity.RESULT_OK)
            //? result code is always 0 for battery optimization, so it must be checked manually
            doneBatteryOptimizationState.value =
                isIgnoringBatteryOptimization(this@PrepareActivity)
        }
    }

    private fun done(force: Boolean = false) {
        if (!isReady.value && !force) return
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == R.id.persistent_notification_request) {
            doneNotificationState.value = (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        }
    }

    @Preview
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    private fun PrepareActivityContent() {
        val context = LocalContext.current
        var size by remember { mutableStateOf(IntSize(1, 1)) }

        val doneNotification by remember { doneNotificationState }
        val doneBatteryOptimization by remember { doneBatteryOptimizationState }
        val isReady by remember(doneNotification, doneBatteryOptimization) {
            derivedStateOf { doneNotification && doneBatteryOptimization }
        }

        GalacticTheme {
            Scaffold {
                val primaryContainer = colorResource(R.color.primaryContainer)
                Box(
                    Modifier
                        .onGloballyPositioned { coordinates -> size = coordinates.size }
                        .background(
                            Brush.linearGradient(
                                start = Offset.Zero,
                                end = Offset(0f, size.height.toFloat()),
                                colorStops = arrayOf(
                                    0f to Color.Transparent,
                                    .025f to primaryContainer.copy(.1f),
                                    .2f to primaryContainer.copy(.15f),
                                    .4f to primaryContainer,
                                    1f to Color.Transparent,
                                )
                            )
                        )
                ) {
                    Column(Modifier.padding(it)) {
                        val colors = listOf(
                            colorResource(R.color.secondaryContainer).copy(.5f),
                            Color.Transparent
                        )

                        fun Modifier.gradient(x: Float, y: Float) =
                            this.background(
                                Brush.radialGradient(
                                    center = Offset(x, y),
                                    colors = colors
                                )
                            )
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .weight(.5f)
                                .gradient(size.width / 4f, size.height / 7f)
                                .gradient(size.width - size.width / 3.5f, size.height / 6f)
                                .gradient(size.width / 2f, size.height / 5f)
                                .background(
                                    Brush.linearGradient(
                                        end = Offset(0f, size.height.toFloat()),
                                        colorStops = arrayOf(
                                            0f to colorResource(R.color.background),
                                            .1f to Color.Transparent,
                                            .5f to Color.Transparent,
                                            1f to colorResource(R.color.background)
                                        )
                                    )
                                )
                        ) {
                            ScreenTitle(
                                stringResource(R.string.prepare_activity_title),
                                Modifier
                                    .align(Alignment.Center)
                                    .padding(20.dp),
                                color = colorResource(R.color.onSecondaryContainer),
                            )
                        }
                        Surface(
                            Modifier.screenHPadding(),
                            color = colorResource(R.color.background),
                            shape = Consistent.shape,
                            shadowElevation = 10.dp,
                        ) {
                            Column {
                                CheckItem(
                                    modifier = Modifier.padding(10.dp),
                                    title = stringResource(R.string.notification_permission_title),
                                    subtitle = stringResource(R.string.notification_permission_text),
                                    checkState = doneNotification,
                                ) {
                                    if (doneNotification) return@CheckItem false
                                    //? set will happen in PrepareActivity.onRequestPermissionsResult
                                    Grants.PersistentNotification.setupAndGrant(context as PrepareActivity)
                                    false
                                }
                                Divider(Modifier.padding(horizontal = 10.dp))
                                CheckItem(
                                    modifier = Modifier.padding(10.dp),
                                    title = stringResource(R.string.battery_optimization_title),
                                    subtitle = stringResource(R.string.battery_optimization_text),
                                    checkState = doneBatteryOptimization,
                                ) {
                                    if (doneBatteryOptimization) return@CheckItem false
                                    val activity = context as PrepareActivity
                                    Grants.BatteryOptimization.grant(
                                        activity,
                                        activity.launcherBatteryOptimization
                                    )
                                    false
                                }
                            }
                        }

                        Spacer(Modifier.weight(.5f))

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 15.dp)
                        ) {
                            AnimatedVisibility(visible = !isReady) {
                                ConfirmationButtons(stringResource(R.string.skip)) {
                                    done(force = true)
                                }

                                
                            }
                            Spacer(Modifier.weight(1f))
                            Button(
                                enabled = isReady,
                                onClick = { done() },
                            ) {
                                Text(stringResource(R.string.continue_))
                            }
                        }
                    }
                }
            }
        }
    }
}