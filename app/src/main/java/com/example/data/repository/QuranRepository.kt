package com.example.data.repository

import com.example.data.local.dao.QuranVerseDao
import com.example.data.local.entity.QuranVerseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class QuranRepository(
    private val quranVerseDao: QuranVerseDao
) {

    fun getVersesBySurah(surahNumber: Int): Flow<List<QuranVerseEntity>> =
        quranVerseDao.getVersesBySurah(surahNumber)

    fun searchVerses(query: String): Flow<List<QuranVerseEntity>> =
        quranVerseDao.searchVerses(query)

    fun getBookmarkedVerses(): Flow<List<QuranVerseEntity>> =
        quranVerseDao.getBookmarkedVerses()

    fun getLastReadVerse(): Flow<QuranVerseEntity?> =
        quranVerseDao.getLastReadVerse()

    suspend fun toggleBookmark(verse: QuranVerseEntity) = withContext(Dispatchers.IO) {
        quranVerseDao.updateBookmark(verse.id, !verse.isBookmarked)
    }

    suspend fun setLastRead(verse: QuranVerseEntity) = withContext(Dispatchers.IO) {
        quranVerseDao.updateLastRead(verse.id, System.currentTimeMillis())
    }

    suspend fun ensureDatabaseInitialized() = withContext(Dispatchers.IO) {
        val count = quranVerseDao.getVerseCount()
        if (count < 60) {
            val initialVerses = QuranDataSeed.generateInitialQuranVerses()
            quranVerseDao.insertVerses(initialVerses)
        }
    }
}
