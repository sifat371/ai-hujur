package com.example.data.repository

import com.example.data.local.dao.AmalDao
import com.example.data.local.entity.DailyAmalEntity
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.data.local.entity.SalahLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository providing a clean, unified API to manage and track daily Islamic practices (Amal):
 * - 5 Daily Obligatory Prayers (Fard) & Sunnah/Tahajjud (Salah)
 * - Quran Recitation (Surah, Juz, pages read, reading history)
 * - Dhikr & Tasbeeh (sessions, counts, targets)
 */
class AmalRepository(private val amalDao: AmalDao) {

    fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    // --- DAILY AMAL FLOWS ---

    /**
     * Observes the aggregated Amal record for today. If no entry exists yet,
     * emits an initialized default state for today.
     */
    fun getTodayAmal(): Flow<DailyAmalEntity> {
        val today = getTodayDate()
        return amalDao.getDailyAmal(today).map { record ->
            record ?: DailyAmalEntity(date = today)
        }
    }

    fun getAmalForDate(date: String): Flow<DailyAmalEntity?> {
        return amalDao.getDailyAmal(date)
    }

    fun getAllAmalHistory(): Flow<List<DailyAmalEntity>> {
        return amalDao.getAllDailyAmal()
    }

    fun getRecentAmalHistory(limit: Int = 30): Flow<List<DailyAmalEntity>> {
        return amalDao.getRecentDailyAmal(limit)
    }

    suspend fun saveDailyAmal(record: DailyAmalEntity) {
        amalDao.insertOrUpdateDailyAmal(record.copy(updatedAt = System.currentTimeMillis()))
    }

    // --- SALAH (PRAYER) OPERATIONS ---

    /**
     * Toggles a prayer status for the given date (default today).
     */
    suspend fun togglePrayer(prayer: String, date: String = getTodayDate()) {
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val updated = when (prayer.lowercase().trim()) {
            "fajr" -> existing.copy(fajrDone = !existing.fajrDone)
            "dhuhr" -> existing.copy(dhuhrDone = !existing.dhuhrDone)
            "asr" -> existing.copy(asrDone = !existing.asrDone)
            "maghrib" -> existing.copy(maghribDone = !existing.maghribDone)
            "isha" -> existing.copy(ishaDone = !existing.ishaDone)
            "taraweeh" -> existing.copy(taraweehDone = !existing.taraweehDone)
            "tahajjud" -> existing.copy(tahajjudDone = !existing.tahajjudDone)
            "fasting" -> existing.copy(fastingDone = !existing.fastingDone)
            "charity" -> existing.copy(charityDone = !existing.charityDone)
            "morning_adhkar" -> existing.copy(morningAdhkarDone = !existing.morningAdhkarDone)
            "evening_adhkar" -> existing.copy(eveningAdhkarDone = !existing.eveningAdhkarDone)
            else -> existing
        }
        amalDao.insertOrUpdateDailyAmal(updated.copy(updatedAt = System.currentTimeMillis()))

        // Also track individual salah log if it was an obligatory/sunnah prayer
        val prayerKey = prayer.lowercase().trim()
        if (prayerKey in listOf("fajr", "dhuhr", "asr", "maghrib", "isha", "taraweeh", "tahajjud")) {
            val isNowDone = when (prayerKey) {
                "fajr" -> updated.fajrDone
                "dhuhr" -> updated.dhuhrDone
                "asr" -> updated.asrDone
                "maghrib" -> updated.maghribDone
                "isha" -> updated.ishaDone
                "taraweeh" -> updated.taraweehDone
                "tahajjud" -> updated.tahajjudDone
                else -> false
            }
            if (isNowDone) {
                amalDao.insertSalahLog(
                    SalahLogEntity(
                        date = date,
                        prayerName = prayer.replaceFirstChar { it.uppercase() },
                        isPrayed = true,
                        prayedInJamat = false
                    )
                )
            } else {
                amalDao.deleteSalahLog(date, prayer.replaceFirstChar { it.uppercase() })
            }
        }
    }

    suspend fun togglePrayerCompletion(prayer: String, date: String = getTodayDate()) {
        togglePrayer(prayer, date)
    }

    /**
     * Records detailed Salah with Jamat (congregation) status.
     */
    suspend fun recordSalahDetail(
        prayerName: String,
        isPrayed: Boolean,
        prayedInJamat: Boolean,
        date: String = getTodayDate()
    ) {
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val normalized = prayerName.lowercase().trim()
        val updated = when (normalized) {
            "fajr" -> existing.copy(fajrDone = isPrayed)
            "dhuhr" -> existing.copy(dhuhrDone = isPrayed)
            "asr" -> existing.copy(asrDone = isPrayed)
            "maghrib" -> existing.copy(maghribDone = isPrayed)
            "isha" -> existing.copy(ishaDone = isPrayed)
            "taraweeh" -> existing.copy(taraweehDone = isPrayed)
            "tahajjud" -> existing.copy(tahajjudDone = isPrayed)
            else -> existing
        }
        amalDao.insertOrUpdateDailyAmal(updated.copy(updatedAt = System.currentTimeMillis()))

        if (isPrayed) {
            amalDao.insertSalahLog(
                SalahLogEntity(
                    date = date,
                    prayerName = prayerName,
                    isPrayed = true,
                    prayedInJamat = prayedInJamat
                )
            )
        } else {
            amalDao.deleteSalahLog(date, prayerName)
        }
    }

    fun getSalahLogsForDate(date: String = getTodayDate()): Flow<List<SalahLogEntity>> {
        return amalDao.getSalahLogsForDate(date)
    }

    // --- QURAN RECITATION OPERATIONS ---

    suspend fun addQuranPages(pages: Int = 1, date: String = getTodayDate()) {
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val newPages = (existing.quranPagesReadToday + pages).coerceAtLeast(0)
        val updated = existing.copy(quranPagesReadToday = newPages, updatedAt = System.currentTimeMillis())
        amalDao.insertOrUpdateDailyAmal(updated)

        // Log reading session
        if (pages > 0) {
            amalDao.insertQuranLog(
                QuranLogEntity(
                    date = date,
                    surahName = updated.quranSurah,
                    juzNumber = updated.quranJuz,
                    pagesRead = pages
                )
            )
        }
    }

    suspend fun updateQuranBookmark(
        juz: Int,
        surah: String,
        pagesToday: Int,
        dailyGoalPages: Int = 20,
        date: String = getTodayDate()
    ) {
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val updated = existing.copy(
            quranJuz = juz,
            quranSurah = surah,
            quranPagesReadToday = pagesToday,
            quranDailyGoalPages = dailyGoalPages,
            updatedAt = System.currentTimeMillis()
        )
        amalDao.insertOrUpdateDailyAmal(updated)
    }

    suspend fun logQuranSession(
        surah: String,
        juz: Int,
        pages: Int,
        durationMinutes: Int = 0,
        note: String = "",
        date: String = getTodayDate()
    ) {
        amalDao.insertQuranLog(
            QuranLogEntity(
                date = date,
                surahName = surah,
                juzNumber = juz,
                pagesRead = pages,
                durationMinutes = durationMinutes,
                note = note
            )
        )
        // Also update daily totals
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val newTotal = (existing.quranPagesReadToday + pages).coerceAtLeast(0)
        amalDao.insertOrUpdateDailyAmal(
            existing.copy(
                quranSurah = surah,
                quranJuz = juz,
                quranPagesReadToday = newTotal,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun getQuranLogsForDate(date: String = getTodayDate()): Flow<List<QuranLogEntity>> {
        return amalDao.getQuranLogsForDate(date)
    }

    fun getAllQuranLogs(): Flow<List<QuranLogEntity>> {
        return amalDao.getAllQuranLogs()
    }

    fun getTodayPagesReadTotal(date: String = getTodayDate()): Flow<Int> {
        return amalDao.getTodayPagesReadTotal(date)
    }

    fun getAllTimePagesReadTotal(): Flow<Int> {
        return amalDao.getAllTimePagesReadTotal()
    }

    suspend fun deleteQuranLog(id: Long) {
        amalDao.deleteQuranLog(id)
    }

    // --- DHIKR & TASBEEH OPERATIONS ---

    suspend fun recordDhikrSession(
        dhikrName: String,
        arabicText: String,
        count: Int,
        target: Int = 33,
        date: String = getTodayDate()
    ) {
        if (count <= 0) return
        amalDao.insertDhikrLog(
            DhikrLogEntity(
                date = date,
                dhikrName = dhikrName,
                arabicText = arabicText,
                count = count,
                target = target
            )
        )

        // Increment today's aggregate
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val updated = existing.copy(
            dhikrTotalCount = existing.dhikrTotalCount + count,
            updatedAt = System.currentTimeMillis()
        )
        amalDao.insertOrUpdateDailyAmal(updated)
    }

    suspend fun incrementDailyDhikr(amount: Int = 1, date: String = getTodayDate()) {
        val existing = amalDao.getDailyAmalSync(date) ?: DailyAmalEntity(date = date)
        val updated = existing.copy(
            dhikrTotalCount = existing.dhikrTotalCount + amount,
            updatedAt = System.currentTimeMillis()
        )
        amalDao.insertOrUpdateDailyAmal(updated)
    }

    fun getDhikrLogsForDate(date: String = getTodayDate()): Flow<List<DhikrLogEntity>> {
        return amalDao.getDhikrLogsForDate(date)
    }

    fun getAllDhikrLogs(): Flow<List<DhikrLogEntity>> {
        return amalDao.getAllDhikrLogs()
    }

    fun getTodayDhikrTotal(date: String = getTodayDate()): Flow<Int> {
        return amalDao.getTodayDhikrTotal(date)
    }

    fun getAllTimeDhikrTotal(): Flow<Int> {
        return amalDao.getAllTimeDhikrTotal()
    }

    suspend fun deleteDhikrLog(id: Long) {
        amalDao.deleteDhikrLog(id)
    }
}
