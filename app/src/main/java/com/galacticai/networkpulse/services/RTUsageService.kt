package com.galacticai.networkpulse.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class RTUsageService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}