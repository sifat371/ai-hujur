package com.example.data.service

import com.example.data.model.HijriCalendarDay
import com.example.data.model.HijriDateInfo
import com.example.data.model.HijriMonthData
import com.example.data.model.IslamicEvent
import java.util.Calendar
import java.util.Locale

/**
 * Robust, offline-capable Islamic (Hijri) Calendar Service.
 * Provides accurate bidirectional conversion between Gregorian and Hijri dates,
 * monthly calendar generation, moon-sighting adjustment, and significant Islamic events calculation.
 */
object HijriCalendarService {

    private val HIJRI_MONTH_NAMES_BN = listOf(
        "মুহররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি",
        "জমাদিউল আউয়াল", "জমাদিউস সানি", "রজব", "শাবান",
        "রমজান", "শাওয়াল", "জিলকদ", "জিলহজ"
    )

    private val HIJRI_MONTH_NAMES_AR = listOf(
        "مَحَرَّم", "صَفَر", "رَبِيع ٱلْأَوَّل", "رَبِيع ٱلْآخِر",
        "جُمَادَىٰ ٱلْأُولَىٰ", "جُمَادَىٰ ٱلْآخِرَة", "رَجَب", "شَعْبَان",
        "رَمَضَان", "شَوَّال", "ذُو ٱلْقَعْدَة", "ذُو ٱلْحِجَّة"
    )

    private val HIJRI_MONTH_NAMES_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhul-Qi'dah", "Dhul-Hijjah"
    )

    private val GREGORIAN_MONTH_NAMES_BN = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
    )

    private val DAY_OF_WEEK_NAMES_BN = listOf(
        "রবিবার", "সোমবার", "মঙ্গলবার", "বুধবার", "বৃহস্পতিবার", "শুক্রবার", "শনিবার"
    )

    private val DAY_OF_WEEK_NAMES_EN = listOf(
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    )

    /**
     * Converts integer digits to Bengali numerals.
     */
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

    /**
     * Converts a Gregorian date (Year, Month 1-12, Day 1-31) to Julian Day number.
     */
    fun gregorianToJulianDay(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = (y / 100).toDouble()
        val b = 2 - a.toInt() + (a / 4).toInt()
        return (365.25 * (y + 4716)).toInt() + (30.6001 * (m + 1)).toInt() + day + b - 1524.5
    }

    /**
     * Converts Julian Day number back to Gregorian date (Triple of Year, Month 1-12, Day 1-31).
     */
    fun julianDayToGregorian(jd: Double): Triple<Int, Int, Int> {
        val z = (jd + 0.5).toInt()
        val alpha = ((z - 1867216.25) / 36524.25).toInt()
        val a = if (z < 2299161) z else z + 1 + alpha - (alpha / 4)
        val b = a + 1524
        val c = ((b - 122.1) / 365.25).toInt()
        val d = (365.25 * c).toInt()
        val e = ((b - d) / 30.6001).toInt()
        val day = b - d - (30.6001 * e).toInt()
        val month = if (e < 14) e - 1 else e - 13
        val year = if (month > 2) c - 4716 else c - 4715
        return Triple(year, month, day)
    }

    /**
     * Converts Julian Day to Hijri date with optional moon-sighting offset.
     * Returns Triple(hijriYear, hijriMonth 1-12, hijriDay 1-30).
     */
    fun julianDayToHijri(jd: Double, offsetDays: Int = 0): Triple<Int, Int, Int> {
        val adjustedJd = (jd + 0.5).toInt() + offsetDays
        val l = adjustedJd - 1948440 + 10632
        val n = (l - 1) / 10631
        val l2 = l - 10631 * n + 354
        val j = ((10985 - l2) / 5316) * ((50 * l2) / 17719) + ((l2 / 5670)) * ((43 * l2) / 15238)
        val l3 = l2 - ((30 - j) / 15) * ((17719 * j) / 50) - (j / 16) * ((15238 * j) / 43) + 29
        val m = (24 * l3) / 709
        val d = l3 - (709 * m) / 24
        val y = 30 * n + j - 30

        val validMonth = m.coerceIn(1, 12)
        val validDay = d.coerceIn(1, 30)
        return Triple(y, validMonth, validDay)
    }

    /**
     * Converts Hijri date back to Julian Day number.
     */
    fun hijriToJulianDay(year: Int, month: Int, day: Int, offsetDays: Int = 0): Double {
        val jd = ((11 * year + 3) / 30).toDouble() + 354 * year + 30 * month - ((month - 1) / 2) + day + 1948440 - 385
        return jd - offsetDays
    }

    /**
     * Gets full HijriDateInfo for a specific Gregorian Date.
     */
    fun getHijriDateInfo(
        year: Int,
        month: Int,
        day: Int,
        offsetDays: Int = 0
    ): HijriDateInfo {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, day)
        val dayOfWeekIndex = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday, 6 = Saturday

        val jd = gregorianToJulianDay(year, month, day)
        val (hYear, hMonth, hDay) = julianDayToHijri(jd, offsetDays)

        val monthNameBn = HIJRI_MONTH_NAMES_BN.getOrElse(hMonth - 1) { "রমজান" }
        val monthNameAr = HIJRI_MONTH_NAMES_AR.getOrElse(hMonth - 1) { "رَمَضَان" }
        val monthNameEn = HIJRI_MONTH_NAMES_EN.getOrElse(hMonth - 1) { "Ramadan" }

        val dayOfWeekBn = DAY_OF_WEEK_NAMES_BN.getOrElse(dayOfWeekIndex) { "শুক্রবার" }
        val dayOfWeekEn = DAY_OF_WEEK_NAMES_EN.getOrElse(dayOfWeekIndex) { "Friday" }
        val gMonthNameBn = GREGORIAN_MONTH_NAMES_BN.getOrElse(month - 1) { "মার্চ" }

        val formattedHijri = "${toBengaliDigits(hDay)} $monthNameBn ${toBengaliDigits(hYear)} হিজরি"
        val formattedGregorian = "${toBengaliDigits(day)} $gMonthNameBn ${toBengaliDigits(year)}, $dayOfWeekBn"

        val isAyyamAlBeed = hDay in 13..15
        val isFriday = (dayOfWeekIndex == 5) // Friday in Java Calendar is 6, index is 5

        return HijriDateInfo(
            day = hDay,
            month = hMonth,
            year = hYear,
            monthNameBn = monthNameBn,
            monthNameAr = monthNameAr,
            monthNameEn = monthNameEn,
            dayOfWeekBn = dayOfWeekBn,
            dayOfWeekEn = dayOfWeekEn,
            gregorianDay = day,
            gregorianMonth = month,
            gregorianMonthNameBn = gMonthNameBn,
            gregorianYear = year,
            formattedHijriBn = formattedHijri,
            formattedGregorianBn = formattedGregorian,
            isAyyamAlBeed = isAyyamAlBeed,
            isFriday = isFriday
        )
    }

    /**
     * Gets the current Islamic (Hijri) Date for today.
     */
    fun getTodayHijriDate(offsetDays: Int = 0): HijriDateInfo {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return getHijriDateInfo(y, m, d, offsetDays)
    }

    /**
     * Determines how many days are in a given Hijri month (29 or 30).
     */
    fun getDaysInHijriMonth(year: Int, month: Int, offsetDays: Int = 0): Int {
        // Look up the Gregorian start of this month and the next month
        val startJd = hijriToJulianDay(year, month, 1, offsetDays)
        val nextMonth = if (month == 12) 1 else month + 1
        val nextYear = if (month == 12) year + 1 else year
        val nextStartJd = hijriToJulianDay(nextYear, nextMonth, 1, offsetDays)
        val diff = (nextStartJd - startJd).toInt()
        return if (diff in 29..30) diff else if (month % 2 != 0) 30 else 29
    }

    /**
     * Builds the full monthly calendar data for the specified Hijri month and year.
     */
    fun getHijriMonthData(
        hijriYear: Int,
        hijriMonth: Int,
        offsetDays: Int = 0
    ): HijriMonthData {
        val today = getTodayHijriDate(offsetDays)
        val daysInMonth = getDaysInHijriMonth(hijriYear, hijriMonth, offsetDays)

        val monthNameBn = HIJRI_MONTH_NAMES_BN.getOrElse(hijriMonth - 1) { "মুহররম" }
        val monthNameAr = HIJRI_MONTH_NAMES_AR.getOrElse(hijriMonth - 1) { "مَحَرَّم" }
        val monthNameEn = HIJRI_MONTH_NAMES_EN.getOrElse(hijriMonth - 1) { "Muharram" }

        val eventsMap = getSignificantEvents().filter { it.hijriMonth == hijriMonth }
            .associateBy { it.hijriDay }

        val daysList = mutableListOf<HijriCalendarDay>()

        var firstGregorianDay = 1
        var firstGregorianMonth = 1
        var lastGregorianDay = 1
        var lastGregorianMonth = 1
        var gregorianYear = 2026

        for (day in 1..daysInMonth) {
            val jd = hijriToJulianDay(hijriYear, hijriMonth, day, offsetDays)
            val (gYear, gMonth, gDay) = julianDayToGregorian(jd)

            if (day == 1) {
                firstGregorianDay = gDay
                firstGregorianMonth = gMonth
                gregorianYear = gYear
            }
            if (day == daysInMonth) {
                lastGregorianDay = gDay
                lastGregorianMonth = gMonth
            }

            val cal = Calendar.getInstance()
            cal.set(gYear, gMonth - 1, gDay)
            val dow = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday
            val dowNameBn = DAY_OF_WEEK_NAMES_BN.getOrElse(dow - 1) { "" }

            val isToday = (hijriYear == today.year && hijriMonth == today.month && day == today.day)
            val isAyyamAlBeed = day in 13..15
            val isFriday = (dow == Calendar.FRIDAY)
            val event = eventsMap[day]

            daysList.add(
                HijriCalendarDay(
                    hijriDay = day,
                    hijriMonth = hijriMonth,
                    hijriYear = hijriYear,
                    gregorianDay = gDay,
                    gregorianMonth = gMonth,
                    gregorianYear = gYear,
                    dayOfWeek = dow,
                    dayOfWeekNameBn = dowNameBn,
                    isCurrentMonth = true,
                    isToday = isToday,
                    isAyyamAlBeed = isAyyamAlBeed,
                    isFriday = isFriday,
                    event = event
                )
            )
        }

        val firstMonthBn = GREGORIAN_MONTH_NAMES_BN.getOrElse(firstGregorianMonth - 1) { "" }
        val lastMonthBn = GREGORIAN_MONTH_NAMES_BN.getOrElse(lastGregorianMonth - 1) { "" }
        val gregorianSpan = if (firstGregorianMonth == lastGregorianMonth) {
            "${toBengaliDigits(firstGregorianDay)} – ${toBengaliDigits(lastGregorianDay)} $firstMonthBn ${toBengaliDigits(gregorianYear)}"
        } else {
            "${toBengaliDigits(firstGregorianDay)} $firstMonthBn – ${toBengaliDigits(lastGregorianDay)} $lastMonthBn ${toBengaliDigits(gregorianYear)}"
        }

        return HijriMonthData(
            month = hijriMonth,
            year = hijriYear,
            monthNameBn = monthNameBn,
            monthNameAr = monthNameAr,
            monthNameEn = monthNameEn,
            daysInMonth = daysInMonth,
            gregorianSpanBn = gregorianSpan,
            days = daysList
        )
    }

    /**
     * Master repository of significant Islamic holy days and events throughout the year.
     */
    fun getSignificantEvents(): List<IslamicEvent> {
        return listOf(
            IslamicEvent(
                id = "event_islamic_new_year",
                titleBn = "পহেলা মুহররম (ইসলামিক নববর্ষ)",
                titleAr = "رأس السنة الهجرية",
                hijriDay = 1,
                hijriMonth = 1,
                hijriMonthNameBn = "মুহররম",
                category = "পবিত্র মাস",
                significance = "হিজরি সনের প্রথম দিন। রাসূলুল্লাহ ﷺ-এর মক্কা থেকে মদিনায় হিজরতের ঐতিহাসিক স্মৃতি বিজড়িত মহিমান্বিত সূচনালগ্ন।",
                recommendedDeeds = listOf(
                    "নতুন বছরের বরকত ও কল্যাণের জন্য তাওবা ও নেক দোয়ার সংকল্প",
                    "মুহররম মাসে অধিক পরিমাণে নফল রোজা রাখা",
                    "গুনাহ থেকে বিরত থেকে আত্মশুদ্ধি অর্জন"
                ),
                quranHadithReference = "রাসূলুল্লাহ ﷺ বলেছেন: 'রমজানের পর সর্বাধিক উত্তম রোজা হলো আল্লাহর প্রিয় মাস মুহররমের রোজা।' (সহীহ মুসলিম ১১৬৩)"
            ),
            IslamicEvent(
                id = "event_ashura",
                titleBn = "পবিত্র আশুরা ও তাসূ'আ",
                titleAr = "يوم عاشوراء",
                hijriDay = 10,
                hijriMonth = 1,
                hijriMonthNameBn = "মুহররম",
                category = "ঐতিহাসিক দিবস",
                significance = "মুহররমের ১০ তারিখ। আল্লাহ তাআলা হযরত মূসা (আ.) ও বনী ইসরাঈলকে ফেরাউনের দাসত্ব ও লোহিত সাগরের নিমজ্জন থেকে মুক্তি দিয়েছিলেন। এ দিনে কারবালার প্রান্তরে ইমাম হুসাইন (রা.)-এর শাহাদাতের বিয়োগান্তক ঘটনাও ঘটে।",
                recommendedDeeds = listOf(
                    "আশুরার দিনে রোজা রাখা (বিগত ১ বছরের সগিরা গুনাহ মাফ হয়)",
                    "সুন্নাহ রক্ষায় ৯ ও ১০ অথবা ১০ ও ১১ মুহররম মোট ২টি রোজা রাখা",
                    "পরিবারের সদস্যদের প্রতি পানাহারে প্রশস্ততা দান"
                ),
                quranHadithReference = "রাসূলুল্লাহ ﷺ বলেছেন: 'আমি আল্লাহর কাছে আশা করি, আশুরার রোজা বিগত এক বছরের গুনাহর কাফফারা হবে।' (সহীহ মুসলিম ১১৬২)"
            ),
            IslamicEvent(
                id = "event_mawlid",
                titleBn = "ঈদে মিলাদুন্নবী ﷺ (সীরাতুন্নবী)",
                titleAr = "المولد النبوي الشريف",
                hijriDay = 12,
                hijriMonth = 3,
                hijriMonthNameBn = "রবিউল আউয়াল",
                category = "ঐতিহাসিক দিবস",
                significance = "বিশ্বজাহানের রহমত, শেষ নবী ও সর্বশ্রেষ্ঠ মানব হযরত মুহাম্মদ মুস্তফা ﷺ-এর পবিত্র বিলাদত (আগমন) দিবস।",
                recommendedDeeds = listOf(
                    "রাসূলুল্লাহ ﷺ-এর ওপর অধিক পরিমাণে দরুদ ও সালাম পেশ করা",
                    "সীরাতুন্নবী ও সুন্নাহর গভীর চর্চা ও জীবনে বাস্তবায়ন",
                    "অসহায়দের খাদ্যদান ও সাদকাহ"
                ),
                quranHadithReference = "আল্লাহ তায়ালা ইরশাদ করেন: 'আমি আপনাকে সমগ্র বিশ্বজগতের জন্য কেবল রহমত স্বরূপ প্রেরণ করেছি।' (সূরা আল-আম্বিয়া ১০৭)"
            ),
            IslamicEvent(
                id = "event_shab_e_miraj",
                titleBn = "লাইলাতুল মেরাজ (শবে মেরাজ)",
                titleAr = "الإسراء والمعراج",
                hijriDay = 27,
                hijriMonth = 7,
                hijriMonthNameBn = "রজব",
                category = "ফজিলতপূর্ণ রজনী",
                significance = "রজব মাসের ঐতিহাসিক রজনী, যাতে মহান আল্লাহ স্বীয় হাবীব ﷺ-কে মসজিদুল হারাম থেকে মসজিদুল আকসা এবং সেখান থেকে ঊর্ধ্বাকাশে সিদরাতুল মুনতাহা পর্যন্ত অলৌকিক মেরাজ করিয়েছিলেন এবং উম্মতের জন্য ৫ ওয়াক্ত নামাজ হাদিয়া দেন।",
                recommendedDeeds = listOf(
                    "রাতের বেলা তাহাজ্জুদ ও নফল নামাজ আদায়",
                    "নামাজের প্রতি যত্নবান হওয়ার নতুন সংকল্প",
                    "অধিক পরিমাণে ইস্তিগফার ও তাসবীহ তাহলীল"
                ),
                quranHadithReference = "সূরা বনী ইসরাঈল ১: 'পরম পবিত্র ও মহিমাময় সত্তা তিনি, যিনি স্বীয় বান্দাকে রাতের বেলা মসজিদুল হারাম থেকে মসজিদুল আকসা পর্যন্ত ভ্রমণ করিয়েছিলেন...'"
            ),
            IslamicEvent(
                id = "event_shab_e_barat",
                titleBn = "লাইলাতুল বারাআত (শবে বরাত)",
                titleAr = "ليلة النصف من شعبان",
                hijriDay = 15,
                hijriMonth = 8,
                hijriMonthNameBn = "শাবান",
                category = "ফজিলতপূর্ণ রজনী",
                significance = "পবিত্র শাবান মাসের মধ্যরজনী। আল্লাহর রহমত ও ক্ষমার বিশেষ দ্বার উন্মুক্ত হয়। মুশরিক ও হিংসুক ব্যতীত সমস্ত খাঁটি তওবাকারীকে ক্ষমা ঘোষণা করা হয়।",
                recommendedDeeds = listOf(
                    "রাতের নির্জনে ইস্তিগফার ও রোনাজারি",
                    "পরদিন (১৫ শাবান) নফল রোজা রাখা",
                    "পরস্পরের প্রতি হিংসা-বিদ্বেষ পরিহার করে ক্ষমা চাওয়া"
                ),
                quranHadithReference = "রাসূলুল্লাহ ﷺ বলেছেন: 'মধ্য শাবানের রাতে আল্লাহ তায়ালা সৃষ্টিজগতের প্রতি রহমতের দৃষ্টি দেন এবং মুশরিক ও হিংসুক ছাড়া সবাইকে ক্ষমা করে দেন।' (সহীহ ইবনে হিব্বান ৫৬৬৫)"
            ),
            IslamicEvent(
                id = "event_ramadan_start",
                titleBn = "পবিত্র রমজানুল মুবারক শুরু",
                titleAr = "أول شهر رمضان المبارك",
                hijriDay = 1,
                hijriMonth = 9,
                hijriMonthNameBn = "রমজান",
                category = "রমজান ও ঈদ",
                significance = "রহমত, মাগফিরাত ও জাহান্নাম থেকে মুক্তির মাস রমজানের শুভ সূচনা। জান্নাতের দরজাসমূহ উন্মুক্ত করা হয় এবং শয়তানকে শৃঙ্খলিত করা হয়।",
                recommendedDeeds = listOf(
                    "ফরজ সিয়ামের আন্তরিক নিয়ত",
                    "সেহরি ও ইফতার সুন্নাহ অনুযায়ী আদায়",
                    "প্রতি রাতে জামাতে ২০ রাকাত বা ৮ রাকাত তারাবীহর সালাত",
                    "দৈনিক কুরআন তিলাওয়াত বৃদ্ধি"
                ),
                quranHadithReference = "সূরা আল-বাক্বারাহ ১৮৫: 'রমজান মাস, যাতে কুরআন নাজিল করা হয়েছে মানুষের জন্য পথপ্রদর্শক হিসেবে...'"
            ),
            IslamicEvent(
                id = "event_badr_day",
                titleBn = "ঐতিহাসিক বদর বিজয় দিবস",
                titleAr = "غزوة بدر الكبرى",
                hijriDay = 17,
                hijriMonth = 9,
                hijriMonthNameBn = "রমজান",
                category = "ঐতিহাসিক দিবস",
                significance = "১৭ রমজান দ্বিতীয় হিজরিতে সংঘটিত সত্য ও মিথ্যার চূড়ান্ত পার্থক্যকারী ঐতিহাসিক বদর প্রান্তরের যুদ্ধ, যাতে ৩১৩ জন সাহাবীকে আল্লাহ তাআলা ফেরেশতা পাঠিয়ে অলৌকিক বিজয় দান করেছিলেন।",
                recommendedDeeds = listOf(
                    "বদর যুদ্ধের ইতিহাস ও সাহাবীদের ত্যাগ স্মরণ",
                    "আল্লাহর নুসরত ও ঈমানি দৃঢ়তার জন্য বিশেষ মোনাজাত",
                    "রমজানের আমলকে আরও বেগবান করা"
                ),
                quranHadithReference = "সূরা আলে ইমরান ১২৩: 'নিশ্চয়ই আল্লাহ বদরে তোমাদের সাহায্য করেছিলেন যখন তোমরা ছিলে সম্পূর্ণ দুর্বল।'"
            ),
            IslamicEvent(
                id = "event_laylatul_qadr",
                titleBn = "লাইলাতুল কদর (মহিমান্বিত কদরের রাত)",
                titleAr = "ليلة القدر المباركة",
                hijriDay = 27,
                hijriMonth = 9,
                hijriMonthNameBn = "রমজান",
                category = "ফজিলতপূর্ণ রজনী",
                significance = "হাজার মাসের চেয়েও শ্রেষ্ঠতম রাত। রমজানের শেষ দশকের বিজোড় রাতসমূহে (বিশেষ করে ২৭তম রাতে) লুকায়িত এই বরকতময় রজনীতে আল-কুরআন নাজিল হয়।",
                recommendedDeeds = listOf(
                    "সারারাত জেগে তাহাজ্জুদ ও কুরআন তিলাওয়াত",
                    "দোয়া পাঠ: 'আল্লাহুম্মা ইন্নাকা আফুউন তুহিব্বুল আফওয়া ফা'ফু আন্নী'",
                    "শেষ দশকে এতেকাফ পালন",
                    "অধিক পরিমাণে দান-সদকা করা"
                ),
                quranHadithReference = "সূরা আল-কদর ৩: 'লাইলাতুল কদর এক হাজার মাসের চেয়েও শ্রেষ্ঠ।'"
            ),
            IslamicEvent(
                id = "event_eid_ul_fitr",
                titleBn = "পবিত্র ঈদুল ফিতর",
                titleAr = "عيد الفطر المبارك",
                hijriDay = 1,
                hijriMonth = 10,
                hijriMonthNameBn = "শাওয়াল",
                category = "রমজান ও ঈদ",
                significance = "মাসব্যাপী সিয়াম সাধনার পর মহান রবের পক্ষ থেকে মুসলিম উম্মাহর জন্য মহিমান্বিত উপহার ও আনন্দ উৎসবের দিন। এদিন রোজা রাখা হারাম।",
                recommendedDeeds = listOf(
                    "নামাজের পূর্বে গরিবদের মাঝে সদকাতুল ফিতর আদায়",
                    "ঈদগাহে গিয়ে ঈদের দুই রাকাত ওয়াজিব সালাত ও খুতবা শ্রবণ",
                    "সুন্দর ও পরিচ্ছন্ন পোশাক পরিধান, মিষ্টিমুখ ও শুভেচ্ছা বিনিময়"
                ),
                quranHadithReference = "সহীহ বুখারী ৯৫৮: রাসূলুল্লাহ ﷺ ঈদের দিনে জামাতে যাওয়ার আগে খেজুর দিয়ে মিষ্টিমুখ করতেন।"
            ),
            IslamicEvent(
                id = "event_six_fasts_shawwal",
                titleBn = "শাওয়াল মাসের ছয় রোজা",
                titleAr = "ست من شوال",
                hijriDay = 2,
                hijriMonth = 10,
                hijriMonthNameBn = "শাওয়াল",
                category = "পবিত্র মাস",
                significance = "ঈদুল ফিতরের পর পুরো শাওয়াল মাস জুড়ে যেকোনো ৬টি নফল রোজা রাখা অত্যন্ত সওয়াবের আমল।",
                recommendedDeeds = listOf(
                    "শাওয়াল মাসের ভেতর যেকোনো সুবিধাজনক ৬ দিন রোজা রাখা",
                    "টানা অথবা ভেঙে ভেঙে রোজা রাখা যায়",
                    "রমজানের রোজা কাজা থাকলে আগে তা আদায় করা উত্তম"
                ),
                quranHadithReference = "রাসূলুল্লাহ ﷺ বলেছেন: 'যে ব্যক্তি রমজানের রোজা রাখল, অতঃপর শাওয়ালের ছয়টি রোজা রাখল, সে যেন সারা বছরই রোজা রাখল।' (সহীহ মুসলিম ১১৬৪)"
            ),
            IslamicEvent(
                id = "event_first_ten_dhul_hijjah",
                titleBn = "জিলহজের বরকতময় প্রথম দশক",
                titleAr = "عشر ذي الحجة",
                hijriDay = 1,
                hijriMonth = 12,
                hijriMonthNameBn = "জিলহজ",
                category = "পবিত্র মাস",
                significance = "বছরের শ্রেষ্ঠতম ১০টি দিন। আল্লাহ তাআলা আল-কুরআনে এই দশ রাতের শপথ করেছেন। এ দিনগুলোতে নেক আমল আল্লাহর কাছে অন্য যেকোনো দিনের চেয়ে অধিক প্রিয়।",
                recommendedDeeds = listOf(
                    "১ থেকে ৯ জিলহজ নফল রোজা রাখা",
                    "অধিক পরিমাণে তাহলীল (লা ইলাহা ইল্লাল্লাহ), তাকবীর (আল্লাহু আকবার) ও তাহমীদ (আলহামদুলিল্লাহ) পাঠ",
                    "যাদের ওপর কোরবানি ওয়াজিব, নখ ও চুল কাটা থেকে বিরত থাকা"
                ),
                quranHadithReference = "সহীহ বুখারী ৯৬৯: 'এমন কোনো দিন নেই যাতে নেক আমল করা আল্লাহর কাছে এই দশ দিনের চেয়ে অধিক প্রিয়।'"
            ),
            IslamicEvent(
                id = "event_day_of_arafah",
                titleBn = "ইয়াওমু আরাফাহ (আরাফাত দিবস)",
                titleAr = "يوم عرفة",
                hijriDay = 9,
                hijriMonth = 12,
                hijriMonthNameBn = "জিলহজ",
                category = "ঐতিহাসিক দিবস",
                significance = "হজের সবচেয়ে গুরুত্বপূর্ণ রুকন হলো ৯ জিলহজ আরাফাতের ময়দানে অবস্থান করা। যারা হজে যাননি, তাদের জন্য এই দিনের রোজা অত্যন্ত ফজিলতপূর্ণ।",
                recommendedDeeds = listOf(
                    "আরাফাতের দিনে রোজা রাখা (বিগত ১ বছর ও আগামী ১ বছরের গুনাহ মাফ হয়)",
                    "দিনের পুরোটা সময় জিকির ও নিজের ও উম্মতের জন্য মোনাজাত",
                    "আরাফাতের শ্রেষ্ঠ দোয়া: 'লা ইলাহা ইল্লাল্লাহু ওয়াহদাহু লা শারীকা লাহু...'"
                ),
                quranHadithReference = "রাসূলুল্লাহ ﷺ বলেছেন: 'আরাফার দিনের রোজার ব্যাপারে আমি আল্লাহর কাছে আশা করি, তিনি পূর্ববর্তী এক বছর এবং পরবর্তী এক বছরের গুনাহ ক্ষমা করে দেবেন।' (সহীহ মুসলিম ১১৬২)"
            ),
            IslamicEvent(
                id = "event_eid_ul_adha",
                titleBn = "পবিত্র ঈদুল আজহা ও কোরবানি",
                titleAr = "عيد الأضحى المبارك",
                hijriDay = 10,
                hijriMonth = 12,
                hijriMonthNameBn = "জিলহজ",
                category = "রমজান ও ঈদ",
                significance = "হযরত ইব্রাহিম (আ.) ও ইসমাইল (আ.)-এর মহান আত্মত্যাগের স্মরণে পশু কোরবানি ও ত্যাগের পবিত্র উৎসব।",
                recommendedDeeds = listOf(
                    "ঈদুল আজহার নামাজ ও খুতবা আদায়",
                    "সামর্থ্যবানদের জন্য পশু কোরবানি করা এবং গরিব ও আত্মীয়দের মাঝে গোশত বণ্টন",
                    "৯ জিলহজ ফজর থেকে ১৩ জিলহজ আসর পর্যন্ত প্রত্যেক ফরজ নামাজের পর তাকবীরে তাশরীক পাঠ ওয়াজিব"
                ),
                quranHadithReference = "সূরা আল-কাওসার ২: 'অতএব আপনি আপনার রবের উদ্দেশ্যে নামাজ পড়ুন এবং কোরবানি করুন।'"
            ),
            IslamicEvent(
                id = "event_ayyam_at_tashreeq",
                titleBn = "আইয়ামে তাশরীক (কোরবানি ও তাকবীরের দিন)",
                titleAr = "أيام التشريق",
                hijriDay = 11,
                hijriMonth = 12,
                hijriMonthNameBn = "জিলহজ",
                category = "পবিত্র মাস",
                significance = "জিলহজের ১১, ১২ ও ১৩ তারিখ। হজের কার্যাবলি ও কোরবানির সময়কাল। এ দিনগুলোতে রোজা রাখা সম্পূর্ণ হারাম।",
                recommendedDeeds = listOf(
                    "প্রত্যেক ফরজ নামাজের পর পুরুষদের উচ্চস্বরে ও নারীদের নিম্নস্বরে তাকবীরে তাশরীক পাঠ",
                    "তাকবীর: 'আল্লাহু আকবার, আল্লাহু আকবার, লা ইলাহা ইল্লাল্লাহু ওয়াল্লাহু আকবার, আল্লাহু আকবার ওয়া লিল্লাহিল হামদ'",
                    "আনন্দ উপভোগ ও আল্লাহর নেয়ামতের শুকরিয়া"
                ),
                quranHadithReference = "সহীহ মুসলিম ১১৪১: 'আইয়ামে তাশরীক হলো পানাহার এবং আল্লাহর জিকিরের দিন।'"
            )
        )
    }

    /**
     * Calculates countdown / days remaining for each significant Islamic event
     * relative to the provided Hijri date.
     */
    fun getUpcomingEvents(
        currentHijriYear: Int,
        currentHijriMonth: Int,
        currentHijriDay: Int,
        offsetDays: Int = 0
    ): List<IslamicEvent> {
        val currentJd = hijriToJulianDay(currentHijriYear, currentHijriMonth, currentHijriDay, offsetDays)
        val allEvents = getSignificantEvents()

        return allEvents.map { event ->
            // Determine whether the event is in the current Hijri year or next Hijri year
            val eventThisYearJd = hijriToJulianDay(currentHijriYear, event.hijriMonth, event.hijriDay, offsetDays)
            val eventTargetJd = if (eventThisYearJd >= currentJd) {
                eventThisYearJd
            } else {
                hijriToJulianDay(currentHijriYear + 1, event.hijriMonth, event.hijriDay, offsetDays)
            }

            val diffDays = (eventTargetJd - currentJd).toLong()
            val (gYear, gMonth, gDay) = julianDayToGregorian(eventTargetJd)
            val gMonthBn = GREGORIAN_MONTH_NAMES_BN.getOrElse(gMonth - 1) { "" }
            val cal = Calendar.getInstance()
            cal.set(gYear, gMonth - 1, gDay)
            val dowNameBn = DAY_OF_WEEK_NAMES_BN.getOrElse(cal.get(Calendar.DAY_OF_WEEK) - 1) { "" }

            val estimatedGregorian = "${toBengaliDigits(gDay)} $gMonthBn ${toBengaliDigits(gYear)}, $dowNameBn"
            val isToday = (diffDays == 0L)

            event.copy(
                estimatedGregorianDateBn = estimatedGregorian,
                daysRemaining = diffDays,
                isToday = isToday
            )
        }.sortedBy { it.daysRemaining }
    }

    /**
     * Gets the immediate next upcoming significant Islamic event.
     */
    fun getNextSignificantEvent(
        currentHijriYear: Int,
        currentHijriMonth: Int,
        currentHijriDay: Int,
        offsetDays: Int = 0
    ): IslamicEvent? {
        val list = getUpcomingEvents(currentHijriYear, currentHijriMonth, currentHijriDay, offsetDays)
        return list.firstOrNull()
    }
}
