package com.galacticai.networkpulse.databse

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import com.galacticai.networkpulse.common.toUTC
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.models.settings.Setting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch

/** Wrapper for [SpeedRecordsDAO] */
class SpeedRecordRepository(
    private val dao: SpeedRecordsDAO,
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    fun delete(vararg record: SpeedRecord) = dao.delete(*record)

    val countAllLive = dao.countAll()
        .asLiveData(coroutineScope.coroutineContext)

    private val newestTimeLive = dao.getNewestTime()
        .asLiveData(coroutineScope.coroutineContext)

    fun getBetweenLive(from: Long, to: Long) = dao.getBetween(from, to)
        .asLiveData(coroutineScope.coroutineContext)

    val daysLive = dao.getDays()
        .asLiveData(coroutineScope.coroutineContext)

    val daysAndCountLive = MediatorLiveData<Map<Long, Int>>()

    private var recentRecordsTime = mutableIntStateOf(Setting.RecentRecordsTime.defaultValue)
    val recentRecordsLive = MediatorLiveData<List<SpeedRecord>>()

    init {
        coroutineScope.launch {
            recentRecordsTime.intValue = Setting.RecentRecordsTime.get(context)
        }

        recentRecordsLive.addSource(newestTimeLive) {
            coroutineScope.launch {
                val from = it - recentRecordsTime.intValue
                val nowUTC = System.currentTimeMillis().toUTC()
                val recordsLive = getBetweenLive(from, nowUTC)
                recentRecordsLive.value = recordsLive.value
            }
        }

        daysAndCountLive.addSource(daysLive) {
            coroutineScope.launch {
                daysAndCountLive.value = it.reversed().associate {
                    val count = dao.countBetween(it.first, it.last)
                        .lastOrNull() ?: 0
                    it.first to count
                }
            }
        }
    }
}
