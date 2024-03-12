package com.galacticai.networkpulse.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.util.Consistent
import com.galacticai.networkpulse.ui.main.screens.MainScreen

@Composable
fun MainNavigation(controller: NavHostController) {
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        shape = Consistent.shape,
        modifier = Modifier.padding(10.dp),
    ) {
        NavigationBar(
            containerColor = colorResource(R.color.surface).copy(.8f),
            contentColor = colorResource(R.color.onSurface),
        ) {
            for (screen in MainScreen.entries) {
                val label = stringResource(screen.labelID)
                NavigationBarItem(
                    selected = currentRoute == screen.route,
                    icon = { Icon(screen.icon, contentDescription = label) },
                    label = { Text(label) },
                    onClick = {
                        controller.navigate(screen.route) {
                            popUpTo(controller.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}