package com.galacticai.networkpulse.common.number_with_unit

import com.galacticai.networkpulse.common.models.Jsonable
import org.json.JSONObject
import kotlin.math.pow

sealed class NumberUnitPower(
    val shortName: String,
    val longName: String,
    val exponent: Int,
    val unitSystem: NumberUnitSystem,
) : Jsonable {
    override fun toString(): String = shortName
    abstract fun getAll(): List<NumberUnitPower>

    /** The amount a value should be multiplied by to get the value in the base unit */
    val baseMultiplier get() = unitSystem.multiplier.pow(exponent)

    val base: NumberUnitPower
        get() = when (unitSystem) {
            NumberUnitSystem.Metric -> Metric.Base
            NumberUnitSystem.Binary -> Binary.Base
            else -> throw IllegalStateException("Unsupported unit system: $unitSystem")
        }

    override fun toJson(): JSONObject =
        JSONObject().apply {
            put("shortName", shortName)
            put("longName", longName)
            put("power", exponent)
            put("unitSystem", unitSystem.toJson())
        }

    companion object {
        fun fromLongName(longName: String): NumberUnitPower? =
            Metric.fromLongName(longName) ?: Binary.fromLongName(longName)

        fun fromShortName(shortName: String): NumberUnitPower? =
            Metric.fromShortName(shortName) ?: Binary.fromShortName(shortName)

        fun fromJson(json: JSONObject, unitSystem: NumberUnitSystem): NumberUnitPower =
            when (unitSystem) {
                is NumberUnitSystem.Metric -> Metric.fromJson(json)
                is NumberUnitSystem.Binary -> Binary.fromJson(json)
                else -> throw IllegalStateException("This method cannot instantiate a NumberUnitPower when the unit system is custom: $unitSystem")
            }
    }


    open class Metric(shortName: String, longName: String, exponent: Int) :
        NumberUnitPower(shortName, longName, exponent, NumberUnitSystem.Metric) {

        companion object {
            val entries
                get() = listOf(
                    Pico,
                    Nano,
                    Micro,
                    Milli,
                    //                    Centi,
                    //                    Deci,
                    Base,
                    Kilo,
                    Mega,
                    Giga,
                    Tera,
                    Peta,
                    Exa,
                    Zetta,
                    Yotta
                )

            /** Returns the entry with the given [longName], or null if not found */
            fun fromLongName(longName: String): Metric? =
                entries.find { it.longName == longName }

            /** Returns the entry with the given [shortName], or null if not found */
            fun fromShortName(shortName: String): Metric? =
                entries.find { it.shortName == shortName }


            fun fromJson(json: JSONObject) = Metric(
                json.getString("longName"),
                json.getString("shortName"),
                json.getInt("power")
            )
        }

        override fun getAll(): List<Metric> = entries


        data object Pico : Metric("p", "Pico", -12)
        data object Nano : Metric("n", "Nano", -9)
        data object Micro : Metric("μ", "Micro", -6)
        data object Milli : Metric("m", "Milli", -3)

        data object Base : Metric("", "", 0)

        data object Kilo : Metric("K", "Kilo", 3)
        data object Mega : Metric("M", "Mega", 6)
        data object Giga : Metric("G", "Giga", 9)
        data object Tera : Metric("T", "Tera", 12)
        data object Peta : Metric("P", "Peta", 15)
        data object Exa : Metric("E", "Exa", 18)
        data object Zetta : Metric("Z", "Zetta", 21)
        data object Yotta : Metric("Y", "Yotta", 24)
    }

    open class Binary(shortName: String, longName: String, exponent: Int) :
        NumberUnitPower(shortName, longName, exponent, NumberUnitSystem.Binary) {

        companion object {
            val entries get() = listOf(Base, Kibi, Mebi, Gibi, Tebi, Pebi, Exbi, Zebi, Yobi)

            /** Returns the entry with the given [longName], or null if not found */
            fun fromLongName(longName: String): Binary? =
                entries.find { it.longName == longName }

            /** Returns the entry with the given [shortName], or null if not found */
            fun fromShortName(shortName: String): Binary? =
                entries.find { it.shortName == shortName }


            fun fromJson(json: JSONObject) = Binary(
                json.getString("longName"),
                json.getString("shortName"),
                json.getInt("power")
            )
        }

        override fun getAll(): List<Binary> = entries


        data object Base : Binary("", "", 0)
        data object Kibi : Binary("Ki", "Kibi", 3)
        data object Mebi : Binary("Mi", "Mebi", 6)
        data object Gibi : Binary("Gi", "Gibi", 9)
        data object Tebi : Binary("Ti", "Tebi", 12)
        data object Pebi : Binary("Pi", "Pebi", 15)
        data object Exbi : Binary("Ei", "Exbi", 18)
        data object Zebi : Binary("Zi", "Zebi", 21)
        data object Yobi : Binary("Yi", "Yobi", 24)
    }
}