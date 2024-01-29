package com.galacticai.networkpulse.common.number_with_unit

import com.galacticai.networkpulse.common.models.Jsonable
import org.json.JSONObject

open class NumberUnitBase(
    val shortName: String,
    val longName: String,
    val multiplier: Double
) : Jsonable {
    companion object {
        val entries = listOf(Bit, Byte, Octet, Second)
        fun fromLongName(longName: String): NumberUnitBase? =
            entries.find { it.longName == longName }

        fun fromShortName(shortName: String): NumberUnitBase? =
            entries.find { it.shortName == shortName }

        fun fromJson(json: JSONObject): NumberUnitBase =
            NumberUnitBase(
                json.getString("shortName"),
                json.getString("longName"),
                json.getDouble("multiplier")
            )
    }

    override fun toJson(): JSONObject = JSONObject().apply {
        put("shortName", shortName)
        put("longName", longName)
        put("multiplier", multiplier)
    }


    override fun toString(): String = longName
    val isBaseUnit: Boolean get() = multiplier == 1.0

    object Bit : NumberUnitBase("b", "Bit", 1.0)
    object Byte : NumberUnitBase("B", "Byte", 8.0)
    object Octet : NumberUnitBase("o", "Octet", 16.0)

    object Second : NumberUnitBase("s", "Second", 1.0)
}