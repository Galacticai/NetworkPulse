package com.galacticai.networkpulse.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.galacticai.networkpulse.common.toUTC
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.SpeedRecordRepository
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.services.PulseService
import kotlinx.coroutines.launch

class MainActivityViewModel(private val application: Application) : AndroidViewModel(application) {
    val repo = SpeedRecordRepository(
        LocalDatabase.getDB(application).speedRecordsDAO(),
        application,
        viewModelScope
    )

    private var recentRecordsTime = Setting.RecentRecordsTime.defaultValue
    private var startedPulseService = false
    val recentRecords = MutableLiveData<List<SpeedRecord>>(emptyList())

    fun init() = viewModelScope.launch {
        val start = Setting.EnablePulseService.get(application)
        if (start) {
            PulseService.startIfNotRunning(application)
            //EventBus.getDefault().register(application)
            startedPulseService = true
        }
    }

    fun end( ) = viewModelScope.launch {
        if (startedPulseService) {
            //EventBus.getDefault().unregister(activity)
            startedPulseService = false
        }
    }

    fun rollRecentRecords(record: SpeedRecord) {
        val records = recentRecords.value
            .orEmpty()
            .trimLeadingToSize(2000)
            .toMutableList()
        val nowUTC = System.currentTimeMillis().toUTC()
            .toUTC() //? compare all in utc instead of converting every one to system time
        records.retainAll { it.time >= nowUTC - recentRecordsTime }
        records.add(record)
        recentRecords.postValue(records)
    }
}

fun <T> List<T>.trimLeadingToSize(size: Int): List<T> {
    require(size >= 0) { "Size must be non-negative: size=$size" }
    return if (size >= this.size) this
    else this.subList(this.size - size, this.size)
}