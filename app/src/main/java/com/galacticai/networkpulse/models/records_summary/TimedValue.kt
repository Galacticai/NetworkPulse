package com.galacticai.networkpulse.models.records_summary

data class TimedValue<N : Number>(
    val value: N,
    val time: Long,
)