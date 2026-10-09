package com.example.data.service

import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

/**
 * Astronomical Prayer Times Calculator Service
 * Computes exact daily Islamic prayer times (Fajr, Sunrise, Dhuhr, Asr, Maghrib, Isha)
 * based on geographical coordinates (latitude & longitude), elevation, and date.
 * Implements standard conventions (including Karachi/Islamic Foundation 18° twilight angles and Hanafi/Shafi'i Asr).
 */
object PrayerTimeCalculatorService {

    data class CalculatedPrayerSchedule(
        val fajrMinutes: Int,
        val sunriseMinutes: Int,
        val dhuhrMinutes: Int,
        val asrMinutes: Int,
        val maghribMinutes: Int,
        val ishaMinutes: Int,
        val date: Date,
        val latitude: Double,
        val longitude: Double,
        val locationName: String
    ) {
        fun formatTime(minutes: Int): String {
            val totalMinutes = (minutes + 1440) % 1440
            val hours = totalMinutes / 60
            val mins = totalMinutes % 60
            val amPm = if (hours >= 12) "অপরাহ্ন" else "পূর্বাহ্ন"
            val displayHour = if (hours % 12 == 0) 12 else hours % 12
            return String.format(Locale.US, "%02d:%02d %s", displayHour, mins, amPm)
        }

        fun toPrayerMinutesList(): List<Pair<String, Int>> = listOf(
            "ফজর" to fajrMinutes,
            "সূর্যোদয়" to sunriseMinutes,
            "যোহর" to dhuhrMinutes,
            "আসর" to asrMinutes,
            "মাগরিব" to maghribMinutes,
            "এশা" to ishaMinutes
        )
    }

    /**
     * Calculates astronomical prayer times for given latitude, longitude, and date.
     * @param latitude North positive, South negative
     * @param longitude East positive, West negative
     * @param fajrAngle twilight angle in degrees (e.g., 18.0° for Islamic Foundation Bangladesh / Karachi)
     * @param ishaAngle twilight angle in degrees (e.g., 18.0° for Islamic Foundation Bangladesh / Karachi)
     * @param asrJuristic 1 for Shafi'i/Standard (shadow factor 1), 2 for Hanafi (shadow factor 2, default in Bangladesh)
     */
    fun calculatePrayerTimes(
        latitude: Double,
        longitude: Double,
        calendar: Calendar = Calendar.getInstance(),
        locationName: String = "বর্তমান অবস্থান",
        fajrAngle: Double = 18.0,
        ishaAngle: Double = 18.0,
        asrJuristicFactor: Double = 2.0 // Hanafi default for Bangladesh
    ): CalculatedPrayerSchedule {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val timeZoneOffsetHours = calendar.timeZone.getOffset(calendar.timeInMillis) / 3600000.0

        // Julian Day computation
        val jd = computeJulianDay(year, month, day) - (longitude / (15.0 * 24.0))

        // Solar coordinates
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * sinDeg(g) + 0.020 * sinDeg(2 * g))

        val e = 23.439 - 0.00000036 * d
        val declination = asinDeg(sinDeg(e) * sinDeg(l))
        val ra = fixHour(atan2Deg(cosDeg(e) * sinDeg(l), cosDeg(l)) / 15.0)
        val eqT = (q / 15.0) - ra

        // Solar Noon (Dhuhr) in hours (Local Time)
        val noon = fixHour(12.0 + timeZoneOffsetHours - (longitude / 15.0) - eqT)

        // Sunrise & Sunset angle (standard atmospheric refraction -0.833°)
        val sunriseAngle = 0.833
        val sunriseHourDiff = sunHourAngle(sunriseAngle, latitude, declination)
        val sunrise = if (!sunriseHourDiff.isNaN()) noon - sunriseHourDiff else noon - 6.0
        val sunset = if (!sunriseHourDiff.isNaN()) noon + sunriseHourDiff else noon + 6.0

        // Fajr (twilight angle below horizon)
        val fajrHourDiff = sunHourAngle(fajrAngle, latitude, declination)
        val fajr = if (!fajrHourDiff.isNaN()) noon - fajrHourDiff else noon - 7.2

        // Isha (twilight angle below horizon)
        val ishaHourDiff = sunHourAngle(ishaAngle, latitude, declination)
        val isha = if (!ishaHourDiff.isNaN()) noon + ishaHourDiff else noon + 7.2

        // Asr calculation (shadow length = object shadow + juristicFactor * object height)
        val asrAngle = -atanDeg(1.0 / (asrJuristicFactor + tanDeg(abs(latitude - declination))))
        val asrHourDiff = sunHourAngle(asrAngle, latitude, declination)
        val asr = if (!asrHourDiff.isNaN()) noon + asrHourDiff else noon + 3.5

        // Maghrib is sunset + small safety margin (~1-2 minutes)
        val maghrib = sunset + (2.0 / 60.0)

        // Convert hours to minutes from midnight
        val toMinutes = { h: Double -> (h * 60.0).roundToInt() }

        return CalculatedPrayerSchedule(
            fajrMinutes = toMinutes(fajr),
            sunriseMinutes = toMinutes(sunrise),
            dhuhrMinutes = toMinutes(noon),
            asrMinutes = toMinutes(asr),
            maghribMinutes = toMinutes(maghrib),
            ishaMinutes = toMinutes(isha),
            date = calendar.time,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName
        )
    }

    private fun computeJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716.0)) + floor(30.6001 * (m + 1.0)) + day + b - 1524.5
    }

    private fun sunHourAngle(angle: Double, latitude: Double, declination: Double): Double {
        val cosH = (sinDeg(-angle) - sinDeg(latitude) * sinDeg(declination)) /
                (cosDeg(latitude) * cosDeg(declination))
        return if (cosH < -1.0 || cosH > 1.0) Double.NaN else acosDeg(cosH) / 15.0
    }

    private fun fixAngle(a: Double): Double {
        var angle = a - 360.0 * floor(a / 360.0)
        if (angle < 0) angle += 360.0
        return angle
    }

    private fun fixHour(h: Double): Double {
        var hour = h - 24.0 * floor(h / 24.0)
        if (hour < 0) hour += 24.0
        return hour
    }

    private fun sinDeg(deg: Double): Double = sin(Math.toRadians(deg))
    private fun cosDeg(deg: Double): Double = cos(Math.toRadians(deg))
    private fun tanDeg(deg: Double): Double = tan(Math.toRadians(deg))
    private fun asinDeg(x: Double): Double = Math.toDegrees(asin(x))
    private fun acosDeg(x: Double): Double = Math.toDegrees(acos(x))
    private fun atanDeg(x: Double): Double = Math.toDegrees(atan(x))
    private fun atan2Deg(y: Double, x: Double): Double = Math.toDegrees(atan2(y, x))
}
