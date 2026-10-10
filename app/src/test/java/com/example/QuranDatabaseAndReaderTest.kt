package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.dao.QuranVerseDao
import com.example.data.repository.QuranDataSeed
import com.example.data.repository.QuranRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class QuranDatabaseAndReaderTest {

    private lateinit var db: AppDatabase
    private lateinit var quranDao: QuranVerseDao
    private lateinit var repository: QuranRepository

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        quranDao = db.quranVerseDao()
        repository = QuranRepository(quranDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testQuranDatabaseInitializationAndSeeding() = runBlocking {
        assertEquals(0, quranDao.getVerseCount())

        repository.ensureDatabaseInitialized()
        val count = quranDao.getVerseCount()
        assertTrue("Seed verses count should be > 60, got: $count", count > 60)

        // Verify Surah Al-Fatihah (Surah 1) has 7 verses
        val fatihah = repository.getVersesBySurah(1).first()
        assertEquals(7, fatihah.size)
        assertEquals("الفاتحة", fatihah[0].surahNameArabic)
        assertEquals("সূরা আল-ফাতিহা", fatihah[0].surahNameBengali)
        assertEquals(1, fatihah[0].ayahNumber)
    }

    @Test
    fun testSearchVersesInLocalDatabase() = runBlocking {
        repository.ensureDatabaseInitialized()

        // Search for "রাহমান" in Bengali meaning or transliteration
        val results = repository.searchVerses("রাহমান").first()
        assertTrue("Search should return verses containing 'রাহমান'", results.isNotEmpty())

        // Search for specific Surah name
        val ikhlasResults = repository.searchVerses("ইখলাস").first()
        assertEquals(4, ikhlasResults.size)
    }

    @Test
    fun testBookmarkingFunctionality() = runBlocking {
        repository.ensureDatabaseInitialized()

        // Initially no bookmarks
        val initialBookmarks = repository.getBookmarkedVerses().first()
        assertEquals(0, initialBookmarks.size)

        // Get verse 1 of Surah 1
        val verses = repository.getVersesBySurah(1).first()
        val firstAyah = verses[0]
        assertFalse(firstAyah.isBookmarked)

        // Bookmark it
        repository.toggleBookmark(firstAyah)

        val bookmarkedList = repository.getBookmarkedVerses().first()
        assertEquals(1, bookmarkedList.size)
        assertEquals(firstAyah.id, bookmarkedList[0].id)
        assertTrue(bookmarkedList[0].isBookmarked)

        // Un-bookmark it
        repository.toggleBookmark(bookmarkedList[0])
        val unbookmarkedList = repository.getBookmarkedVerses().first()
        assertEquals(0, unbookmarkedList.size)
    }

    @Test
    fun testLastReadTracking() = runBlocking {
        repository.ensureDatabaseInitialized()

        val initialLastRead = repository.getLastReadVerse().first()
        assertNull(initialLastRead)

        val verses = repository.getVersesBySurah(112).first()
        val ayahToMark = verses[0]

        repository.setLastRead(ayahToMark)

        val updatedLastRead = repository.getLastReadVerse().first()
        assertNotNull(updatedLastRead)
        assertEquals(ayahToMark.id, updatedLastRead?.id)
        assertEquals(112, updatedLastRead?.surahNumber)
    }
}
