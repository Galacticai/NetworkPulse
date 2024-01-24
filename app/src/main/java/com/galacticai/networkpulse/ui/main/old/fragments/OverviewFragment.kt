package com.galacticai.networkpulse.ui.main.old.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.models.sql.CircularList
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecordEntity
import com.galacticai.networkpulse.services.PulseService
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.Date


class OverviewFragment : Fragment() {

    private lateinit var view: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_overview, container, false)
        EventBus.getDefault().register(this)
        initChart()
        return view
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    data class RadarSpeedEntry(val up: RadarEntry, val down: RadarEntry)

    private val hourChartEntries: CircularList<RadarSpeedEntry> = CircularList(60)
    private val hourChartUpEntries get() = hourChartEntries.map { it.up }
    private val hourChartDownEntries get() = hourChartEntries.map { it.down }

    @Subscribe
    fun onPulseDone(ev: PulseService.DoneEvent) {
        hourChartEntries.add(
            RadarSpeedEntry(
                RadarEntry(ev.timedSpeedRecord.up),
                RadarEntry(ev.timedSpeedRecord.down)
            )
        )
        updateChart()
    }

    @Subscribe
    fun onPulseOther(ev: PulseService.OtherEvent) {
        Log.d("PulseService", ev.response?.toString() ?: "no response")
        hourChartEntries.add(
            RadarSpeedEntry(
                RadarEntry(if (Date().time % 2 == 0L) 10f else 0f),
                RadarEntry(if (Date().time % 2 != 0L) 10f else 0f)
            )
        )
        updateChart()
    }

    @Subscribe
    fun onPulseError(ev: PulseService.ErrorEvent) {
        Log.e("PulseService", ev.error.message.toString())
    }

    private lateinit var hourChart: RadarChart
    private fun initChart() {
        hourChart = view.findViewById<RadarChart>(R.id.hourChart).apply {
            setBackgroundColor(view.context.getColor(R.color.background))
            description.isEnabled = false
            webLineWidth = 1f
            webColor = view.context.getColor(R.color.primary)
            webLineWidthInner = 1f
            webColorInner = view.context.getColor(R.color.secondary)
            webAlpha = 100
            yAxis.axisMinimum = 0f
        }

        hourChartEntries.clear()

        val dao = LocalDatabase
            .getDBMainThread(view.context)
            .speedRecordsDAO()
        dao.insert(SpeedRecordEntity(Date().time, 10f, 20f))
        for (record in dao.getAfter(Date().time - (1000 * 60 * 60))) {
            if (record.up == null || record.down == null)
                continue
            hourChartEntries.add(
                RadarSpeedEntry(RadarEntry(record.up), RadarEntry(record.down))
            )
        }

        updateChart()

        hourChart.apply {
            animateXY(1400, 1400, Easing.EaseOutCubic)
            legend.isEnabled = false
        }
    }


    private fun updateChart() {
        val upSet = createRadarDataset(hourChartUpEntries, "Upload", R.color.secondary)
        val downSet = createRadarDataset(hourChartDownEntries, "Download", R.color.primary)

        //TODO: check db if runtime lists are empty
        val sets: List<RadarDataSet> = arrayListOf(upSet, downSet)
        val data = RadarData(sets).apply {
            setValueTextSize(8f)
            setDrawValues(false)
            setValueTextColor(view.context.getColor(R.color.onBackground))
        }
        hourChart.apply {
            this.data = data
            invalidate()
        }
    }

    private fun createRadarDataset(
        entries: List<RadarEntry>,
        label: String,
        color: Int
    ): RadarDataSet {
        val colorRes = view.context.getColor(color)
//        val evenList = evenlyPickedList(entries, 60)
        val set = RadarDataSet(
            entries, //  filledList(evenList, 60, RadarEntry(0f)),
            label
        ).apply {
            this.color = colorRes
            fillColor = colorRes
            setDrawFilled(true)
            fillAlpha = 120
            lineWidth = 2f
            isDrawHighlightCircleEnabled = true
            setDrawHighlightIndicators(false)
        }
        return set
    }

    companion object {
        private fun evenlyDistributedIndicies(length: Int, dividedBy: Int): List<Int> {
            val resultCount =
                if (length % 2 == 0) length
                else length - 1
            val spacing = length / dividedBy
            return (resultCount downTo 1 step spacing).toList()
        }

        private fun <T> evenlyPickedList(inputList: List<T>, n: Int): List<T> {
            if (n <= 0 || inputList.isEmpty()) {
                return emptyList()
            }

            val step = inputList.size.toDouble() / n.toDouble()
            val result = mutableListOf<T>()

            for (i in n downTo 1) {
                val index = ((i - 1) * step).toInt()
                result.add(inputList[index])
            }

            return result
        }

        private fun <T> filledList(
            originalList: List<T>,
            n: Int,
            default: T
        ): List<T> {
            val newList = originalList.toMutableList()
            for (i in originalList.size until n)
                newList.add(i, default)
            return newList
        }
    }
}