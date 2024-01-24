package com.galacticai.networkpulse.common.models.sql

/** Represents a SQL data type */
abstract class SQLDataType(
    val inSQL: String,
    vararg val args: String
) {

    override fun toString(): String =
        inSQL + args.joinToString(", ", "[", "]")

    object VARCHAR : SQLDataType("VARCHAR")
    object CHAR : SQLDataType("CHAR")
    object TEXT : SQLDataType("TEXT")
    object BOOLEAN : SQLDataType("BOOLEAN")
    object YEAR : SQLDataType("YEAR")
    object DATE : SQLDataType("DATE")
    object TIME : SQLDataType("TIME")
    object TIMESTAMP : SQLDataType("TIMESTAMP")
    object REAL : SQLDataType("REAL")
    object NUMERIC : SQLDataType("NUMERIC")
    object INT : SQLDataType("INT")
    object INTEGER : SQLDataType("INTEGER")
    object DECIMAL : SQLDataType("DECIMAL")
    object FLOAT : SQLDataType("FLOAT")
    object DOUBLE : SQLDataType("DOUBLE")
    object BINARY : SQLDataType("BINARY")
    object BLOB : SQLDataType("BLOB")
    object CLOB : SQLDataType("CLOB")
    object SMALLINT : SQLDataType("SMALLINT")
    object BIGINT : SQLDataType("BIGINT")
    object TINYINT : SQLDataType("TINYINT")
    object VARBINARY : SQLDataType("VARBINARY")
    object JSON : SQLDataType("JSON")
    object XML : SQLDataType("XML")
    object ARRAY : SQLDataType("ARRAY")
    object UUID : SQLDataType("UUID")
    object INTERVAL : SQLDataType("INTERVAL")
    object GEOMETRY : SQLDataType("GEOMETRY")
    object POINT : SQLDataType("POINT")
    object LINESTRING : SQLDataType("LINESTRING")
    object POLYGON : SQLDataType("POLYGON")
    object GEOMETRYCOLLECTION : SQLDataType("GEOMETRYCOLLECTION")
    object MULTIPOINT : SQLDataType("MULTIPOINT")
    object MULTILINESTRING : SQLDataType("MULTILINESTRING")
    object MULTIPOLYGON : SQLDataType("MULTIPOLYGON")
}

