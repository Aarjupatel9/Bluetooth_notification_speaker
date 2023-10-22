package com.example.notificationservice.functionalityClasses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.notificationservice.R

class MyStaticFunctions {
    val CHANNEL_ID = "Notification testing" // The id of the channel.

    //perfect example of notification
    fun showPushNotification(
        context: Context,
        title: String?,
        message: String?,
        intent: Intent?,
        reqCode: Int
    ) {
        /**
         * @param context
         * @param title   --> title to show
         * @param message --> details to show
         * @param intent  --> What should happen on clicking the notification
         * @param reqCode --> unique code for the notification
         */
        val pendingIntent = PendingIntent.getActivity(
            context,
            reqCode,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(
            reqCode,
            notificationBuilder.build()
        ) // 0 is the request code, it should be unique id
        Log.d("showNotification", "showNotification: $reqCode")
    }

    fun createNotificationChannel(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = CHANNEL_ID// The user-visible name of the channel.
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}