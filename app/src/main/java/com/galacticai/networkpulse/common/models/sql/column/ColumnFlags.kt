package com.galacticai.networkpulse.common.models.sql.column

class ColumnFlags {
    companion object {
        val NONE = ColumnFlags()
    }

    private val flagsList = mutableListOf<String>()

    fun primaryKey(): ColumnFlags {
        flagsList.add("PRIMARY KEY")
        return this
    }

    fun autoIncrement(): ColumnFlags {
        flagsList.add("AUTOINCREMENT")
        return this
    }

    fun notNull(): ColumnFlags {
        flagsList.add("NOT NULL")
        return this
    }

    fun unique(): ColumnFlags {
        flagsList.add("UNIQUE")
        return this
    }

    fun check(expression: String): ColumnFlags {
        flagsList.add("CHECK($expression)")
        return this
    }

    fun default(value: String): ColumnFlags {
        flagsList.add("DEFAULT($value)")
        return this
    }

    fun custom(expression: String): ColumnFlags {
        flagsList.add(expression)
        return this
    }

    override fun toString(): String {
        return flagsList.joinToString(" ")
    }
}