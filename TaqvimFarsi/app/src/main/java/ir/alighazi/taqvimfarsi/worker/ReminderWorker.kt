package ir.alighazi.taqvimfarsi.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.glance.appwidget.updateAll
import ir.alighazi.taqvimfarsi.data.EventRepository
import ir.alighazi.taqvimfarsi.widget.TodayWidget
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        TodayWidget().updateAll(applicationContext)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success()
        }
        val events = EventRepository(applicationContext).forDate(LocalDate.now())
        if (events.isNotEmpty()) {
            val manager = applicationContext.getSystemService(NotificationManager::class.java)
            val channelId = "calendar_events"
            if (Build.VERSION.SDK_INT >= 26) manager.createNotificationChannel(
                NotificationChannel(channelId, "یادآور مناسبت‌ها", NotificationManager.IMPORTANCE_DEFAULT))
            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("مناسبت امروز")
                .setContentText(events.joinToString("، ") { it.title })
                .setAutoCancel(true).build()
            manager.notify(1001, notification)
        }
        return Result.success()
    }
}
object ReminderScheduler {
    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_calendar_reminder", ExistingPeriodicWorkPolicy.KEEP, request)
    }
}
