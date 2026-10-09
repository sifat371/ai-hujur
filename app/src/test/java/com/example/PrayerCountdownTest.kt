package com.example

import com.example.data.repository.IslamicRepository
import com.example.data.service.PrayerTimeCalculatorService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

/** Tests a regression where late-night countdown accidentally used today's Fajr. */
class PrayerCountdownTest {
    private fun localCalendar(year: Int, month: Int, day: Int, hour: Int): Calendar =
        Calendar.getInstance(TimeZone.getTimeZone("Asia/Dhaka")).apply {
            set(year, month - 1, day, hour, 30, 0)
            set(Calendar.MILLISECOND, 0)
        }

    @Test
    fun afterIshaCountsDownToFollowingDayFajr() {
        val atNight = localCalendar(2026, 10, 9, 23)
        val tomorrow = (atNight.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        val tomorrowFajr = PrayerTimeCalculatorService.calculatePrayerTimes(
            24.3745, 88.6042, tomorrow
        ).fajrMinutes
        val expectedSeconds = 30L * 60L + tomorrowFajr * 60L
        val (label, _, seconds) = IslamicRepository.getNextPrayerCountdown(
            24.3745, 88.6042, "রাজশাহী", atNight
        )
        assertTrue(label.contains("ফজর"))
        assertEquals(expectedSeconds, seconds)
    }

    @Test
    fun calculatedBangladeshTimesStayInExpectedOrder() {
        for ((lat, lon) in listOf(23.8103 to 90.4125, 24.3745 to 88.6042)) {
            for (month in listOf(1, 7)) {
                val schedule = PrayerTimeCalculatorService.calculatePrayerTimes(
                    lat, lon, localCalendar(2026, month, 15, 12)
                )
                assertTrue(schedule.fajrMinutes < schedule.sunriseMinutes)
                assertTrue(schedule.sunriseMinutes < schedule.dhuhrMinutes)
                assertTrue(schedule.dhuhrMinutes < schedule.asrMinutes)
                assertTrue(schedule.asrMinutes < schedule.maghribMinutes)
                assertTrue(schedule.maghribMinutes < schedule.ishaMinutes)
            }
        }
    }
}
