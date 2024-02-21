package com.galacticai.networkpulse.models.records_summary

data class AverageInfo<N : Number>(
    val average: N,
    val max: TimedValue<N>,
    val min: TimedValue<N>,
)