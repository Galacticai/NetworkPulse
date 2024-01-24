package com.galacticai.networkpulse.common.models.sql.column

enum class ColumnType(val inSQL: String) {
    /** [Int] [Long] */
    integer("INTEGER"),
    /** [String] */
    text("TEXT"),
    /** [Double] [Float] */
    real("REAL"),
    /** Binary */
    blob("BLOB"),
    /** [Boolean] */
    boolean("BOOLEAN"),
    /** [null] */
    NULL("NULL");

    override fun toString(): String = inSQL
}