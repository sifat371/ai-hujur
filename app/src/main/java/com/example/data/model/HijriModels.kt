package com.example.data.model

/**
 * Information regarding a specific Hijri (Islamic) Date.
 */
data class HijriDateInfo(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthNameBn: String,
    val monthNameAr: String,
    val monthNameEn: String,
    val dayOfWeekBn: String,
    val dayOfWeekEn: String,
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianMonthNameBn: String,
    val gregorianYear: Int,
    val formattedHijriBn: String,
    val formattedGregorianBn: String,
    val isAyyamAlBeed: Boolean = false,
    val isFriday: Boolean = false
)

/**
 * Representation of a single cell/day in a monthly Hijri Calendar grid.
 */
data class HijriCalendarDay(
    val hijriDay: Int,
    val hijriMonth: Int,
    val hijriYear: Int,
    val gregorianDay: Int,
    val gregorianMonth: Int,
    val gregorianYear: Int,
    val dayOfWeek: Int, // 1 = Sunday, 7 = Saturday
    val dayOfWeekNameBn: String,
    val isCurrentMonth: Boolean = true,
    val isToday: Boolean = false,
    val isAyyamAlBeed: Boolean = false,
    val isFriday: Boolean = false,
    val event: IslamicEvent? = null
)

/**
 * Month metadata containing all days and Gregorian span.
 */
data class HijriMonthData(
    val month: Int,
    val year: Int,
    val monthNameBn: String,
    val monthNameAr: String,
    val monthNameEn: String,
    val daysInMonth: Int,
    val gregorianSpanBn: String,
    val days: List<HijriCalendarDay>
)

/**
 * Significant Islamic Event / Holy Day.
 */
data class IslamicEvent(
    val id: String,
    val titleBn: String,
    val titleAr: String,
    val hijriDay: Int,
    val hijriMonth: Int,
    val hijriMonthNameBn: String,
    val category: String, // "রমজান ও ঈদ", "পবিত্র মাস", "ঐতিহাসিক দিবস", "ফজিলতপূর্ণ রজনী"
    val significance: String,
    val recommendedDeeds: List<String>,
    val quranHadithReference: String,
    val estimatedGregorianDateBn: String = "",
    val daysRemaining: Long = 0,
    val isToday: Boolean = false
)
