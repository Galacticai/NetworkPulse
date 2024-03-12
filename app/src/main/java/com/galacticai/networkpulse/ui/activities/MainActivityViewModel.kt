package com.galacticai.networkpulse.ui.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.SpeedRecordRepository
import com.galacticai.networkpulse.models.settings.Setting
import com.galacticai.networkpulse.services.PulseService
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch

class MainActivityViewModel(private val application: Application) :
    AndroidViewModel(application),
    DisposableHandle {

    val repo = SpeedRecordRepository(
        LocalDatabase.getDB(application).speedRecordsDAO(),
        application,
        viewModelScope
    )

    var startedPulseService = false
        private set

    fun init() = viewModelScope.launch {
        val start = Setting.EnablePulseService.get(application)
        if (start) {
            PulseService.start(application)
            startedPulseService = true
        }
    }


    override fun dispose() {
        startedPulseService = false
    }

    //    fun rollRecentRecords(record: SpeedRecord) {
    //        val records = recentRecords.value
    //            .orEmpty()
    //            .trimLeadingToSize(2000)
    //            .toMutableList()
    //        val nowUTC = System.currentTimeMillis().toUTC()
    //            .toUTC() //? compare all in utc instead of converting every one to system time
    //        records.retainAll { it.time >= nowUTC - recentRecordsTime }
    //        records.add(record)
    //        recentRecords.postValue(records)
    //    }
}

//fun <T> List<T>.trimLeadingToSize(size: Int): List<T> {
//    require(size >= 0) { "Size must be non-negative: size=$size" }
//    return if (size >= this.size) this
//    else this.subList(this.size - size, this.size)
//}