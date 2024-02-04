package com.galacticai.networkpulse.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.galacticai.networkpulse.common.ui.CubicChartData
import com.galacticai.networkpulse.common.ui.CubicChartItem
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.services.PulseService
import com.galacticai.networkpulse.ui.common.AppTitle
import com.galacticai.networkpulse.ui.main.HourGraph
import com.galacticai.networkpulse.ui.main.MainNavigation
import com.galacticai.networkpulse.ui.main.RecordsList
import com.galacticai.networkpulse.ui.main.screens.DashboardScreen
import com.galacticai.networkpulse.ui.main.screens.MainScreen
import com.galacticai.networkpulse.ui.main.screens.OverviewScreen
import com.galacticai.networkpulse.ui.main.screens.SettingsScreen
import com.galacticai.networkpulse.ui.theme.GalacticTheme
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Date
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainActivityContent(this) }

        EventBus.getDefault().register(this)
        PulseService.startIfNotRunning(this)

        val values = getChartValuesFromDB()
        chartValues.addAll(values)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    val dao get() = LocalDatabase.getDBMainThread(this).speedRecordsDAO()
    fun getChartValuesFromDB(): List<SpeedRecord> =
        dao.getAfter(chartAfterTime)

    fun getAllValuesFromDB(): List<SpeedRecord> =
        dao.getAll()

    val chartValues = mutableStateListOf<SpeedRecord>()
    val chartData
        get() = CubicChartData(chartValues.map {
            CubicChartItem(
                formatTimeAgo(it.time),
                it.down ?: 0f
            )
        })

    @Subscribe
    fun onPulseDone(ev: PulseService.DoneEvent) {
        chartValues.retainAll { it.time >= chartAfterTime }
        if (chartValues.size >= 30) chartValues.removeAt(0)
        chartValues.add(ev.timedSpeedRecord)
        Log.d(
            "PulseService",
            "Pulse done (d=${ev.timedSpeedRecord.down}, t=${ev.timedSpeedRecord.time})"
        )
    }

    companion object {
        val chartAfterTime
            get() = System.currentTimeMillis() - (1000 * 60 * 5)

        fun formatTimeAgo(date: Long?): String {
            if (date == null) return "-"

            val timeDifferenceMillis = System.currentTimeMillis() - date

            val seconds = TimeUnit.MILLISECONDS.toSeconds(timeDifferenceMillis)
            if (seconds < 60) return "${seconds}s"

            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifferenceMillis)
            return "${minutes}m"
        }
    }
}

@Composable
fun MainActivityContent(mainActivity: MainActivity) {
    val navController = rememberNavController()

    GalacticTheme {
        Scaffold(bottomBar = {
            MainNavigation(navController)
        }) { scaffoldPadding ->
            NavHost(
                navController = navController,
                startDestination = MainScreen.Overview.route,
                modifier = Modifier.padding(scaffoldPadding)
            ) {
                composable(MainScreen.Overview.route) { OverviewScreen(mainActivity) }
                composable(MainScreen.Dashboard.route) { DashboardScreen(mainActivity) }
                composable(MainScreen.Settings.route) { SettingsScreen(mainActivity) }
            }
        }
    }
}


@Preview
@Composable
fun MainActivityDefaultPreview() {
    val navController = rememberNavController()
    GalacticTheme {
        Scaffold(bottomBar = {
            MainNavigation(navController)
        }) { scaffoldPadding ->
            Column(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .padding(10.dp)
            ) {
                AppTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                )
                HourGraph(
                    data = CubicChartData.fromItems(
                        CubicChartItem("1s", 100f),
                        CubicChartItem("5s", 223f),
                        CubicChartItem("10s", 123f),
                        CubicChartItem("15s", 223f),
                        CubicChartItem("20s", 23f),
                        CubicChartItem("30s", 13f),
                        CubicChartItem("1m", 100f),
                    )
                )
                Text("Records in the last 5m: (10)")
                RecordsList(
                    records = listOf(
                        SpeedRecord(
                            Date().time - 0 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 1 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            0f
                        ),
                        SpeedRecord(
                            Date().time - 1 * 6 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            15f
                        ),
                        SpeedRecord(
                            Date().time - 4 * 60 * 1100,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            9f
                        ),
                        SpeedRecord(
                            Date().time - 2 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1000,
                            SpeedRecord.Status.Error.toInt(),
                            2382,
                            0f,
                            5f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1400,
                            SpeedRecord.Status.Timeout.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1001,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            3f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1010,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            7f
                        ),
                        SpeedRecord(
                            Date().time - 0 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 1 * 60 * 1200,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            0f
                        ),
                        SpeedRecord(
                            Date().time - 1 * 60 * 2000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            15f
                        ),
                        SpeedRecord(
                            Date().time - 4 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            9f
                        ),
                        SpeedRecord(
                            Date().time - 2 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            5f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 140,
                            SpeedRecord.Status.Timeout.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1000,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            10f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1500,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            3f
                        ),
                        SpeedRecord(
                            Date().time - 5 * 60 * 1100,
                            SpeedRecord.Status.Success.toInt(),
                            2382,
                            10f,
                            7f
                        ),
                    ),
                    modifier = Modifier.padding(10.dp)
                )
                Button(onClick = {

                }) {
                    Text("Export CSV")
                }
                Button(onClick = {

                }) {
                    Text("Clear database")
                }
            }
        }
    }
}