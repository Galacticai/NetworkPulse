package com.galacticai.networkpulse.common.models

import org.json.JSONObject

interface Jsonable {
    fun toJson(): JSONObject
}
