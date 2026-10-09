package com.example.data.local

import android.content.Context
import com.example.data.service.LocationService
import com.example.data.service.UserLocationInfo

/** Settings shared by the UI and broadcast receivers after process death or reboot. */
class UserSettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "al_hujur_user_settings", Context.MODE_PRIVATE
    )
    var prayerNotificationsEnabled: Boolean
        get() = prefs.getBoolean("prayer_notifications", false)
        set(value) { prefs.edit().putBoolean("prayer_notifications", value).apply() }
    var aiDailyRemindersEnabled: Boolean
        get() = prefs.getBoolean("ai_reminders", false)
        set(value) { prefs.edit().putBoolean("ai_reminders", value).apply() }
    var userName: String
        get() = prefs.getString("user_name", "ব্যবহারকারী") ?: "ব্যবহারকারী"
        set(value) { prefs.edit().putString("user_name", value).apply() }
    var spiritualGoal: String
        get() = prefs.getString("spiritual_goal", "প্রতিদিন নিয়মিত আমল করা") ?: "প্রতিদিন নিয়মিত আমল করা"
        set(value) { prefs.edit().putString("spiritual_goal", value).apply() }
    var calculationMethod: String
        get() = prefs.getString("calculation_method", "ইসলামিক ফাউন্ডেশন বাংলাদেশ (হানাফী)")
            ?: "ইসলামিক ফাউন্ডেশন বাংলাদেশ (হানাফী)"
        set(value) { prefs.edit().putString("calculation_method", value).apply() }
    var hijriOffsetDays: Int
        get() = prefs.getInt("hijri_offset_days", 0).coerceIn(-2, 2)
        set(value) { prefs.edit().putInt("hijri_offset_days", value.coerceIn(-2, 2)).apply() }
    var location: UserLocationInfo
        get() {
            val fallback = LocationService.DHAKA
            val lat = prefs.getString("latitude", null)?.toDoubleOrNull() ?: return fallback
            val lon = prefs.getString("longitude", null)?.toDoubleOrNull() ?: return fallback
            if (!lat.isFinite() || !lon.isFinite() ||
                lat !in -90.0..90.0 || lon !in -180.0..180.0) return fallback
            return UserLocationInfo(lat, lon,
                prefs.getString("location_name", fallback.cityName) ?: fallback.cityName,
                prefs.getBoolean("auto_detected_location", false))
        }
        set(value) {
            require(value.latitude.isFinite() && value.longitude.isFinite() &&
                value.latitude in -90.0..90.0 && value.longitude in -180.0..180.0)
            prefs.edit().putString("latitude", value.latitude.toString())
                .putString("longitude", value.longitude.toString())
                .putString("location_name", value.cityName)
                .putBoolean("auto_detected_location", value.isAutoDetected)
                .apply()
        }
}
