package com.galacticai.networkpulse.common.number_with_unit

open class NumberUnitBase(
    val shortName: String,
    val longName: String,
    val multiplier: Double
) {
    override fun toString(): String = shortName


    val isBaseUnit: Boolean get() = multiplier == 1.0

    object Bit : NumberUnitBase("b", "Bit", 1.0)
    object Byte : NumberUnitBase("B", "Byte", 8.0)

    object Second : NumberUnitBase("s", "Second", 1.0)
}