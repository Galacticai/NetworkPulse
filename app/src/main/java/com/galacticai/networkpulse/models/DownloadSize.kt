package com.galacticai.networkpulse.models

import com.galacticai.networkpulse.common.models.Jsonable
import com.galacticai.networkpulse.common.number_with_unit.NumberUnit
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitBase
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitPower
import com.galacticai.networkpulse.common.number_with_unit.NumberWithUnit
import org.json.JSONObject
import java.util.Locale

//? Yes this is like enum but custom
sealed class DownloadSize(
    value: Int,
    unit: NumberUnit,
) : NumberWithUnit(value.toDouble(), unit),
    Jsonable {
    override fun toString(): String =
        "$value${unit.unitPower.shortName.uppercase(Locale.ROOT)}"

    override fun toJson(): JSONObject = JSONObject().apply {
        put("value", value)
        putOpt("unit", unit.unitPower.toJson())
    }

    val url get() = "https://ipv4.appliwave.testdebit.info/${this}.iso"

    fun requiredBytesPerSecond(seconds: Int): Int =
        if (seconds <= 0) throw IllegalArgumentException("seconds must be > 0")
        else (super.valueInBaseUnit / seconds).toInt()

    fun isHighUsage(requestInterval: Int, maxPerSecond: Int): Boolean =
        requiredBytesPerSecond(requestInterval) > maxPerSecond

    companion object {
        fun fromString(s: String): DownloadSize =
            entries.find { it.toString() == s }
                ?: throw IllegalArgumentException("Invalid download size: $s")

        fun fromJson(json: JSONObject): DownloadSize {
            val value = json.getInt("value")
            val unit = NumberUnit.fromJson(json.getJSONObject("unit"))
            return fromString("$value$unit")
        }

        val entries = listOf(
            Size0,
            Size1,
            Size5,
            Size10,
            Size50,
            Size100,
            Size500,
            Size1K,
            Size5K,
            Size10K,
            Size50K,
            Size100K,
            Size500K,
            Size1M,
            Size5M,
            Size10M,
            Size50M
        )
    }

    data object Size0 :
        DownloadSize(0, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size1 :
        DownloadSize(1, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size5 :
        DownloadSize(5, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size10 :
        DownloadSize(10, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size50 :
        DownloadSize(50, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size100 :
        DownloadSize(100, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size500 :
        DownloadSize(500, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    data object Size1K :
        DownloadSize(1, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    data object Size5K :
        DownloadSize(5, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    data object Size10K :
        DownloadSize(10, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    data object Size50K :
        DownloadSize(50, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    data object Size100K :
        DownloadSize(100, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    data object Size500K :
        DownloadSize(500, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    data object Size1M :
        DownloadSize(1, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))

    data object Size5M :
        DownloadSize(5, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))

    data object Size10M :
        DownloadSize(10, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))

    data object Size50M :
        DownloadSize(50, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))
}