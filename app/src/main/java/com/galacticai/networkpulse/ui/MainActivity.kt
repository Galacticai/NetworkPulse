package com.galacticai.networkpulse.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.galacticai.networkpulse.services.PulseService
import com.galacticai.networkpulse.ui.main.MainNavigation
import com.galacticai.networkpulse.ui.main.screens.DashboardScreen
import com.galacticai.networkpulse.ui.main.screens.MainScreen
import com.galacticai.networkpulse.ui.main.screens.OverviewScreen
import com.galacticai.networkpulse.ui.main.screens.SettingsScreen
import com.galacticai.networkpulse.ui.theme.GalacticTheme
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MainActivityContent() }
        viewModel.init()
    }

    override fun onDestroy() {
        viewModel.end()
        super.onDestroy()
    }


    @Subscribe
    fun onPulseDone(ev: PulseService.PulseEvent.DoneEvent) {
        //viewModel.rollRecentRecords(ev.record)
        Log.d(
            "PulseService",
            "Pulse done (d=${ev.record.down}, t=${ev.record.time}, r=${ev.record.runtimeMS})"
        )
    }

    @Subscribe
    fun onPulseError(ev: PulseService.PulseEvent.ErrorEvent) {
        //viewModel.rollRecentRecords(ev.record)
        Log.d(
            "PulseService",
            "Pulse error (e=${ev.error.message}, t=${ev.record.time}, r=${ev.runtime})"
        )
    }
}

@Composable
private fun MainActivityContent() {
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