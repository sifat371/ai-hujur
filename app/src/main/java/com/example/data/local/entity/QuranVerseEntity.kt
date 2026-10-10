package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Lightweight Room entity representing an individual Quranic verse (Ayah)
 * for offline reading, search, and bookmarking.
 */
@Entity(tableName = "quran_verses")
data class QuranVerseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val surahNameBengali: String,
    val totalAyahs: Int,
    val revelationType: String, // "Makki" or "Madani"
    val arabicText: String,
    val pronunciationBengali: String,
    val translationBengali: String,
    val translationEnglish: String = "",
    val isBookmarked: Boolean = false,
    val lastReadTimestamp: Long = 0L
)
