package com.example.notificationservice

import android.Manifest
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.notificationservice.services.MyBroadcastReceiver
import com.example.notificationservice.services.MyJobService


class MainActivity : Activity() {
    private val TAG = "mainActivity";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main);
        if (!isNotificationServiceEnabled(this)) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val PERMISSIONS_MUST_REQUIRED = arrayOf(
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH,
            )
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS_MUST_REQUIRED, 102
            )
        }


        scheduleJob()

        val filter1 = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(MyBroadcastReceiver(), filter1)

    }

    private fun scheduleJob() {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(this, MyJobService::class.java)
        val jobInfo = JobInfo.Builder(1, componentName)
            .setPeriodic(20 * 60 * 1000) // 15 minutes (minimum allowed interval)
            .setPersisted(true) // Job will survive device reboots
            .build()
        val resultCode = jobScheduler.schedule(jobInfo)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled successfully!")
        } else {
            Log.e(TAG, "Job scheduling failed!")
        }
    }

    private fun isNotificationServiceEnabled(c: Context): Boolean {
        val pkgName = c.packageName
        val flat: String = Settings.Secure.getString(
            c.contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

}