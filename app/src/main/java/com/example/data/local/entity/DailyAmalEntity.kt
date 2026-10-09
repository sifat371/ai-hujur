package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the daily aggregated record of Islamic practices (Amal).
 * Tracks the 5 daily obligatory prayers, Sunnah/Nafl prayers, fasting,
 * charity, daily Adhkar, Quran progress, and Dhikr counts.
 */
@Entity(tableName = "daily_amal")
data class DailyAmalEntity(
    @PrimaryKey
    val date: String, // Format: "yyyy-MM-dd"
    val fajrDone: Boolean = false,
    val dhuhrDone: Boolean = false,
    val asrDone: Boolean = false,
    val maghribDone: Boolean = false,
    val ishaDone: Boolean = false,
    val taraweehDone: Boolean = false,
    val tahajjudDone: Boolean = false,
    val fastingDone: Boolean = false,
    val charityDone: Boolean = false,
    val morningAdhkarDone: Boolean = false,
    val eveningAdhkarDone: Boolean = false,
    val quranJuz: Int = 1,
    val quranSurah: String = "সূরা আল-ফাতিহা",
    val quranPagesReadToday: Int = 0,
    val quranDailyGoalPages: Int = 20,
    val dhikrTotalCount: Int = 0,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun prayerCompletionCount(): Int {
        var count = 0
        if (fajrDone) count++
        if (dhuhrDone) count++
        if (asrDone) count++
        if (maghribDone) count++
        if (ishaDone) count++
        return count
    }

    fun overallProgressPercent(): Float {
        val totalItems = 7f
        var done = prayerCompletionCount().toFloat()
        if (taraweehDone) done += 1f
        if (fastingDone) done += 1f
        return (done / totalItems).coerceIn(0f, 1f)
    }
}
