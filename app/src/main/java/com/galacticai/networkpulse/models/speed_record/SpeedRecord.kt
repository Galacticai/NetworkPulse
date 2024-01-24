package com.galacticai.networkpulse.models.speed_record

import com.galacticai.networkpulse.common.assertPositive

open class LooseSpeedRecord(open val up: Float?, open val down: Float?)

open class SpeedRecord(final override val up: Float, final override val down: Float) :
    LooseSpeedRecord(up, down) {

    init {
        assertPositive(up, down)
    }

    fun average(other: SpeedRecord): SpeedRecord {
        return average(this, other)
    }

    fun withTime(time: Long): TimedSpeedRecord {
        return TimedSpeedRecord(time, down, up)
    }

    companion object {
        fun average(vararg records: SpeedRecord): SpeedRecord {
            var totalDown = 0f
            var totalUp = 0f
            for (record in records) {
                totalUp += record.up
                totalDown += record.down
            }
            return SpeedRecord(
                totalDown / records.size,
                totalUp / records.size
            )
        }
    }
}

open class TimedSpeedRecord(
    val time: Long,
    up: Float,
    down: Float
) : SpeedRecord(up, down) {

    init {
        assertPositive(time)
    }

    fun withoutTime(): SpeedRecord {
        return SpeedRecord(up, down)
    }

    fun equalsValue(other: TimedSpeedRecord): Boolean {
        return up.equals(other.up) && down.equals(other.down)
    }
}
