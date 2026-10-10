package com.example.data.service

import java.util.Calendar

data class BanglaDateInfo(
    val day: Int,
    val monthIndex: Int, // 1 to 12
    val monthName: String,
    val year: Int,
    val dayOfWeek: String,
    val season: String,
    val formattedBengali: String
)

/**
 * Accurate Bengali Calendar (বঙ্গাব্দ) calculation service according to
 * the Bangla Academy standard calendar for Bangladesh.
 */
object BanglaCalendarService {

    val BANGLA_MONTHS = listOf(
        "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
        "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র"
    )

    val BANGLA_SEASONS = listOf(
        "গ্রীষ্মকাল", "গ্রীষ্মকাল",
        "বর্ষাকাল", "বর্ষাকাল",
        "শরৎকাল", "শরৎকাল",
        "হেমন্তকাল", "হেমন্তকাল",
        "শীতকাল", "শীতকাল",
        "বসন্তকাল", "বসন্তকাল"
    )

    val DAYS_OF_WEEK_BN = listOf(
        "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
    )

    fun toBengaliDigits(number: Int): String {
        val bengaliDigits = arrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
        val str = number.toString()
        val builder = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                builder.append(bengaliDigits[ch - '0'])
            } else {
                builder.append(ch)
            }
        }
        return builder.toString()
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * Converts a Gregorian calendar date to a Bengali calendar date (Bongabdo).
     */
    fun getBanglaDate(calendar: Calendar = Calendar.getInstance()): BanglaDateInfo {
        val gYear = calendar.get(Calendar.YEAR)
        val gMonth = calendar.get(Calendar.MONTH) + 1 // 1-12
        val gDay = calendar.get(Calendar.DAY_OF_MONTH)
        val dayOfWeekIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
        val dayOfWeekName = DAYS_OF_WEEK_BN[dayOfWeekIndex.coerceIn(0, 6)]

        val leapYear = isLeapYear(gYear)

        // Month lengths in Bengali calendar (Bangla Academy revised standard):
        // Boishakh to Ashwin (months 1 to 6) = 31 days each
        // Kartik to Magh (months 7 to 10) = 30 days each
        // Falgun (month 11) = 29 days (30 in leap year)
        // Chaitra (month 12) = 30 days
        val falgunDays = if (leapYear) 30 else 29
        val monthLengths = listOf(31, 31, 31, 31, 31, 31, 30, 30, 30, 30, falgunDays, 30)

        // Bengali Year calculation:
        // Pohela Boishakh is April 14
        val bYear = if (gMonth > 4 || (gMonth == 4 && gDay >= 14)) {
            gYear - 593
        } else {
            gYear - 594
        }

        var bMonthIndex = 1
        var bDay = 1

        when (gMonth) {
            1 -> { // January
                if (gDay <= 14) {
                    bMonthIndex = 9 // Poush
                    bDay = gDay + 16
                } else {
                    bMonthIndex = 10 // Magh
                    bDay = gDay - 14
                }
            }
            2 -> { // February
                if (gDay <= 13) {
                    bMonthIndex = 10 // Magh
                    bDay = gDay + 17
                } else {
                    bMonthIndex = 11 // Falgun
                    bDay = gDay - 13
                }
            }
            3 -> { // March
                val falgunBoundary = if (leapYear) 15 else 14
                val falgunOffset = if (leapYear) 15 else 14
                if (gDay <= falgunBoundary) {
                    bMonthIndex = 11 // Falgun
                    bDay = gDay + (falgunDays - falgunOffset)
                } else {
                    bMonthIndex = 12 // Chaitra
                    bDay = gDay - falgunBoundary
                }
            }
            4 -> { // April
                if (gDay <= 13) {
                    bMonthIndex = 12 // Chaitra
                    bDay = gDay + 17
                } else {
                    bMonthIndex = 1 // Boishakh
                    bDay = gDay - 13
                }
            }
            5 -> { // May
                if (gDay <= 14) {
                    bMonthIndex = 1 // Boishakh
                    bDay = gDay + 17
                } else {
                    bMonthIndex = 2 // Jyoishtho
                    bDay = gDay - 14
                }
            }
            6 -> { // June
                if (gDay <= 14) {
                    bMonthIndex = 2 // Jyoishtho
                    bDay = gDay + 17
                } else {
                    bMonthIndex = 3 // Asharh
                    bDay = gDay - 14
                }
            }
            7 -> { // July
                if (gDay <= 15) {
                    bMonthIndex = 3 // Asharh
                    bDay = gDay + 16
                } else {
                    bMonthIndex = 4 // Srabon
                    bDay = gDay - 15
                }
            }
            8 -> { // August
                if (gDay <= 15) {
                    bMonthIndex = 4 // Srabon
                    bDay = gDay + 16
                } else {
                    bMonthIndex = 5 // Bhadro
                    bDay = gDay - 15
                }
            }
            9 -> { // September
                if (gDay <= 15) {
                    bMonthIndex = 5 // Bhadro
                    bDay = gDay + 16
                } else {
                    bMonthIndex = 6 // Ashwin
                    bDay = gDay - 15
                }
            }
            10 -> { // October
                if (gDay <= 16) {
                    bMonthIndex = 6 // Ashwin
                    bDay = gDay + 15
                } else {
                    bMonthIndex = 7 // Kartik
                    bDay = gDay - 16
                }
            }
            11 -> { // November
                if (gDay <= 15) {
                    bMonthIndex = 7 // Kartik
                    bDay = gDay + 15
                } else {
                    bMonthIndex = 8 // Agrahayan
                    bDay = gDay - 15
                }
            }
            12 -> { // December
                if (gDay <= 15) {
                    bMonthIndex = 8 // Agrahayan
                    bDay = gDay + 15
                } else {
                    bMonthIndex = 9 // Poush
                    bDay = gDay - 15
                }
            }
        }

        val monthName = BANGLA_MONTHS[bMonthIndex - 1]
        val season = BANGLA_SEASONS[bMonthIndex - 1]
        val formatted = "${toBengaliDigits(bDay)} $monthName, ${toBengaliDigits(bYear)} বঙ্গাব্দ"

        return BanglaDateInfo(
            day = bDay,
            monthIndex = bMonthIndex,
            monthName = monthName,
            year = bYear,
            dayOfWeek = dayOfWeekName,
            season = season,
            formattedBengali = formatted
        )
    }
}
