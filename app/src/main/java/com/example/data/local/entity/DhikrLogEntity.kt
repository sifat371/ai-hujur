package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Detailed log entity for Dhikr (remembrance of Allah) and Tasbeeh,
 * recording the phrase, Arabic script, count completed, target,
 * and completion timestamp.
 */
@Entity(tableName = "dhikr_logs")
data class DhikrLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: "yyyy-MM-dd"
    val dhikrName: String, // e.g. "SubhanAllah", "Alhamdulillah", "Allahu Akbar", "Astaghfirullah"
    val arabicText: String = "",
    val count: Int,
    val target: Int = 33,
    val timestamp: Long = System.currentTimeMillis()
)
