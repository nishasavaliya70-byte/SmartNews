package com.smartnewsai.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.smartnewsai.MainActivity
import com.smartnewsai.R

class SmartNewsNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID_BREAKING = "smartnews_breaking_channel"
        const val CHANNEL_NAME_BREAKING = "Breaking News & Critical Updates"
        const val NOTIFICATION_ID_BASE = 1001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_BREAKING,
                CHANNEL_NAME_BREAKING,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts for verified breaking news and emerging topics"
                enableLights(true)
                enableVibration(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showBreakingNewsNotification(title: String, message: String, newsId: String? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("news_id", newsId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BREAKING)
            .setSmallIcon(R.drawable.ic_smartnews_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID_BASE + (0..999).random(), notification)
    }
}
