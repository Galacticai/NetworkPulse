package com.galacticai.networkpulse.common.number_with_unit

import kotlin.math.pow

sealed class NumberUnitPower(
    val shortName: String,
    val longName: String,
    val power: Int,
    val unitSystem: NumberUnitSystem,
) {
    override fun toString(): String = shortName
    abstract fun getAll(): List<NumberUnitPower>

    /** The amount a value should be multiplied by to get the value in the base unit */
    val baseMultiplier get() = unitSystem.multiplier.pow(power)

    val base: NumberUnitPower
        get() = when (unitSystem) {
            NumberUnitSystem.Metric -> Metric.Base
            NumberUnitSystem.Binary -> Binary.Base
            else -> throw IllegalStateException("Unsupported unit system: $unitSystem")
        }

    open class Metric(shortName: String, longName: String, power: Int) :
        NumberUnitPower(shortName, longName, power, NumberUnitSystem.Metric) {

        override fun getAll(): List<Metric> =
            listOf(
                Pico,
                Nano,
                Micro,
                Milli,
                Centi,
                Deci,
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

        object Pico : Metric("p", "Pico", -12)
        object Nano : Metric("n", "Nano", -9)
        object Micro : Metric("μ", "Micro", -6)
        object Milli : Metric("m", "Milli", -3)
        object Centi : Metric("c", "Centi", -2)
        object Deci : Metric("d", "Deci", -1)

        object Base : Metric("", "", 0)

        object Kilo : Metric("K", "Kilo", 1)
        object Mega : Metric("M", "Mega", 2)
        object Giga : Metric("G", "Giga", 3)
        object Tera : Metric("T", "Tera", 4)
        object Peta : Metric("P", "Peta", 5)
        object Exa : Metric("E", "Exa", 6)
        object Zetta : Metric("Z", "Zetta", 7)
        object Yotta : Metric("Y", "Yotta", 8)
    }

    open class Binary(shortName: String, longName: String, power: Int) :
        NumberUnitPower(shortName, longName, power, NumberUnitSystem.Binary) {

        override fun getAll(): List<Binary> =
            listOf(Base, Kibi, Mebi, Gibi, Tebi, Pebi, Exbi, Zebi, Yobi)

        object Base : Binary("", "", 0)
        object Kibi : Binary("Ki", "Kibi", 1)
        object Mebi : Binary("Mi", "Mebi", 2)
        object Gibi : Binary("Gi", "Gibi", 3)
        object Tebi : Binary("Ti", "Tebi", 4)
        object Pebi : Binary("Pi", "Pebi", 5)
        object Exbi : Binary("Ei", "Exbi", 6)
        object Zebi : Binary("Zi", "Zebi", 7)
        object Yobi : Binary("Yi", "Yobi", 8)
    }
}