package com.galacticai.networkpulse.common.models

import android.os.CancellationSignal
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.Duration
import java.util.Date
import java.util.SortedMap

/** Queue that runs tasks when calling [run] but only triggers the event of a task when it is the first one in the queue
 */
class PatientTaskQueue<K : Comparable<K>, V> {
    abstract class PatientTaskEvents<K : Comparable<K>, V> {
        abstract fun onAddListener(key: K)
        abstract fun onStartListener(key: K, startedAt: Date)
        abstract fun onStopListener(key: K, startedAt: Date, runtime: Duration)
        abstract fun onDoneListener(key: K, value: V?, startedAt: Date, runtime: Duration)
        abstract fun onTimeoutListener(key: K, timeout: Duration, startedAt: Date)
        abstract fun onErrorListener(key: K, error: Exception, startedAt: Date, runtime: Duration)
        abstract fun onFinallyListener(key: K, info: TaskInfo<V>)
    }

    constructor()
    constructor(
        onAddListener: PatientTaskAdd<K>? = null,
        onStartListener: PatientTaskStart<K>? = null,
        onStopListener: PatientTaskStop<K>? = null,
        onDoneListener: PatientTaskDone<K, V>? = null,
        onTimeoutListener: PatientTaskTimeout<K>? = null,
        onErrorListener: PatientTaskError<K>? = null,
        onFinallyListener: PatientTaskFinally<K, V>? = null
    ) : this() {
        this.onAddListener = onAddListener
        this.onStartListener = onStartListener
        this.onTimeoutListener = onTimeoutListener
        this.onStopListener = onStopListener
        this.onErrorListener = onErrorListener
        this.onDoneListener = onDoneListener
        this.onFinallyListener = onFinallyListener
    }

    constructor(events: PatientTaskEvents<K, V>) : this(
        events::onAddListener,
        events::onStartListener,
        events::onStopListener,
        events::onDoneListener,
        events::onTimeoutListener,
        events::onErrorListener
    )

    private val tasks: SortedMap<K, TaskInfo<V>> = sortedMapOf()


    /** Get a [TaskInfo] of the provided [key] or throws [NoSuchElementException] if not found
     * @return [TaskInfo] corresponding to [key]
     * @exception NoSuchElementException if the task was not found */
    private fun getOrThrow(key: K): TaskInfo<V> =
        tasks[key] ?: throw NoSuchElementException("Task not found: $key")


    /** Add a task
     * @return true if added
     * @exception IllegalArgumentException if timeout is zero or negative */
    fun add(key: K, timeout: Duration, task: PatientTask<V>): Boolean {
        if (timeout.isZero || timeout.isNegative)
            throw IllegalArgumentException("Timeout duration must be positive and non zero")

        if (tasks.containsKey(key)) return false

        tasks[key] = TaskInfo(task, timeout, FutureValue.Pending(Date()))
        onAddListener?.invoke(key)
        return true
    }

    /** Check whether a task of the provided [key] is currently pending
     * @return true if pending */
    fun isPending(key: K): Boolean = tasks[key]?.value is FutureValue.Pending

    /** Check whether a task of the provided [key] is currently running
     * @return true if running */
    fun isRunning(key: K): Boolean = tasks[key]?.value is FutureValue.Running

    /** Check whether a task of the provided [key] has either finished, stopped, or failed
     * - [FutureValue.Finished] : Success
     * - [FutureValue.Stopped] : Stopped intentionally
     * - [FutureValue.Failed] : Error or Timeout
     * @return true if one of the above types
     * @exception NoSuchElementException if the task was not found */
    private fun isEnded(key: K): Boolean = when (getOrThrow(key).value) {
        is FutureValue.Finished -> true
        is FutureValue.Stopped -> true
        is FutureValue.Failed -> true
        else -> false
    }

    /** Run a task
     * @return Task value or null failed
     * @exception IllegalStateException if the task is already running
     * @exception NoSuchElementException if the task was not found */
    fun run(key: K): V? {
        val info = getOrThrow(key)

        if (!isPending(key))
            throw IllegalStateException("Task is not pending: $key")

        val startedAt = Date()
        fun getRuntime(): Duration = Duration.ofMillis(System.currentTimeMillis() - startedAt.time)

        info.value = FutureValue.Running(CancellationSignal(), startedAt)
        onStartListener?.invoke(key, startedAt)
        var value: V? = null
        val job = Job()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                withTimeout(info.timeout.toMillis()) {
                    Log.d("PatientTaskQueue", "run $key . withTimeout")
                    try {
                        value = info.task()
                        info.value = FutureValue.Finished(value, startedAt, getRuntime())
                        Log.d("PatientTaskQueue", "run $key . Finished")
                    } catch (error: Exception) {
                        info.value = FutureValue.Failed.Error(error, startedAt, getRuntime())
                        Log.d("PatientTaskQueue", "run $key . Failed.Error")
                    } finally {
                        onFinallyListener?.invoke(key, info)
                    }
                }
            } catch (error: CancellationException) {
                if (error is TimeoutCancellationException) {
                    info.value = FutureValue.Failed.Timeout(info.timeout, startedAt)
                    Log.d("PatientTaskQueue", "run $key . Timeout")
                } else {
                    info.value = FutureValue.Stopped(startedAt, getRuntime())
                    Log.d("PatientTaskQueue", "run $key . Stopped")
                }
            } finally {
                job.cancel()
                onFinallyListener?.invoke(key, info)
            }
        }
        return value
    }

    fun addRunRoll(key: K, timeout: Duration, task: PatientTask<V>) {
        if (!add(key, timeout, task)) return
        run(key)
        rollForward()
    }

    fun runAndRoll() {
        if (tasks.isEmpty()) return
        run(tasks.keys.first())
        rollForward()
    }

    /** Trigger, in order, the events of tasks that have ended ([FutureValue.Finished], [FutureValue.Failed])...
     *
     * And stop if a task has not ended yet ([FutureValue.Pending], [FutureValue.Running]) */
    fun rollForward() {
        val keys = tasks.keys.toList()
        for (key in keys) {
            val ended = triggerTaskEvent(key, tasks[key]!!)
            if (!ended) break
            tasks.remove(key)
        }
    }

    /** Trigger the event of a task
     * @return true if the task has ended */
    private fun triggerTaskEvent(key: K, taskInfo: TaskInfo<V>): Boolean {
        when (val taskValue = taskInfo.value) {
            is FutureValue.Finished -> onDoneListener?.invoke(
                key,
                taskValue.value,
                taskValue.startedAt!!,
                taskValue.runtime!!,
            )

            is FutureValue.Stopped -> onStopListener?.invoke(
                key,
                taskValue.startedAt!!,
                taskValue.runtime!!
            )

            is FutureValue.Failed.Timeout -> onTimeoutListener?.invoke(
                key,
                taskValue.timeout,
                taskValue.startedAt!!
            )

            is FutureValue.Failed.Error -> onErrorListener?.invoke(
                key,
                taskValue.error,
                taskValue.startedAt!!,
                taskValue.runtime!!,
            )

            else -> {} //? not ended, do nothing
        }
        return isEnded(key)
    }
    
    /** Add and run a task
     * @exception IllegalStateException if the task is already running
     * @exception NoSuchElementException if the task was not found
     * @return Task value or null if not added or failed */
    fun addRun(key: K, timeout: Duration, task: PatientTask<V>): V? {
        Log.d("PatientTaskQueue", "addRun $key")
        val added = add(key, timeout, task)
        if (!added) return null
        return run(key)
    }

    /** Check if a task exists in the queue
     * @return true if found */
    fun contains(key: K): Boolean = tasks.containsKey(key)

    /** Stop a task
     * @return true if found + was running + stopped
     * @exception IllegalStateException if the task is already running */
    fun stop(key: K): Boolean {
        if (!contains(key)) return false

        if (!isRunning(key))
            throw IllegalStateException("Task is not running")
        val info = tasks[key]!!
        val running = info.value as FutureValue.Running

        running.cancellationSignal!!.cancel()

        info.value =
            FutureValue.Stopped(
                running.startedAt!!,
                Duration.ofMillis(System.currentTimeMillis() - running.startedAt.time)
            )
        return true
    }

    /** Remove a task
     * @return true if found + not running + removed */
    fun remove(key: K, stopRunning: Boolean = false): Boolean {
        if (!contains(key)) return true

        if (isRunning(key)) {
            if (stopRunning) {
                val stopped = stop(key)
                if (!stopped) return false
            } else return false
        }
        return tasks.remove(key) != null
    }


    /** Called when a task is added */
    private var onAddListener: PatientTaskAdd<K>? = null

    /** Called when a task is started */
    private var onStartListener: PatientTaskStart<K>? = null

    /** Called when a task gets cancelled */
    private var onStopListener: PatientTaskStop<K>? = null

    /** Called when a task is done */
    private var onDoneListener: PatientTaskDone<K, V>? = null

    /** Called when a task times out */
    private var onTimeoutListener: PatientTaskTimeout<K>? = null

    /** Called when a task throws an error */
    private var onErrorListener: PatientTaskError<K>? = null

    /** Called after the task had ended (even if failed)... (Useful for cleaning up...) */
    private var onFinallyListener: PatientTaskFinally<K, V>? = null

    /** Called when a task is added */
    fun setOnAddListener(listener: PatientTaskAdd<K>?) {
        onAddListener = listener
    }

    /** Called when a task is started */
    fun setOnStartListener(listener: PatientTaskStart<K>?) {
        onStartListener = listener
    }

    /** Called when a task times out */
    fun setOnTimeoutListener(listener: PatientTaskTimeout<K>?) {
        onTimeoutListener = listener
    }

    /** Called when a task gets cancelled */
    fun setOnStopListener(listener: PatientTaskStop<K>?) {
        onStopListener = listener
    }

    /** Called when a task throws an error */
    fun setOnErrorListener(listener: PatientTaskError<K>?) {
        onErrorListener = listener
    }

    /** Called when a task is done */
    fun setOnDoneListener(listener: PatientTaskDone<K, V>?) {
        onDoneListener = listener
    }

    /** Called after the task had ended (even if failed)... (Useful for cleaning up...) */
    fun setOnFinallyListener(listener: PatientTaskFinally<K, V>?) {
        onFinallyListener = listener
    }
}

data class TaskInfo<V>(
    val task: PatientTask<V>,
    val timeout: Duration,
    var value: FutureValue<V?>
)

typealias PatientTask<V> = () -> V
typealias PatientTaskAdd<K> = (key: K) -> Unit
typealias PatientTaskStart<K> = (key: K, startedAt: Date) -> Unit
typealias PatientTaskStop<K> = (key: K, startedAt: Date, runtime: Duration) -> Unit
typealias PatientTaskDone<K, V> = (key: K, value: V?, startedAt: Date, runtime: Duration) -> Unit
typealias PatientTaskTimeout<K> = (key: K, timeout: Duration, startedAt: Date) -> Unit
typealias PatientTaskError<K> = (key: K, error: Exception, startedAt: Date, runtime: Duration) -> Unit
typealias PatientTaskFinally<K, V> = (key: K, info: TaskInfo<V>) -> Unit
