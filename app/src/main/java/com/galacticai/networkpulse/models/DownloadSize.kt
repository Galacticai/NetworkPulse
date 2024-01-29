package com.galacticai.networkpulse.models

import com.galacticai.networkpulse.common.models.Jsonable
import com.galacticai.networkpulse.common.number_with_unit.NumberUnit
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitBase
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitPower
import com.galacticai.networkpulse.common.number_with_unit.NumberUnitSystem
import com.galacticai.networkpulse.common.number_with_unit.NumberWithUnit
import org.json.JSONObject

//? Yes this is like enum but custom
open class DownloadSize(
    value: Int,
    unit: NumberUnit,
) : NumberWithUnit(value.toDouble(), unit),
    Jsonable {
    override fun toString(): String = "${value.toInt()} $unit"

    override fun toJson(): JSONObject = JSONObject().apply {
        put("value", value)
        put("unit", unit.toJson())
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

        fun fromJson(json: JSONObject): DownloadSize = DownloadSize(
            json.getInt("value"),
            NumberUnit.fromJson(json.getJSONObject("unit"), NumberUnitSystem.Metric)
        )

        val sizes = listOf(1, 5, 10, 50, 100, 500)
        val powers = listOf<NumberUnitPower>(
            NumberUnitPower.Metric.Base,
            NumberUnitPower.Metric.Kilo,
            NumberUnitPower.Metric.Mega,
        )
        val entries
            get() = powers.flatMap { p ->
                sizes.map { s ->
                    DownloadSize(s, NumberUnit(p, NumberUnitBase.Byte))
                }
            }
        //            listOf(
        //            Size0,
        //            Size1,
        //            Size5,
        //            Size10,
        //            Size50,
        //            Size100,
        //            Size500,
        //            Size1K,
        //            Size5K,
        //            Size10K,
        //            Size50K,
        //            Size100K,
        //            Size500K,
        //            Size1M,
        //            Size5M,
        //            Size10M,
        //            Size50M
        //        )
    }

    object Size0 :
        DownloadSize(0, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size1 :
        DownloadSize(1, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size5 :
        DownloadSize(5, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size10 :
        DownloadSize(10, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size50 :
        DownloadSize(50, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size100 :
        DownloadSize(100, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size500 :
        DownloadSize(500, NumberUnit(NumberUnitPower.Metric.Base, NumberUnitBase.Byte))

    object Size1K :
        DownloadSize(1, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    object Size5K :
        DownloadSize(5, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    object Size10K :
        DownloadSize(10, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    object Size50K :
        DownloadSize(50, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    object Size100K :
        DownloadSize(100, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    object Size500K :
        DownloadSize(500, NumberUnit(NumberUnitPower.Metric.Kilo, NumberUnitBase.Byte))

    object Size1M :
        DownloadSize(1, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))

    object Size5M :
        DownloadSize(5, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))

    object Size10M :
        DownloadSize(10, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))

    object Size50M :
        DownloadSize(50, NumberUnit(NumberUnitPower.Metric.Mega, NumberUnitBase.Byte))
}