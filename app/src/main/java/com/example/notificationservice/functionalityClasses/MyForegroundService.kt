package com.example.notificationservice.functionalityClasses

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.notificationservice.MainActivity
import com.example.notificationservice.R


class MyForegroundService : Service() {
    // Define variables and objects needed for your foreground service
    var NOTIFICATION_ID = 111
    override fun onCreate() {
        super.onCreate()
        // Perform initialization tasks for your foreground service
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Start your foreground service and perform tasks here

        // Create and configure the notification for your foreground service
        val tx = Thread {
            var timer: Long = 0
            while (true) {
                Log.d("log-foreground-service", "service running from $timer seconds")
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    Log.d("log-foreground-service-exception", e.toString())
                }
                timer += 5000
            }
        }
        tx.start()
        // Start the foreground service with a notification
//        Notification notification = createNotification();
//        startForeground(NOTIFICATION_ID, notification);

        // Return the appropriate flag based on your requirements
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up resources and perform any necessary cleanup tasks
    }

    override fun onBind(intent: Intent): IBinder? {
        // If your service does not support binding, return null
        return null
    }

    // Create a notification for your foreground service
    private fun createNotification(): Notification {
        // Create a notification using the NotificationCompat.Builder
        val CHANNEL_ID = "channel_name"
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("My Foreground Service")
            .setContentText("Running...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Create a PendingIntent for the notification (optional)
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        builder.setContentIntent(pendingIntent)

        // Build and return the notification
        return builder.build()
    }
}