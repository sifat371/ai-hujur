package com.example.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Reschedules all 10-minute local prayer reminder alarms when the device boots up.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            Log.d("BootCompletedReceiver", "Device reboot detected, restoring prayer alerts")
            try {
                PrayerNotificationScheduler.scheduleAllPrayerAlerts(
                    context = context,
                    latitude = LocationService.DHAKA.latitude,
                    longitude = LocationService.DHAKA.longitude,
                    locationName = LocationService.DHAKA.cityName
                )
            } catch (e: Exception) {
                Log.e("BootCompletedReceiver", "Error restoring prayer alarms: ${e.message}")
            }
        }
    }
}
