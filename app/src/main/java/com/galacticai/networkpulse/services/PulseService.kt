package com.galacticai.networkpulse.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.galacticai.networkpulse.R
import com.galacticai.networkpulse.common.isServiceRunning
import com.galacticai.networkpulse.common.models.patient_task_queue.PatientTaskEvent
import com.galacticai.networkpulse.common.models.patient_task_queue.PatientTaskQueue
import com.galacticai.networkpulse.databse.LocalDatabase
import com.galacticai.networkpulse.databse.models.SpeedRecord
import com.galacticai.networkpulse.databse.models.SpeedRecordStatus
import com.galacticai.networkpulse.databse.models.SpeedRecordUtils
import com.galacticai.networkpulse.models.settings.Setting
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.greenrobot.eventbus.EventBus
import java.time.Duration
import java.util.Date
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer


class PulseService : Service() {
    companion object {
        const val POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS"
        const val NOTIFICATION_CHANNEL_ID = "PulseServiceChannel"

        fun start(context: Context) {
            context.startService(Intent(context, PulseService::class.java))
        }

        fun stop(context: Context) {
            if (!context.isServiceRunning(PulseService::class.java)) return
            context.stopService(Intent(context, PulseService::class.java))
        }

        fun startIfNotRunning(context: Context): Boolean {
            if (context.isServiceRunning(PulseService::class.java)) return false
            start(context)
            return true
        }

        fun setupNotificationChannel(context: Context) {
            val manager = ContextCompat.getSystemService(context, NotificationManager::class.java)
            if (manager?.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null)
                return

            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                PulseService::class.simpleName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(false)
                setSound(null, null)
                description = "Pulse Service"
            }
            manager?.createNotificationChannel(channel)
        }
    }

    private var interval = mutableLongStateOf(Setting.RequestInterval.defaultValue)
    private var downloadSize = mutableStateOf(Setting.DownloadSize.defaultObject)
    private lateinit var timer: Timer
    private lateinit var client: OkHttpClient

    //? timestamp: Long , result: Response
    private lateinit var queue: PatientTaskQueue<Long, Response>

    private lateinit var notification: Notification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runBlocking {
            interval.longValue = Setting.RequestInterval.get(this@PulseService)
            downloadSize.value = Setting.DownloadSize.getObject(this@PulseService)
        }

        val ms = TimeUnit.MILLISECONDS
        client = OkHttpClient.Builder()
            .connectTimeout(interval.longValue, ms)
            //            .callTimeout(interval, ms)
            //            .readTimeout(interval, ms)
            .build()

        cleanupDatabase()
        initQueue()
        initTimer()
        showNotification()
        return START_STICKY
    }

    private fun cleanupDatabase() {
        //        val monthAgo = 1000L * 60 * 60 * 24 * 30
        //        val job = Job()
        //        CoroutineScope(Dispatchers.IO + job).launch {
        //            LocalDatabase.getDB(this@PulseService).apply {
        //                speedRecordsDAO().deleteOlderThan(Date().time - monthAgo)
        //            }.close()
        //        }
        //        job.cancel()
    }

    private fun initQueue() {
        queue = PatientTaskQueue(
            null, null, null, null,
            ::onDoneListener, ::onTimeoutListener, ::onErrorListener, ::onFinallyListener
        )
    }

    private fun initTimer() {
        this.timer = timer("PulseServiceTimer", false, 0, interval.longValue) {
            queue.addRunRoll(
                System.currentTimeMillis(),
                Duration.ofMillis(interval.longValue)
            ) {
                Log.d("PulseService", "Running request")
                val req = Request.Builder().url(downloadSize.value.url).build()
                val call = client.newCall(req)
                return@addRunRoll call.execute()
            }
        }
    }

    private fun showNotification() {
        NotificationManagerCompat.from(this).apply {
            val permission =
                ActivityCompat.checkSelfPermission(applicationContext, POST_NOTIFICATIONS)
            if (permission != PackageManager.PERMISSION_GRANTED)
                return@apply

            notification = NotificationCompat
                .Builder(this@PulseService, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Pulse")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(0L))
                .setSound(null)
                .setOngoing(true)
                .build()

            notify(R.id.pulse_notification, notification)
        }
    }

    //? PatientTaskQueue

    private fun event(ev: Any) = EventBus.getDefault().post(ev)

    interface PulseEvent {
        class AnyEvent(val ev: PulseEvent) : PulseEvent
        data class DoneEvent(val record: SpeedRecord) : PulseEvent

        data class OtherEvent(
            val timestamp: Long,
            val record: SpeedRecord,
            val response: Response?,
            val runtime: Duration
        ) : PulseEvent

        data class TimeoutEvent(
            val key: Long,
            val record: SpeedRecord,
            val timeout: Duration,
            val startedAt: Date
        ) : PulseEvent

        data class ErrorEvent(
            val key: Long,
            val record: SpeedRecord,
            val error: Exception,
            val startedAt: Date,
            val runtime: Duration
        ) : PulseEvent
    }


    private fun onDoneListener(ev: PatientTaskEvent.TaskDone<Long, Response>) {
        onResponse(ev.key, ev.value!!, ev.runtime)
    }

    private fun onTimeoutListener(ev: PatientTaskEvent.TaskTimeout<Long>) {
        val record =
            SpeedRecord(
                ev.key,
                SpeedRecordStatus.Timeout.toInt(),
                ev.timeout.toMillis().toInt(),
                null,
                null
            )
        LocalDatabase.getDB(this).apply {
            speedRecordsDAO().insert(record)
        }.close()
        event(PulseEvent.TimeoutEvent(ev.key, record, ev.timeout, ev.startedAt))
    }

    private fun onErrorListener(ev: PatientTaskEvent.TaskError<Long>) {
        //? only IOException by OkHttp's call.execute() indicates a failed request,
        //? the rest are accidental and should be fixed
        if (ev.error !is IOException) throw ev.error

        val record = SpeedRecordUtils.error(ev.key, ev.runtime.toMillis().toInt())
        LocalDatabase.getDB(this).apply {
            speedRecordsDAO().insert(record)
        }.close()
        event(PulseEvent.ErrorEvent(ev.key, record, ev.error, ev.startedAt, ev.runtime))
    }

    private fun onFinallyListener(ev: PatientTaskEvent.TaskFinally<Long, Response>) {

    }

    //? Request

    private fun onResponse(timestamp: Long, response: Response?, runtime: Duration) {
        if (response?.isSuccessful == true)
            onResponseOk(timestamp, response, runtime)
        else onResponseOther(timestamp, response, runtime)
    }

    private fun onResponseOk(timestamp: Long, response: Response, runtime: Duration) {
        val record = SpeedRecordUtils.success(
            timestamp,
            runtime.toMillis().toInt(),
            0f,
            SpeedRecordUtils.getDownSpeed(response, runtime.toMillis().toInt())
        )

        LocalDatabase.getDB(this).apply {
            speedRecordsDAO().insert(record)
        }.close()

        event(PulseEvent.DoneEvent(record))
    }

    private fun onResponseOther(timestamp: Long, response: Response?, runtime: Duration) {
        val runtimeMS = runtime.toMillis().toInt()
        val record: SpeedRecord =
            if (response == null) SpeedRecord(timestamp, 0, runtimeMS, null, null)
            else SpeedRecord(timestamp, response.code, runtimeMS, 0f, 0f)

        LocalDatabase.getDB(this).apply {
            speedRecordsDAO().insert(record)
        }.close()
        event(PulseEvent.OtherEvent(timestamp, record, response, runtime))
    }


    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
