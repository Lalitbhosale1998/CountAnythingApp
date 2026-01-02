package com.lalit.countanything

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Reschedule checking preference
            val settingsManager = SettingsManager(context)
            CoroutineScope(Dispatchers.IO).launch {
                val isEnabled = settingsManager.isDailyReminderEnabled.first()
                if (isEnabled) {
                    NotificationScheduler.scheduleDailyReminder(context)
                }
            }
            return
        }

        // Proceed to show notification
        showNotification(context)
        
        // NEW: Check for events
        CoroutineScope(Dispatchers.IO).launch {
            checkAndShowEventNotifications(context)
        }
    }

    private fun showNotification(context: Context) {
        // Check permission again just in case
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Can't post notification without permission
            return
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Fallback to launcher icon. Ideally should be a silhouette.
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(2001, builder.build())
        }
    }
    private suspend fun checkAndShowEventNotifications(context: Context) {
        val events = StorageHelper.loadEvents(context)
        val today = java.time.LocalDate.now()
        
        events.forEach { event ->
            val eventDate = java.time.LocalDate.parse(event.date)
            val isToday = if (event.isRecurring) {
                eventDate.month == today.month && eventDate.dayOfMonth == today.dayOfMonth
            } else {
                eventDate == today
            }
            
            if (isToday) {
                 showEventNotification(context, event)
            }
        }
    }

    private fun showEventNotification(context: Context, event: StorageHelper.Event) {
         if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            event.id.hashCode(), // Unique Request Code
            tapIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(event.title)
            .setContentText("Happening Today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(event.id.hashCode(), builder.build())
        }
    }
}
