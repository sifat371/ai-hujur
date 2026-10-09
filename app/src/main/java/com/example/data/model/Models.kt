package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PrayerTimeInfo(
    val name: String,
    val timeString: String,
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val arabicName: String,
    val timeMinutes: Int = 0
)

data class DailyNasihot(
    val title: String,
    val advice: String,
    val reference: String,
    val category: String,
    val date: String
)

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
    val isVoiceMessage: Boolean = false
)

data class ForumPost(
    val id: String,
    val authorName: String,
    val timeAgo: String,
    val category: String,
    val questionText: String,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val replies: List<ForumReply> = emptyList()
)

data class ForumReply(
    val id: String,
    val authorName: String,
    val replyText: String,
    val timeAgo: String,
    val isAiModerator: Boolean = false,
    val verifiedReference: String? = null
)

data class AmalDailyProgress(
    val fajrDone: Boolean = false,
    val dhuhrDone: Boolean = false,
    val asrDone: Boolean = false,
    val maghribDone: Boolean = false,
    val ishaDone: Boolean = false,
    val taraweehDone: Boolean = false,
    val tahajjudDone: Boolean = false,
    val fastingToday: Boolean = true,
    val charityGiven: Boolean = false,
    val morningAdhkarDone: Boolean = false,
    val eveningAdhkarDone: Boolean = false,
    val quranJuz: Int = 15,
    val quranSurah: String = "Surah Al-Kahf",
    val quranPagesReadToday: Int = 12,
    val quranDailyGoalPages: Int = 20,
    val dhikrTotalCount: Int = 0
) {
    fun prayerCompletionCount(): Int {
        var count = 0
        if (fajrDone) count++
        if (dhuhrDone) count++
        if (asrDone) count++
        if (maghribDone) count++
        if (ishaDone) count++
        return count
    }

    fun overallProgressPercent(): Float {
        val totalItems = 7f
        var done = prayerCompletionCount().toFloat()
        if (taraweehDone) done += 1f
        if (fastingToday) done += 1f
        return (done / totalItems).coerceIn(0f, 1f)
    }
}

data class RamadanCalendarDay(
    val dayNumber: Int,
    val dateString: String,
    val dayOfWeek: String,
    val sehriTime: String,
    val iftarTime: String,
    val isToday: Boolean = false
)

data class LibraryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val arabicText: String,
    val englishTranslation: String,
    val reference: String,
    val category: String // "Surah", "Hadith", "Name of Allah", "Dua"
)

data class QuickDua(
    val title: String,
    val arabic: String,
    val transliteration: String,
    val translation: String,
    val occasion: String
)
