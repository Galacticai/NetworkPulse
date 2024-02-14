//package com.galacticai.networkpulse.common.number_with_unit
//
//import com.galacticai.networkpulse.common.models.Jsonable
//import org.json.JSONObject
//
//data class NumberUnit(
//    val unitPower: NumberUnitPower,
//    val unitBase: NumberUnitBase,
//) : Jsonable {
//    val shortUnit get() = unitPower.shortName + unitBase.shortName
//    val longUnit get() = unitPower.longName + unitBase.longName
//
//    companion object {
//        fun fromJson(json: JSONObject, unitSystem: NumberUnitSystem): NumberUnit = NumberUnit(
//            NumberUnitPower.fromJson(json.getJSONObject("unitPower"), unitSystem),
//            NumberUnitBase.fromJson(json.getJSONObject("unitBase"))
//        )
//    }
//
//    override fun toJson(): JSONObject = JSONObject().apply {
//        put("unitPower", unitPower.toJson())
//        put("unitBase", unitBase.toJson())
//    }
//
//    override fun toString(): String = shortUnit
//
//    /** The amount a value should be multiplied by to get the value in the base unit */
//    val baseMultiplier get() = unitPower.baseMultiplier
//
//    /** The base unit of this (with [unitPower] set to [NumberUnitPower.base]) */
//    val base get() = if (isBaseUnit) this else NumberUnit(unitPower.base, unitBase)
//
//    val isBaseUnit get() = unitBase.isBaseUnit && unitPower.exponent == 0
//}