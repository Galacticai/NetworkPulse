package com.galacticai.networkpulse.ui.main.old

import androidx.annotation.IdRes
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.ui.main.old.fragments.DashboardFragment
import com.galacticai.networkpulse.ui.main.old.fragments.OverviewFragment
import com.galacticai.networkpulse.ui.main.old.fragments.SettingsFragment

enum class MainFragmentName(val index: Int) {
    Overview(0),
    Dashboard(1),
    Settings(2);

    companion object {
        fun fromInt(value: Int) = MainFragmentName.entries.first { it.index == value }

        fun fromId(@IdRes id: Int) = when (id) {
            R.id.action_overview -> Overview
            R.id.action_dashboard -> Dashboard
            R.id.action_settings -> Settings
            else -> throw IllegalArgumentException("id does not correspond to a MainFragmentName")
        }

        fun idFromName(name: MainFragmentName) = when (name) {
            Overview -> R.id.action_overview
            Dashboard -> R.id.action_dashboard
            Settings -> R.id.action_settings
        }

        fun idFromIndex(index: Int) = idFromName(fromInt(index))

        fun fragmentFromName(name: MainFragmentName) = when (name) {
            Overview -> OverviewFragment()
            Dashboard -> DashboardFragment()
            Settings -> SettingsFragment()
        }
    }
}