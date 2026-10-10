package com.example.data.model

/**
 * Data model for Tazkiyah (খারাপ অভ্যাস ও গুনাহ বর্জন) tracking.
 */
data class BadHabit(
    val id: String,
    val titleBn: String,
    val category: String, // "দৃষ্টি", "জিহ্বা", "আমল", "নেশা", "সময়", "অন্যান্য"
    val iconCategory: String,
    val streakDays: Int = 0,
    val lastCleanDate: String = "", // YYYY-MM-DD
    val totalCleanDays: Int = 0,
    val reasonToQuit: String = "",
    val spiritualRemedy: String = "",
    val quranAyahOrHadith: String = "",
    val reference: String = "",
    val isTracking: Boolean = true
)

/**
 * Emergency SOS Step to combat Satanic Waswasah (কুপ্রবৃত্তি ও ওয়াসওয়াসা প্রতিরোধের পদক্ষেপ).
 */
data class WaswasahEmergencyStep(
    val stepNumber: Int,
    val titleBn: String,
    val actionBn: String,
    val hadithProofBn: String,
    val iconType: String
)
