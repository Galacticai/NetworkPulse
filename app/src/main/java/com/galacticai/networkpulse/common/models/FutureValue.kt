package com.galacticai.networkpulse.common.models

import android.os.CancellationSignal
import java.time.Duration
import java.util.Date

/** Value to be retrieved in the future
 *
 * This is useful for task related stuff where the value is not immediately available
 * @param V Value type */
sealed class FutureValue<out V> {

    /** Waiting to get started later */
    data class Pending<out V>(
        val addedAt: Date? = null
    ) : FutureValue<V>()

    /** Running in order to get the value */
    data class Running<out V>(
        val cancellationSignal: CancellationSignal? = null,
        val startedAt: Date? = null,
    ) : FutureValue<V>()

    /** Stopped intentionally
     * @param runtime the amount of time */
    data class Stopped<out V>(
        val startedAt: Date? = null,
        val runtime: Duration? = null,
    ) : Failed<V>()

    /** Done, and the value is ready */
    data class Finished<out V>(
        val value: V,
        val startedAt: Date? = null,
        val runtime: Duration? = null,
    ) : FutureValue<V>()

    /** Failed to get the value */
    sealed class Failed<out V> : FutureValue<V>() {
        /** Ran out of time
         * @param duration the amount of time */
        data class Timeout<out V>(
            val timeout: Duration,
            val startedAt: Date? = null,
        ) : Failed<V>()

        /** Something went wrong
         * @param error the error thrown by the runner */
        data class Error<out V>(
            val error: Exception,
            val startedAt: Date? = null,
            val runtime: Duration? = null,
        ) : Failed<V>()
    }
}