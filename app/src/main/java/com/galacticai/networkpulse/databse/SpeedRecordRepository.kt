package com.galacticai.networkpulse.databse

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.asLiveData
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils.sorted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.SortedSet

/** Wrapper for [SpeedRecordsDAO] */
class SpeedRecordRepository(
    private val dao: SpeedRecordsDAO,
    private val context: Context,
    private val viewModelScope: CoroutineScope
) {
    fun insert(vararg record: SpeedRecord) = dao.insert(*record)
    fun delete(vararg record: SpeedRecord) = dao.delete(*record)

    val countAllLive = dao.countAll()
        .asLiveData(viewModelScope.coroutineContext)

    private val _newestTimeLive = dao.getNewestTime()
        .asLiveData(viewModelScope.coroutineContext)

    fun getBetweenLive(from: Long, to: Long) = dao.getBetween(from, to)
        .asLiveData(viewModelScope.coroutineContext)

    fun daysLive(offsetMS: Int) = dao.getDays(offsetMS)
        .asLiveData(viewModelScope.coroutineContext)

    /**? Because MediatorLiveData has no way to check for observers (long story) */
    @Composable
    fun rememberRecentRecords(): State<SortedSet<SpeedRecord>> {
        val recentTimeDelta = 1000 * 60 * 60 * 2
        val to by _newestTimeLive.observeAsState(System.currentTimeMillis())
        val from by remember(to, recentTimeDelta) { derivedStateOf { to - recentTimeDelta } }
        var records by rememberSaveable { mutableStateOf<List<SpeedRecord>>(emptyList()) }

        LaunchedEffect(from) { //? "to" key is already in the state of "from"
            viewModelScope.launch {
                dao.getBetweenLastDescending(from, to, 2000)
                    .collect { records = it }
            }
        }
        return remember(records) { derivedStateOf { records.sorted() } }
    }

    //private var _recentRecordsTime = mutableIntStateOf(Setting.RecentRecordsTime.defaultValue)
    //val recentRecordsLive = MediatorLiveData<List<SpeedRecord>>()
    //val daysAndCountLive = MediatorLiveData<Map<Long, Int>>()

    //    private val recentRecordsObserver = Observer<Long> {
    //        //!? can be optimized to add last one instead of getting all recent ones,
    //        //!? but that requires too much stuff and this is fast enough already
    //        coroutineScope.launch {
    //            val from = it - _recentRecordsTime.intValue
    //            val nowUTC = System.currentTimeMillis().toUTC()
    //            val records = dao.getBetweenLast(from, nowUTC, 2000)
    //                .lastOrNull().orEmpty()
    //            recentRecordsLive.postValue(records)
    //        }
    //    }

    //    private val daysAndCountObserver = Observer<List<DayRange>> {
    //        viewModelScope.launch {
    //            val value = it.reversed().associate {
    //                val count = dao.countBetween(it.first, it.last)
    //                    .lastOrNull() ?: 0
    //                return@associate it.first to count
    //            }
    //            daysAndCountLive.postValue(value)
    //        }
    //    }

    //    init {
    ////        coroutineScope.launch {
    ////            _recentRecordsTime.intValue = Setting.RecentRecordsTime.get(context)
    ////        }
    //        addSources()
    //    }

    //    private fun addSources() {
    //        try {
    //            recentRecordsLive.addSource(_newestTimeLive, recentRecordsObserver)
    //            daysAndCountLive.addSource(daysAndCountLive, daysAndCountObserver)
    //        } catch (_: Exception) { //? Already added, cant check if already added beforehand. so here we go
    //        }
    //    }
}
