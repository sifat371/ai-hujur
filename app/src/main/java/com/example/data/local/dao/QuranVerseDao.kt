package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.QuranVerseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranVerseDao {

    @Query("SELECT * FROM quran_verses WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    fun getVersesBySurah(surahNumber: Int): Flow<List<QuranVerseEntity>>

    @Query("SELECT * FROM quran_verses WHERE surahNumber = :surahNumber ORDER BY ayahNumber ASC")
    suspend fun getVersesBySurahSync(surahNumber: Int): List<QuranVerseEntity>

    @Query("SELECT COUNT(*) FROM quran_verses")
    suspend fun getVerseCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<QuranVerseEntity>)

    @Query("""
        SELECT * FROM quran_verses 
        WHERE surahNameBengali LIKE '%' || :query || '%' 
           OR surahNameEnglish LIKE '%' || :query || '%' 
           OR translationBengali LIKE '%' || :query || '%' 
           OR pronunciationBengali LIKE '%' || :query || '%'
           OR arabicText LIKE '%' || :query || '%'
        ORDER BY surahNumber ASC, ayahNumber ASC
    """)
    fun searchVerses(query: String): Flow<List<QuranVerseEntity>>

    @Query("SELECT * FROM quran_verses WHERE isBookmarked = 1 ORDER BY surahNumber ASC, ayahNumber ASC")
    fun getBookmarkedVerses(): Flow<List<QuranVerseEntity>>

    @Query("UPDATE quran_verses SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun updateBookmark(id: Long, isBookmarked: Boolean)

    @Query("UPDATE quran_verses SET lastReadTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLastRead(id: Long, timestamp: Long)

    @Query("SELECT * FROM quran_verses WHERE lastReadTimestamp > 0 ORDER BY lastReadTimestamp DESC LIMIT 1")
    fun getLastReadVerse(): Flow<QuranVerseEntity?>
}
