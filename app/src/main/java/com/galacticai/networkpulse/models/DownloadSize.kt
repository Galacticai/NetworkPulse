package com.galacticai.networkpulse.models

import com.galacticai.networkpulse.common.models.Jsonable
import com.galacticai.networkpulse.common.models.bit_value.BitUnit
import com.galacticai.networkpulse.common.models.bit_value.BitUnitBase
import com.galacticai.networkpulse.common.models.bit_value.BitUnitExponent
import com.galacticai.networkpulse.common.models.bit_value.BitValue
import org.json.JSONObject

//? Yes this is like enum but custom
open class DownloadSize(
    value: Int,
    unit: BitUnit,
) : BitValue(value.toFloat(), unit),
    Jsonable {
    override fun toString(): String = "${value.toInt()} $unit"

    override fun toJson(): JSONObject = JSONObject().apply {
        put("value", bits / 8)
    }

    val url get() = "https://ipv4.appliwave.testdebit.info/${this}.iso"

    fun requiredBytesPerSecond(seconds: Int): Int =
        if (seconds <= 0) throw IllegalArgumentException("seconds must be > 0")
        else (bits / 8 / seconds).toInt()

    fun isHighUsage(requestInterval: Int, maxPerSecond: Int): Boolean =
        requiredBytesPerSecond(requestInterval) > maxPerSecond

    companion object {
        fun fromString(s: String): DownloadSize =
            entries.find { it.toString() == s }
                ?: throw IllegalArgumentException("Invalid download size: $s")

        fun fromJson(json: JSONObject): DownloadSize {
            val b = json.getDouble("value").toFloat()
            val bitValue = BitValue(b, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
                .toNearestUnit()
            return DownloadSize(bitValue.value.toInt(), bitValue.unit)
        }

        val sizes = listOf(1, 5, 10, 50, 100, 500)
        val exponents = listOf<BitUnitExponent>(
            BitUnitExponent.Metric.Basic,
            BitUnitExponent.Metric.Kilo,
            BitUnitExponent.Metric.Mega,
        )
        val entries
            get() = exponents.flatMap { e ->
                sizes.map { s ->
                    DownloadSize(s, BitUnit(e, BitUnitBase.Byte))
                }
            }
    }

    //    object Size0 : DownloadSize(0, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size1 : DownloadSize(1, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size5 : DownloadSize(5, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size10 : DownloadSize(10, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size50 : DownloadSize(50, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size100 : DownloadSize(100, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size500 : DownloadSize(500, BitUnit(BitUnitExponent.Metric.Basic, BitUnitBase.Byte))
    //    object Size1K : DownloadSize(1, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
    //    object Size5K : DownloadSize(5, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
    //    object Size10K : DownloadSize(10, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
    //    object Size50K : DownloadSize(50, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
    //    object Size100K : DownloadSize(100, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
    //    object Size500K : DownloadSize(500, BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte))
    //    object Size1M : DownloadSize(1, BitUnit(BitUnitExponent.Metric.Mega, BitUnitBase.Byte))
    //    object Size5M : DownloadSize(5, BitUnit(BitUnitExponent.Metric.Mega, BitUnitBase.Byte))
    //    object Size10M : DownloadSize(10, BitUnit(BitUnitExponent.Metric.Mega, BitUnitBase.Byte))
    //    object Size50M : DownloadSize(50, BitUnit(BitUnitExponent.Metric.Mega, BitUnitBase.Byte))
}