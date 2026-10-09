package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Detailed log entity for individual Salah (prayers),
 * recording prayer name, performance status, congregation (Jamat) attendance,
 * and exact timestamp.
 */
@Entity(tableName = "salah_logs")
data class SalahLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val prayerName: String, // "Fajr", "Dhuhr", "Asr", "Maghrib", "Isha", "Taraweeh", "Tahajjud", "Duha"
    val isPrayed: Boolean = true,
    val prayedInJamat: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
