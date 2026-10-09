package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.dao.AmalDao
import com.example.data.local.entity.DailyAmalEntity
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.data.local.entity.SalahLogEntity
import com.example.data.repository.AmalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AmalDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var amalDao: AmalDao
    private lateinit var repository: AmalRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        amalDao = db.amalDao()
        repository = AmalRepository(amalDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveDailyAmal() = runBlocking {
        val today = repository.getTodayDate()
        val initial = repository.getTodayAmal().first()
        assertEquals(today, initial.date)
        assertEquals(0, initial.prayerCompletionCount())

        // Toggle Fajr
        repository.togglePrayer("fajr", today)
        val afterFajr = repository.getTodayAmal().first()
        assertTrue(afterFajr.fajrDone)
        assertEquals(1, afterFajr.prayerCompletionCount())

        // Toggle Dhuhr
        repository.togglePrayer("dhuhr", today)
        val afterDhuhr = repository.getTodayAmal().first()
        assertTrue(afterDhuhr.dhuhrDone)
        assertEquals(2, afterDhuhr.prayerCompletionCount())
    }

    @Test
    fun testQuranRecitationTracking() = runBlocking {
        val today = repository.getTodayDate()

        // Log Quran reading session
        repository.logQuranSession(
            surah = "সূরা আল-বাকারা",
            juz = 1,
            pages = 4,
            durationMinutes = 20,
            note = "সকালের তিলাওয়াত",
            date = today
        )

        val dailyRecord = repository.getTodayAmal().first()
        assertEquals("সূরা আল-বাকারা", dailyRecord.quranSurah)
        assertEquals(1, dailyRecord.quranJuz)
        assertEquals(4, dailyRecord.quranPagesReadToday)

        val totalPages = repository.getTodayPagesReadTotal(today).first()
        assertEquals(4, totalPages)

        val quranLogs = repository.getQuranLogsForDate(today).first()
        assertEquals(1, quranLogs.size)
        assertEquals("সূরা আল-বাকারা", quranLogs[0].surahName)
    }

    @Test
    fun testDhikrTracking() = runBlocking {
        val today = repository.getTodayDate()

        // Record SubhanAllah 33 times
        repository.recordDhikrSession(
            dhikrName = "সুবহানাল্লাহ",
            arabicText = "سُبْحَانَ اللَّهِ",
            count = 33,
            target = 33,
            date = today
        )

        // Record Alhamdulillah 33 times
        repository.recordDhikrSession(
            dhikrName = "আলহামদুলিল্লাহ",
            arabicText = "الْحَمْدُ لِلَّهِ",
            count = 33,
            target = 33,
            date = today
        )

        val totalDhikr = repository.getTodayDhikrTotal(today).first()
        assertEquals(66, totalDhikr)

        val logs = repository.getDhikrLogsForDate(today).first()
        assertEquals(2, logs.size)

        val dailyRecord = repository.getTodayAmal().first()
        assertEquals(66, dailyRecord.dhikrTotalCount)
    }

    @Test
    fun testSalahDetailedLog() = runBlocking {
        val today = repository.getTodayDate()

        repository.recordSalahDetail(
            prayerName = "Fajr",
            isPrayed = true,
            prayedInJamat = true,
            date = today
        )

        val salahLogs = repository.getSalahLogsForDate(today).first()
        assertEquals(1, salahLogs.size)
        assertTrue(salahLogs[0].prayedInJamat)
        assertTrue(salahLogs[0].isPrayed)
    }

    @Test
    fun testRepeatedSalahDetailDoesNotCreateDuplicateLogs() = runBlocking {
        val date = "2026-10-09"
        repository.recordSalahDetail("Fajr", true, false, date)
        repository.recordSalahDetail("fajr", true, true, date)
        val logs = repository.getSalahLogsForDate(date).first()
        assertEquals(1, logs.size)
        assertTrue(logs.single().prayedInJamat)
        repository.recordSalahDetail("FAJR", false, false, date)
        assertTrue(repository.getSalahLogsForDate(date).first().isEmpty())
    }

    @Test
    fun testDeletingLogsReconcilesDailyTotals() = runBlocking {
        val date = "2026-10-09"
        repository.logQuranSession("সূরা আল-ফাতিহা", 1, 5, date = date)
        repository.recordDhikrSession("সুবহানাল্লাহ", "", 33, date = date)
        val quranLog = repository.getQuranLogsForDate(date).first().single()
        val dhikrLog = repository.getDhikrLogsForDate(date).first().single()

        repository.deleteQuranLog(quranLog.id)
        repository.deleteDhikrLog(dhikrLog.id)

        val daily = repository.getAmalForDate(date).first()!!
        assertEquals(0, daily.quranPagesReadToday)
        assertEquals(0, daily.dhikrTotalCount)
        assertEquals(0, repository.getTodayPagesReadTotal(date).first())
        assertEquals(0, repository.getTodayDhikrTotal(date).first())
    }

    @Test
    fun testDateFlowSwitchesRoomSubscriptionAtMidnight() = runBlocking {
        val firstDay = "2026-10-09"
        val nextDay = "2026-10-10"
        repository.saveDailyAmal(DailyAmalEntity(date = firstDay, fajrDone = true))
        val day = MutableStateFlow(firstDay)
        val observed = mutableListOf<DailyAmalEntity>()
        withTimeout(3000) {
            repository.observeDailyAmal(day).take(2).collect { record ->
                observed.add(record)
                if (record.date == firstDay) day.value = nextDay
            }
        }
        assertEquals(listOf(firstDay, nextDay), observed.map { it.date })
        assertTrue(observed[0].fajrDone)
        assertEquals(0, observed[1].prayerCompletionCount())
        assertTrue(repository.getAmalForDate(firstDay).first()!!.fajrDone)
    }
}
