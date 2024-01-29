package com.galacticai.networkpulse.common.number_with_unit

import com.galacticai.networkpulse.common.models.Jsonable
import org.json.JSONObject

open class NumberUnitSystem(val multiplier: Double) : Jsonable {
    companion object {
        val entries = listOf(Binary, Metric)
        fun fromMultiplier(multiplier: Double) =
            entries.find { it.multiplier == multiplier }

        fun fromJson(json: JSONObject): NumberUnitSystem =
            NumberUnitSystem(json.getDouble("multiplier"))
    }

    override fun toString(): String = "NumberUnitSystem(multiplier = $multiplier)"
    override fun toJson(): JSONObject =
        JSONObject().apply {
            put("multiplier", multiplier)
        }

    object Binary : NumberUnitSystem(1024.0)
    object Metric : NumberUnitSystem(1000.0)
}