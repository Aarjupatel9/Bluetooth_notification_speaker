package com.example.notificationservice.services

import android.annotation.SuppressLint
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.util.Log
import androidx.annotation.RequiresApi

class MyNotificationListener : NotificationListenerService() {
    lateinit var logManager: LogManager


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (applicationContext == null) {
            return
        }
        logManager = LogManager(applicationContext)


        val isOngoingCall = sbn.notification?.flags?.and(Notification.FLAG_ONGOING_EVENT) != 0
        val isForegroundService =
            sbn.notification?.flags?.and(Notification.FLAG_FOREGROUND_SERVICE) != 0


        val packageName = sbn.packageName
        val opPkg = sbn.opPkg
        val packageManager = applicationContext.packageManager
        val applicationInfo = try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        val applicationName = if (applicationInfo != null) {
            packageManager.getApplicationLabel(applicationInfo).toString()
        } else {
            packageName // Use package name if application name is not available
        }

        val notificationText = sbn.notification?.tickerText?.toString() ?: ""

        val textToSpeek = extractAppNameFromPackageName(packageName)

        Log.d(
            "NotificationListener",
            "applicationName: $applicationName,  opPkg: $opPkg,  textToSpeek: $textToSpeek,   Text: $notificationText"
        )
        Log.d(
            "NotificationListener",
            "textToSpeek: $textToSpeek,  isClearable: ${sbn.isClearable},  id: ${sbn.id},   Tag: ${sbn.tag},  falgs: ${sbn.notification.flags}"
        )

//        if (isForegroundService || isOngoingCall) {
//            logManager.saveLog(
//                "isForegroundService: $isForegroundService , isOngoingCall: $isOngoingCall",
//                1
//            )
//        }

        if (isBluetoothConnected() && !isForegroundService && !isOngoingCall) {
            var pass: Boolean=true;
            when(textToSpeek){
                "music"->{
                    pass=false
                }
                "whatsapp"->{
                    if(sbn.tag==null){
                        pass=false
                    }
                }
                else -> pass=true
            }

            if(pass){
                Log.d("NotificationListener", "bluetooth connected")
                logManager.saveLog("bluetooth connected , packageName: ${sbn.packageName}", 1)
                var textToSpeechManager =
                    TextToSpeechManager(applicationContext, textToSpeek);
            }
        }
        logManager.saveLog(
            "textToSpeek: $textToSpeek,  isClearable: ${sbn.isClearable},  id: ${sbn.id},   Tag: ${sbn.tag},  falgs: ${sbn.notification.flags}, isForegroundService: $isForegroundService , isOngoingCall: $isOngoingCall",
            1
        )

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Handle removed notifications here
    }

    fun extractAppNameFromPackageName(packageName: String): String {
        val segments = packageName.split(".")
        return when {
            segments.size == 2 -> segments.last()
            segments.size >= 3 -> segments.subList(2, segments.size).joinToString(".")
            else -> packageName // Return the original package name if no segments are found
        }
    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    fun isBluetoothConnected(): Boolean {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        val profiles = intArrayOf(
            BluetoothProfile.A2DP,   // Advanced Audio Distribution Profile (for audio streaming)
            BluetoothProfile.HEADSET // Headset Profile (for audio streaming and voice calls)
            // Add more profiles as needed
        )

        for (profile in profiles) {
            val connectionState = bluetoothAdapter?.getProfileConnectionState(profile)
            if (connectionState == BluetoothProfile.STATE_CONNECTED) {
                return true
            }
        }
        return false
    }
}
