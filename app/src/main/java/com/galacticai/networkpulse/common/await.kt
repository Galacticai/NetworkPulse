package com.galacticai.networkpulse.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun await(action: suspend () -> Unit) =
    CoroutineScope(Dispatchers.IO).launch { action() }