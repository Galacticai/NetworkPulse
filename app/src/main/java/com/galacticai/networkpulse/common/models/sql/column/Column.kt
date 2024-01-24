package com.galacticai.networkpulse.common.models.sql.column

import com.galacticai.networkpulse.common.models.sql.SQLDataType

data class Column(
    val name: String,
    val type: SQLDataType,
    val flags: ColumnFlags = ColumnFlags.NONE
) {
    override fun toString(): String =
        "$name $type $flags"
}

