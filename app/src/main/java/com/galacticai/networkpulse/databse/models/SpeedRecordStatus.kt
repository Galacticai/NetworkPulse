package com.galacticai.networkpulse.databse.models

enum class SpeedRecordStatus(val value: Int) {
    Success(1), Timeout(2), Error(3);

    fun toInt() = value
    override fun toString() = toInt().toString()

    companion object {
        fun isSuccess(i: Int) = i == Success.toInt()
        fun isNotSucceess(i: Int) = !isSuccess(i)
        fun isTimeout(i: Int) = i == Timeout.toInt()
        fun isError(i: Int) = i == Error.toInt()
        fun isOther(i: Int) = !(isSuccess(i) || isTimeout(i) || isError(i))
    }
}