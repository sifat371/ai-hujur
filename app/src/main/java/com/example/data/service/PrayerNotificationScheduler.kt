package com.example.data.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import java.util.Calendar
import java.util.Locale

/**
 * Service responsible for scheduling local device alarms exactly 10 minutes
 * before each calculated Islamic prayer time (Fajr, Dhuhr, Asr, Maghrib, Isha).
 */
object PrayerNotificationScheduler {

    private const val TAG = "PrayerScheduler"

    const val CHANNEL_ID = "al_hujur_prayer_alerts_10min"
    const val CHANNEL_NAME = "নামাজের ওয়াক্ত সতর্কতা (১০ মিনিট পূর্বে)"
    const val CHANNEL_DESC = "নামাজের ওয়াক্ত শুরু হওয়ার ঠিক ১০ মিনিট পূর্বে স্থানীয় নোটিফিকেশন প্রদান করে"

    const val ACTION_PRAYER_ALERT = "com.example.action.PRAYER_ALERT_10MIN"

    const val EXTRA_PRAYER_ID = "extra_prayer_id"
    const val EXTRA_PRAYER_NAME = "extra_prayer_name"
    const val EXTRA_PRAYER_TIME_STR = "extra_prayer_time_str"
    const val EXTRA_PRAYER_ARABIC = "extra_prayer_arabic"
    const val EXTRA_LOCATION_NAME = "extra_location_name"
    const val EXTRA_LATITUDE = "extra_latitude"
    const val EXTRA_LONGITUDE = "extra_longitude"

    const val ID_FAJR = 101
    const val ID_DHUHR = 102
    const val ID_ASR = 103
    const val ID_MAGHRIB = 104
    const val ID_ISHA = 105
    const val ID_TEST = 999

    /**
     * Ensures the Notification Channel is created on Android 8.0 (API 26) and above.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return

            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    lightColor = android.graphics.Color.parseColor("#D4AF37") // Islamic Gold
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 400, 200, 400)
                    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created: $CHANNEL_ID")
            }
        }
    }

    /**
     * Schedules alarms 10 minutes prior to each of the 5 daily prayer times.
     * Uses calculated prayer times based on current location coordinates.
     */
    fun scheduleAllPrayerAlerts(
        context: Context,
        latitude: Double = LocationService.DHAKA.latitude,
        longitude: Double = LocationService.DHAKA.longitude,
        locationName: String = LocationService.DHAKA.cityName
    ) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val now = System.currentTimeMillis()
        val calendarToday = Calendar.getInstance()
        val calendarTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val todaySchedule = PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            calendar = calendarToday,
            locationName = locationName
        )

        val tomorrowSchedule = PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            calendar = calendarTomorrow,
            locationName = locationName
        )

        // 5 Daily Obligatory Prayers
        val prayers = listOf(
            PrayerScheduleItem(ID_FAJR, "ফজর", "الفجر", todaySchedule.fajrMinutes, tomorrowSchedule.fajrMinutes, todaySchedule.formatTime(todaySchedule.fajrMinutes)),
            PrayerScheduleItem(ID_DHUHR, "যোহর", "الظهر", todaySchedule.dhuhrMinutes, tomorrowSchedule.dhuhrMinutes, todaySchedule.formatTime(todaySchedule.dhuhrMinutes)),
            PrayerScheduleItem(ID_ASR, "আসর", "العصر", todaySchedule.asrMinutes, tomorrowSchedule.asrMinutes, todaySchedule.formatTime(todaySchedule.asrMinutes)),
            PrayerScheduleItem(ID_MAGHRIB, "মাগরিব", "المغرب", todaySchedule.maghribMinutes, tomorrowSchedule.maghribMinutes, todaySchedule.formatTime(todaySchedule.maghribMinutes)),
            PrayerScheduleItem(ID_ISHA, "এশা", "العشاء", todaySchedule.ishaMinutes, tomorrowSchedule.ishaMinutes, todaySchedule.formatTime(todaySchedule.ishaMinutes))
        )

        for (prayer in prayers) {
            // Trigger time: 10 minutes before prayer time
            val triggerTimeToday = getTriggerTimeMs(calendarToday, prayer.todayMinutes - 10)

            val (targetTimeMs, prayerTimeStr) = if (triggerTimeToday > now) {
                Pair(triggerTimeToday, prayer.todayFormattedTime)
            } else {
                val triggerTomorrow = getTriggerTimeMs(calendarTomorrow, prayer.tomorrowMinutes - 10)
                val tomorrowFormatted = tomorrowSchedule.formatTime(prayer.tomorrowMinutes)
                Pair(triggerTomorrow, tomorrowFormatted)
            }

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALERT
                putExtra(EXTRA_PRAYER_ID, prayer.id)
                putExtra(EXTRA_PRAYER_NAME, prayer.nameBn)
                putExtra(EXTRA_PRAYER_TIME_STR, prayerTimeStr)
                putExtra(EXTRA_PRAYER_ARABIC, prayer.nameAr)
                putExtra(EXTRA_LOCATION_NAME, locationName)
                putExtra(EXTRA_LATITUDE, latitude)
                putExtra(EXTRA_LONGITUDE, longitude)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                prayer.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            targetTimeMs,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            targetTimeMs,
                            pendingIntent
                        )
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        targetTimeMs,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        targetTimeMs,
                        pendingIntent
                    )
                }
                Log.d(TAG, "Scheduled 10-min alert for ${prayer.nameBn} at: $targetTimeMs")
            } catch (e: SecurityException) {
                Log.e(TAG, "Exact alarm permission denied, falling back to standard alarm: ${e.message}")
                alarmManager.set(AlarmManager.RTC_WAKEUP, targetTimeMs, pendingIntent)
            }
        }
    }

    /**
     * Cancels all scheduled prayer reminder alarms.
     */
    fun cancelAllPrayerAlerts(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prayerIds = listOf(ID_FAJR, ID_DHUHR, ID_ASR, ID_MAGHRIB, ID_ISHA)

        for (id in prayerIds) {
            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                action = ACTION_PRAYER_ALERT
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }
        Log.d(TAG, "Cancelled all prayer reminder alarms")
    }

    /**
     * Sends an immediate test notification so users can verify alert appearance,
     * sound, and vibration on their device.
     */
    fun sendInstantTestAlert(
        context: Context,
        prayerName: String = "যোহর",
        prayerTimeStr: String = "০১:১৫ অপরাহ্ন"
    ) {
        createNotificationChannel(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            ID_TEST,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("নামাজের ওয়াক্ত সতর্কতা: আর ১০ মিনিট বাকি")
            .setContentText("আসন্ন $prayerName ওয়াক্ত শুরু হতে ১০ মিনিট বাকি ($prayerTimeStr)। ওজু করে নামাজের প্রস্তুতি নিন।")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "আসন্ন $prayerName ওয়াক্ত শুরু হতে ১০ মিনিট বাকি ($prayerTimeStr)।\n" +
                                "রাসূলুল্লাহ ﷺ বলেছেন: 'নামাজ হলো দ্বীনের খুঁটি।' এখনই সব পার্থিব ব্যস্ততা স্থগিত রেখে ওজু সম্পন্ন করুন এবং প্রথম তাকবীরের সাথে জামাতে নামাজের প্রস্তুতি নিন।"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "অ্যাপ খুলুন", pendingIntent)
            .setColor(android.graphics.Color.parseColor("#D4AF37"))
            .setVibrate(longArrayOf(0, 400, 200, 400))
            .build()

        notificationManager.notify(ID_TEST, notification)
    }

    private fun getTriggerTimeMs(baseCalendar: Calendar, minutesFromMidnight: Int): Long {
        val cal = baseCalendar.clone() as Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.MINUTE, minutesFromMidnight)
        return cal.timeInMillis
    }

    private data class PrayerScheduleItem(
        val id: Int,
        val nameBn: String,
        val nameAr: String,
        val todayMinutes: Int,
        val tomorrowMinutes: Int,
        val todayFormattedTime: String
    )
}
