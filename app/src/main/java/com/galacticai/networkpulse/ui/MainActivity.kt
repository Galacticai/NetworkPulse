package com.galacticai.networkpulse.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.galacticai.networkpulse.common.ui.CubicChartData
import com.galacticai.networkpulse.common.ui.CubicChartItem
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
import org.greenrobot.eventbus.Subscribe
import java.util.Date

class MainActivity : AppCompatActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainActivityContent() }
        viewModel.init(this)
    }

    override fun onDestroy() {
        viewModel.onDestroy(this)
        super.onDestroy()
    }


    @Subscribe
    fun onPulseDone(ev: PulseService.PulseEvent.DoneEvent) {
        viewModel.rollRecentRecords(ev.record)
        Log.d(
            "PulseService",
            "Pulse done (d=${ev.record.down}, t=${ev.record.time}, r=${ev.record.runtimeMS})"
        )
    }

    @Subscribe
    fun onPulseError(ev: PulseService.PulseEvent.ErrorEvent) {
        viewModel.rollRecentRecords(ev.record)
        Log.d(
            "PulseService",
            "Pulse error (e=${ev.error.message}, t=${ev.record.time}, r=${ev.runtime})"
        )
    }

}


//class MainActivity : AppCompatActivity() {
//    private val viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent { MainActivityContent() }
//
//        runBlocking {
//            val start = Setting.EnablePulseService.get(this@MainActivity)
//            if (!start) return@runBlocking
//            PulseService.startIfNotRunning(this@MainActivity)
//            EventBus.getDefault().register(this@MainActivity)
//            startedService = true
//        }
//        val values = getChartValuesFromDB()
//        recentRecords.addAll(values)
//        await { recentRecordsTime = Setting.RecentRecordsTime.get(this) }
//    }
//
//    override fun onDestroy() {
//        if (startedService) EventBus.getDefault().unregister(this)
//        super.onDestroy()
//    }
//
//    private var startedService: Boolean = false
//    private var recentRecordsTime = Setting.RecentRecordsTime.defaultValue
//    private val dao get() = LocalDatabase.getDBMainThread(this).speedRecordsDAO()
//    private val recentRecordsAfterTime get() = dao.getNewestTime() - recentRecordsTime
//    private fun getChartValuesFromDB(): List<SpeedRecord> =
//        dao.getBetween(recentRecordsAfterTime, System.currentTimeMillis())
//
//    private val recentRecords = mutableListOf<SpeedRecord>()
//    val recentRecordsLive = MutableLiveData<List<SpeedRecord>>(emptyList())
//
//    private fun rollRecentRecords(record: SpeedRecord) {
//        recentRecords.retainAll { it.time >= recentRecordsAfterTime }
//        if (recentRecords.size >= 1000) recentRecords.removeAt(0)
//        recentRecords.add(record)
//        MainScope().launch { recentRecordsLive.value = recentRecords }
//    }
//
//    @Subscribe
//    fun onPulseDone(ev: PulseService.PulseEvent.DoneEvent) {
//        rollRecentRecords(ev.record)
//        Log.d(
//            "PulseService",
//            "Pulse done (d=${ev.record.down}, t=${ev.record.time}, r=${ev.record.runtimeMS})"
//        )
//    }
//
//    @Subscribe
//    fun onPulseError(ev: PulseService.PulseEvent.ErrorEvent) {
//        rollRecentRecords(ev.record)
//        Log.d(
//            "PulseService",
//            "Pulse error (e=${ev.error.message}, t=${ev.record.time}, r=${ev.runtime})"
//        )
//    }
//}

@Composable
fun MainActivityContent() {
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
                composable(MainScreen.Overview.route) { OverviewScreen() }
                composable(MainScreen.Dashboard.route) { DashboardScreen() }
                composable(MainScreen.Settings.route) { SettingsScreen() }
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
                    modifier = Modifier.padding(10.dp),
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
                    reversed = true
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