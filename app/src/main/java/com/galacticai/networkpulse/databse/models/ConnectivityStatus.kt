package com.galacticai.networkpulse.databse.models

//@Entity(tableName = ConnectivityUtils.tableName)
data class Connectivity(
    //@PrimaryKey
    val time: Long,
    val status: Int,
)

object ConnectivityUtils {
    const val tableName = "connectivity"
}