package com.galacticai.networkpulse.common.models.bit_value

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import kotlin.math.abs

open class BitValue(
    val value: Float,
    val unit: BitUnit
) {
    fun toString(maxFractionDigits: Int): String {
        val formatted = NumberFormat.getInstance().apply {
            maximumFractionDigits = maxFractionDigits
        }.format(value)
        return "$formatted ${name.short}"
    }

    override fun toString(): String = toString(2)
    fun toLongString() = "$value ${name.long}"
    val name = ShortLongName(
        "${unit.exponent.name.short}${unit.base.name.short}",
        "${unit.exponent.name.long}${unit.base.name.long}"
    )
    val bits get() = value * unit.toBitMultiplier
    val bytes get() = valueToUnit(BitUnit.BasicByte(unit.exponent))
    fun valueToUnit(toUnit: BitUnit) =
        bits / toUnit.toBitMultiplier

    fun valueToBase(toBase: BitUnitBase) =
        (bits / unit.toBitMultiplier * unit.base.toBitMultiplier) / toBase.toBitMultiplier

    fun toUnit(toUnit: BitUnit) = BitValue(
        valueToUnit(toUnit),
        toUnit
    )

    fun toBase(toBase: BitUnitBase) = BitValue(
        valueToBase(toBase),
        BitUnit(unit.exponent, toBase)
    )

    fun toNearestUnit(
        exponents: List<BitUnitExponent> = unit.exponent.entries(),
        newBase: BitUnitBase = unit.base
    ): BitValue {
        val sortedExponents = exponents.sortedBy { it.exponent }

        var nearestExponent = exponents.first()
        var minDifference = Float.MAX_VALUE
        val b: Float = bits
        for (unit in sortedExponents) {
            val difference = abs(b - unit.toBaseMultiplier * 10)
            if (difference >= minDifference) continue
            minDifference = difference
            nearestExponent = unit
        }
        val nearestUnit = BitUnit(nearestExponent, newBase)
        val valueInNearestUnit = valueToUnit(nearestUnit)

        return BitValue(valueInNearestUnit, nearestUnit)
    }
}

@Preview(showBackground = true)
@Composable
fun BitValueTest() {
    val x = BitValue(
        100_000f,
        BitUnit(BitUnitExponent.Metric.Kilo, BitUnitBase.Byte)
    )
    Column(modifier = Modifier.padding(10.dp)) {
        Text("Original ${x.unit} = ${x.toLongString()}")
        val gb = x.toUnit(BitUnit(BitUnitExponent.Metric.Giga, BitUnitBase.Byte))
        Text("Manual to ${gb.unit} = ${gb.toLongString()}")
        val nearest = x.toNearestUnit()
        Text("Auto = ${nearest.toLongString()}")
        val bitBase = nearest.toBase(BitUnitBase.Bit)
        Text("in bits = ${bitBase.toLongString()}")
    }
}