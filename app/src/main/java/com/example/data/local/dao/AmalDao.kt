package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.DailyAmalEntity
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.data.local.entity.SalahLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for managing and querying all daily Islamic practices (Amal),
 * including Salah, Quran recitation, and Dhikr.
 */
@Dao
interface AmalDao {

    // --- DAILY AMAL AGGREGATE ---

    @Query("SELECT * FROM daily_amal WHERE date = :date LIMIT 1")
    fun getDailyAmal(date: String): Flow<DailyAmalEntity?>

    @Query("SELECT * FROM daily_amal WHERE date = :date LIMIT 1")
    suspend fun getDailyAmalSync(date: String): DailyAmalEntity?

    @Query("SELECT * FROM daily_amal ORDER BY date DESC")
    fun getAllDailyAmal(): Flow<List<DailyAmalEntity>>

    @Query("SELECT * FROM daily_amal ORDER BY date DESC LIMIT :limit")
    fun getRecentDailyAmal(limit: Int): Flow<List<DailyAmalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyAmal(record: DailyAmalEntity)

    // --- SALAH LOGS ---

    @Query("SELECT * FROM salah_logs WHERE date = :date ORDER BY timestamp ASC")
    fun getSalahLogsForDate(date: String): Flow<List<SalahLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalahLog(log: SalahLogEntity)

    @Transaction
    suspend fun replaceSalahLog(log: SalahLogEntity) {
        deleteSalahLog(log.date, log.prayerName)
        insertSalahLog(log)
    }

    @Query("DELETE FROM salah_logs WHERE date = :date AND prayerName = :prayerName COLLATE NOCASE")
    suspend fun deleteSalahLog(date: String, prayerName: String)

    @Query("SELECT COUNT(*) FROM salah_logs WHERE date = :date AND isPrayed = 1")
    fun getPrayedCountForDate(date: String): Flow<Int>

    // --- QURAN RECITATION LOGS ---

    @Query("SELECT * FROM quran_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getQuranLogsForDate(date: String): Flow<List<QuranLogEntity>>

    @Query("SELECT * FROM quran_logs ORDER BY timestamp DESC")
    fun getAllQuranLogs(): Flow<List<QuranLogEntity>>

    @Query("SELECT COALESCE(SUM(pagesRead), 0) FROM quran_logs WHERE date = :date")
    fun getTodayPagesReadTotal(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(pagesRead), 0) FROM quran_logs")
    fun getAllTimePagesReadTotal(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuranLog(log: QuranLogEntity)

    @Query("SELECT * FROM quran_logs WHERE id = :id LIMIT 1")
    suspend fun getQuranLog(id: Long): QuranLogEntity?

    @Query("DELETE FROM quran_logs WHERE id = :id")
    suspend fun deleteQuranLog(id: Long)

    @Transaction
    suspend fun deleteQuranLogAndUpdateTotal(id: Long) {
        val log = getQuranLog(id) ?: return
        deleteQuranLog(id)
        val daily = getDailyAmalSync(log.date) ?: return
        insertOrUpdateDailyAmal(daily.copy(
            quranPagesReadToday = (daily.quranPagesReadToday - log.pagesRead).coerceAtLeast(0),
            updatedAt = System.currentTimeMillis()
        ))
    }

    // --- DHIKR LOGS ---

    @Query("SELECT * FROM dhikr_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getDhikrLogsForDate(date: String): Flow<List<DhikrLogEntity>>

    @Query("SELECT * FROM dhikr_logs ORDER BY timestamp DESC")
    fun getAllDhikrLogs(): Flow<List<DhikrLogEntity>>

    @Query("SELECT COALESCE(SUM(count), 0) FROM dhikr_logs WHERE date = :date")
    fun getTodayDhikrTotal(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(count), 0) FROM dhikr_logs")
    fun getAllTimeDhikrTotal(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDhikrLog(log: DhikrLogEntity)

    @Query("SELECT * FROM dhikr_logs WHERE id = :id LIMIT 1")
    suspend fun getDhikrLog(id: Long): DhikrLogEntity?

    @Query("DELETE FROM dhikr_logs WHERE id = :id")
    suspend fun deleteDhikrLog(id: Long)

    @Transaction
    suspend fun deleteDhikrLogAndUpdateTotal(id: Long) {
        val log = getDhikrLog(id) ?: return
        deleteDhikrLog(id)
        val daily = getDailyAmalSync(log.date) ?: return
        insertOrUpdateDailyAmal(daily.copy(
            dhikrTotalCount = (daily.dhikrTotalCount - log.count).coerceAtLeast(0),
            updatedAt = System.currentTimeMillis()
        ))
    }
}
