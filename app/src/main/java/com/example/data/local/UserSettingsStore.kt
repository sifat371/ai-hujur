package com.example.data.local

import android.content.Context
import com.example.data.service.LocationService
import com.example.data.service.UserLocationInfo

/** Settings readable by both the app and boot/alarm receivers. */
class UserSettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("al_hujur_user_settings", Context.MODE_PRIVATE)
    var prayerNotificationsEnabled: Boolean
        get() = prefs.getBoolean("prayer_notifications", false)
        set(v) { prefs.edit().putBoolean("prayer_notifications", v).apply() }
    var aiDailyRemindersEnabled: Boolean
        get() = prefs.getBoolean("ai_reminders", false)
        set(v) { prefs.edit().putBoolean("ai_reminders", v).apply() }
    var userName: String
        get() = prefs.getString("user_name", "অতিথি ব্যবহারকারী") ?: "অতিথি ব্যবহারকারী"
        set(v) { prefs.edit().putString("user_name", v).apply() }
    var spiritualGoal: String
        get() = prefs.getString("spiritual_goal", "নিয়মিত আমল করা") ?: "নিয়মিত আমল করা"
        set(v) { prefs.edit().putString("spiritual_goal", v).apply() }
    var hijriOffsetDays: Int
        get() = prefs.getInt("hijri_offset_days", 0).coerceIn(-2,2)
        set(v) { prefs.edit().putInt("hijri_offset_days", v.coerceIn(-2,2)).apply() }
    var location: UserLocationInfo
        get() {
            val fallback = LocationService.DHAKA
            val lat = prefs.getString("latitude", null)?.toDoubleOrNull() ?: return fallback
            val lon = prefs.getString("longitude", null)?.toDoubleOrNull() ?: return fallback
            if (!lat.isFinite() || !lon.isFinite() || lat !in -90.0..90.0 || lon !in -180.0..180.0) return fallback
            return UserLocationInfo(lat, lon, prefs.getString("location_name", fallback.cityName) ?: fallback.cityName,
                prefs.getBoolean("auto_detected_location", false))
        }
        set(v) {
            require(v.latitude.isFinite() && v.longitude.isFinite() && v.latitude in -90.0..90.0 && v.longitude in -180.0..180.0)
            prefs.edit().putString("latitude", v.latitude.toString()).putString("longitude", v.longitude.toString())
                .putString("location_name", v.cityName).putBoolean("auto_detected_location", v.isAutoDetected).apply()
        }
}
