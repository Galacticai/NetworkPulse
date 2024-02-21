package com.galacticai.networkpulse.models.records_summary

data class TimedRangeValue<N : Number>(
    val value: N, val from: Long, val to: Long,
)