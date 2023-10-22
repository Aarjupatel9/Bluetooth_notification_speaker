package com.example.notificationservice.services

import android.content.Context
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

class LogManager(context : Context) {
    val externalFilesDir = context.getExternalFilesDir(null)
    val logFile1 = File(externalFilesDir, "notification_listener_logs.txt")
    val logFile2 = File(externalFilesDir, "job_scheduler_log.txt")
    val lofDefault = File(externalFilesDir, "default_log.txt")

    fun saveLog(logMessage: String, type: Int) {
        var fos: FileOutputStream;
        val text = "$\n$logMessage"
        try {
            if (type == 1) {
                fos = FileOutputStream(logFile1, true) // Open the file in append mode
            } else if (type == 2) {
                fos = FileOutputStream(logFile2, true) // Open the file in append mode
            } else {
                fos = FileOutputStream(lofDefault, true) // Open the file in append mode
            }
            fos.write(text.toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}