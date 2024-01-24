package com.galacticai.networkpulse

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.viewpager2.widget.ViewPager2
import com.galacticai.networkpulse.common.ZoomOutPageTransformer
import com.galacticai.networkpulse.common.isIgnoringBatteryOptimization
import com.galacticai.networkpulse.receivers.BootReceiver
import com.galacticai.networkpulse.services.PulseService
import com.galacticai.networkpulse.ui.main.old.MainFragmentName
import com.galacticai.networkpulse.ui.main.old.MainPagerAdapter
import com.github.mikephil.charting.BuildConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivityOld : AppCompatActivity() {
    private lateinit var pager: ViewPager2
    private lateinit var nav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PulseService.start(this)
        initValues()
        initNav()
        initPager()
        initBack()
        initBootReceiver()
        initBatteryOptimization()
        initNotificationPermission()
    }

    companion object {
        const val BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
        const val QUICKBOOT_POWERON = "android.intent.action.QUICKBOOT_POWERON"
        const val LOCKED_BOOT_COMPLETED = "android.intent.action.LOCKED_BOOT_COMPLETED"
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun initBootReceiver() {
        val bootReceiver = BootReceiver()
        val bootIntentFilter: IntentFilter = IntentFilter().apply {
            addAction(BOOT_COMPLETED)
            addAction(QUICKBOOT_POWERON)
            addAction(LOCKED_BOOT_COMPLETED)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            registerReceiver(bootReceiver, bootIntentFilter, RECEIVER_EXPORTED)
        else registerReceiver(bootReceiver, bootIntentFilter)
    }


    private fun initValues() {
        //? Must be done first and together
        //? otherwise the pager/nav have a tiny time window with broken references to each others
        nav = findViewById(R.id.dashboardNavigation)
        pager = findViewById(R.id.mainPager)
    }

    private fun initNav() {
        nav.setOnItemSelectedListener(::onNavItemSelected)
    }

    private fun initPager() {
        pager.setPageTransformer(ZoomOutPageTransformer())
        pager.adapter = MainPagerAdapter(this)
        pager.registerOnPageChangeCallback(OnPageChangeCallback(this))
    }

    private fun initBack() {
        fun back() {
            val overviewI = MainFragmentName.Overview.index
            if (pager.currentItem == overviewI) {
                //? Just for testing
                if (BuildConfig.DEBUG) {
                    val intent = Intent(this, PrepareActivityOld::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    return
                }
                finish()
            } else pager.currentItem = overviewI
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() = back()
            })
        } else {
            onBackPressedDispatcher.addCallback { back() }
        }
    }

    @SuppressLint("BatteryLife")
    private fun initBatteryOptimization() {
        if (isIgnoringBatteryOptimization(this)) return

        MaterialAlertDialogBuilder(this).apply {
            setIcon(
                AppCompatResources.getDrawable(
                    this@MainActivityOld,
                    R.drawable.baseline_battery_saver_64
                )
            )
            setTitle(getString(R.string.battery_optimization_title))
            setMessage(getString(R.string.battery_optimization_text))
            setNegativeButton("${getString(R.string.skip)} (${getString(R.string.not_recommended)})") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(R.string.ok) { dialog, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:${this@MainActivityOld.packageName}")
                    )
                )
                if (isIgnoringBatteryOptimization(this@MainActivityOld))
                    dialog.dismiss()
            }
            show()
        }
    }

    private fun initNotificationPermission() {
        val permission =
            ActivityCompat.checkSelfPermission(applicationContext, PulseService.POST_NOTIFICATIONS)

        if (permission == PackageManager.PERMISSION_GRANTED) return

        MaterialAlertDialogBuilder(this).apply {
            setIcon(AppCompatResources.getDrawable(context, R.drawable.baseline_notifications_24))
            setTitle(getString(R.string.notification_permission_title))
            setMessage(getString(R.string.notification_permission_text))
            setNegativeButton(R.string.skip) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(R.string.enable) { dialog, _ ->
                ActivityCompat.requestPermissions(
                    this@MainActivityOld,
                    arrayOf(PulseService.POST_NOTIFICATIONS),
                    R.id.persistent_notification
                )
                dialog.dismiss()
            }
            show()
        }
    }

    private fun onNavItemSelected(menuItem: MenuItem): Boolean {
        pager.setCurrentItem(
            MainFragmentName.fromId(menuItem.itemId).index,
            true
        )
        return true
    }

    private class OnPageChangeCallback(private val mainActivity: MainActivityOld) //
    /* */ : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mainActivity.nav.selectedItemId = MainFragmentName.idFromIndex(position)
        }
    }

}