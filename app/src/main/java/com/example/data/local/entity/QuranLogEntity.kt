package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Detailed log entity for Quran recitation sessions,
 * recording the Surah, Juz (Para), number of pages read,
 * reading duration, and timestamp.
 */
@Entity(tableName = "quran_logs")
data class QuranLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val surahName: String,
    val juzNumber: Int,
    val pagesRead: Int,
    val durationMinutes: Int = 0,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
