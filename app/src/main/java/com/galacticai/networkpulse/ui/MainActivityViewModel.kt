package com.galacticai.networkpulse.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.SpeedRecordsDAO
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.services.PulseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class MainActivityViewModel : ViewModel() {
    lateinit var dao: SpeedRecordsDAO
    var recentRecordsTime = Setting.RecentRecordsTime.defaultValue
    val recentRecords = MutableLiveData<List<SpeedRecord>>(emptyList())
    var startedService: Boolean = false

    fun init(activity: MainActivity) {
        dao = LocalDatabase.getDBMainThread(activity).speedRecordsDAO()
        viewModelScope.launch {
            val values = getChartValuesFromDB()
            recentRecords.value = values
            recentRecordsTime = Setting.RecentRecordsTime.get(activity)

            val start = Setting.EnablePulseService.get(activity)
            if (start) {
                PulseService.startIfNotRunning(activity)
                EventBus.getDefault().register(activity)
                startedService = true
            }
        }
    }

    fun onDestroy(activity: MainActivity) {
        if (startedService) EventBus.getDefault().unregister(activity)
    }

    private suspend fun getChartValuesFromDB(): List<SpeedRecord> =
        withContext(Dispatchers.IO) {
            val recentRecordsAfterTime = dao.getNewestTime() - recentRecordsTime
            dao.getBetween(recentRecordsAfterTime, System.currentTimeMillis())
        }

    fun rollRecentRecords(record: SpeedRecord) {
        val records = recentRecords.value
            .orEmpty()
            .trimLeadingToSize(2000)
            .toMutableList()
        records.retainAll { it.time >= recentRecordsTime }
        records.add(record)
        recentRecords.postValue(records)
    }
}

fun <T> List<T>.trimLeadingToSize(size: Int): List<T> {
    require(size >= 0) { "Size must be non-negative" }
    return if (size >= this.size) this
    else this.subList(this.size - size, this.size)
}