package com.example.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.data.local.UserSettingsStore

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
                val settings = UserSettingsStore(context)
                if (!settings.prayerNotificationsEnabled) {
                    PrayerNotificationScheduler.cancelAllPrayerAlerts(context)
                    return
                }
                val location = settings.location
                PrayerNotificationScheduler.scheduleAllPrayerAlerts(
                    context = context,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationName = location.cityName
                )
            } catch (e: Exception) {
                Log.e("BootCompletedReceiver", "Error restoring prayer alarms: ${e.message}")
            }
        }
    }
}
