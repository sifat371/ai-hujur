package com.example.data.repository

import com.example.data.model.*
import java.util.Calendar
import java.util.Locale

object IslamicRepository {

    val dailyNasihotList = listOf(
        DailyNasihot(
            title = "নিয়তের বিশুদ্ধতা (ইখলাস)",
            advice = "সমস্ত আমল নিয়তের ওপর নির্ভরশীল। আজকের প্রতিটি রোজা, নামাজ এবং দান-সদকার আগে নিয়তকে শুধু মহান আল্লাহর সন্তুষ্টির জন্য খালেস করুন। লোক দেখানো আমলের কোনো মূল্য নেই, কিন্তু খাঁটি নিয়তে করা ছোট একটি নেক আমলও পাহাড়সম ভারী হতে পারে।",
            reference = "সহীহ আল-বুখারী ১",
            category = "আধ্যাত্মিকতা",
            date = "আজকের নসিহত"
        ),
        DailyNasihot(
            title = "রোজায় জিহ্বা ও আচরণের সংযম",
            advice = "রোজা শুধু পানাহার বর্জন করার নাম নয়; বরং মিথ্যা কথা, গীবত ও অনর্থক রাগ থেকে বিরত থাকাও রোজার অংশ। কেউ খারাপ ব্যবহার করলে বা গালি দিলে শান্তভাবে বলুন: 'ইন্নি সায়িম' (আমি রোজা রেখেছি)।",
            reference = "সহীহ আল-বুখারী ১৯০৪",
            category = "রমজান",
            date = "দৈনিক নসিহত"
        ),
        DailyNasihot(
            title = "অধিক পরিমাণে ইস্তিগফারের ফজিলত",
            advice = "সারাদিনের প্রতিটি অবসরে ইস্তিগফারকে নিজের সঙ্গী বানান। দিনে অন্তত ১০০ বার বলুন: 'আস্তাগফিরুল্লাহ ওয়া আতূবু ইলাইহি'। এটি মনের অশান্তি দূর করে, রিজিকের দ্বার খুলে দেয় এবং জীবনে আল্লাহর রহমত বর্ষণ করে।",
            reference = "সূরা নূহ ৭১:১০-১২",
            category = "জিকির ও দোয়া",
            date = "হিকমত"
        ),
        DailyNasihot(
            title = "পিতা-মাতার খেদমত ও নেক দোয়া",
            advice = "আল্লাহ তায়ালা নিজের শোকর আদায়ের সাথে পিতা-মাতার প্রতি কৃতজ্ঞতার নির্দেশ দিয়েছেন। তাদের সাথে কোমল ভাষায় কথা বলুন, তাঁদের জন্য দোয়া করুন। মা-বাবার সন্তুষ্টিতেই আল্লাহর সন্তুষ্টি নিহিত।",
            reference = "সূরা লোকমান ৩১:১৪",
            category = "সদাচরণ",
            date = "ফজিলত"
        )
    )

    fun getTodayPrayerTimes(
        latitude: Double = 23.8103,
        longitude: Double = 90.4125,
        locationName: String = "ঢাকা (বাংলাদেশ)"
    ): List<PrayerTimeInfo> {
        val calendar = Calendar.getInstance()
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val calculated = com.example.data.service.PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            calendar = calendar,
            locationName = locationName
        )

        val schedule = listOf(
            Triple("ফজর (সেহরি শেষ)", "الفجر", calculated.fajrMinutes),
            Triple("সূর্যোদয়", "الشروق", calculated.sunriseMinutes),
            Triple("যোহর", "الظهر", calculated.dhuhrMinutes),
            Triple("আসর", "العصر", calculated.asrMinutes),
            Triple("মাগরিব (ইফতার)", "المغرب", calculated.maghribMinutes),
            Triple("এশা ও তারাবীহ", "العشاء", calculated.ishaMinutes)
        )

        var foundNext = false
        val result = mutableListOf<PrayerTimeInfo>()

        for ((name, arabic, minutes) in schedule) {
            val isPassed = currentMinutes >= minutes
            val isNext = !isPassed && !foundNext
            if (isNext) foundNext = true

            result.add(
                PrayerTimeInfo(
                    name = name,
                    timeString = calculated.formatTime(minutes),
                    isNext = isNext,
                    isPassed = isPassed,
                    arabicName = arabic,
                    timeMinutes = minutes
                )
            )
        }

        if (!foundNext && result.isNotEmpty()) {
            val fajr = result[0]
            result[0] = fajr.copy(isNext = true)
        }

        return result
    }

    fun getNextPrayerCountdown(
        latitude: Double = 23.8103,
        longitude: Double = 90.4125,
        locationName: String = "ঢাকা (বাংলাদেশ)"
    ): Triple<String, String, Long> {
        val calendar = Calendar.getInstance()
        val currentSeconds = calendar.get(Calendar.HOUR_OF_DAY) * 3600 +
                calendar.get(Calendar.MINUTE) * 60 +
                calendar.get(Calendar.SECOND)

        val calculated = com.example.data.service.PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            calendar = calendar,
            locationName = locationName
        )

        val fajrSec = calculated.fajrMinutes * 60
        val dhuhrSec = calculated.dhuhrMinutes * 60
        val asrSec = calculated.asrMinutes * 60
        val maghribSec = calculated.maghribMinutes * 60
        val ishaSec = calculated.ishaMinutes * 60

        return when {
            currentSeconds < fajrSec -> {
                val diff = (fajrSec - currentSeconds).toLong()
                Triple("ফজর (সেহরি শেষ)", "পরবর্তী ওয়াক্ত ও রোজা শুরু", diff)
            }
            currentSeconds < dhuhrSec -> {
                val diff = (dhuhrSec - currentSeconds).toLong()
                Triple("যোহরের ওয়াক্ত", "দুপুরের ফরজ নামাজ", diff)
            }
            currentSeconds < asrSec -> {
                val diff = (asrSec - currentSeconds).toLong()
                Triple("আসরের ওয়াক্ত", "বিকেলের ফরজ নামাজ", diff)
            }
            currentSeconds < maghribSec -> {
                val diff = (maghribSec - currentSeconds).toLong()
                Triple("মাগরিব (ইফতারের সময়)", "রোজা ভাঙ্গার বরকতময় মুহূর্ত", diff)
            }
            currentSeconds < ishaSec -> {
                val diff = (ishaSec - currentSeconds).toLong()
                Triple("এশা ও তারাবীহ", "রাতের নামাজ ও পবিত্র তারাবীহ", diff)
            }
            else -> {
                val secondsUntilMidnight = 86400 - currentSeconds
                val diff = (secondsUntilMidnight + fajrSec).toLong()
                Triple("ফজর (সেহরি শেষ)", "আগামীকালের রোজা শুরু", diff)
            }
        }
    }

    val initialForumPosts = listOf(
        ForumPost(
            id = "p1",
            authorName = "ভাই তারিকুল ইসলাম (ঢাকা)",
            timeAgo = "২৫ মিনিট আগে",
            category = "রমজান ও রোজা",
            questionText = "ভুলবশত বা অন্যমনস্ক হয়ে রোজার মধ্যে কিছু খেয়ে ফেললে বা পানি পান করলে কি রোজা ভেঙে যাবে?",
            likes = 36,
            isLiked = false,
            replies = listOf(
                ForumReply(
                    id = "r1_ai",
                    authorName = "এআই মডারেটর (মুফতি)",
                    replyText = "শরয়ী সমাধান: ভুলবশত পানাহার করলে রোজা নষ্ট হয় না, রোজা সম্পূর্ণ সহীহ থাকবে। রাসূলুল্লাহ ﷺ বলেছেন: 'যে ব্যক্তি রোজা থাকা অবস্থায় ভুলে গিয়ে পানাহার করল, সে যেন তার রোজা পূর্ণ করে। কারণ আল্লাহই তাকে খাইয়েছেন এবং পান করিয়েছেন।' (সহীহ বুখারী ১৯৩৩, মুসলিম ১১৫৫)। এর জন্য কোনো কাজা বা কাফফারা ওয়াজিব হয় না।",
                    timeAgo = "২০ মিনিট আগে",
                    isAiModerator = true,
                    verifiedReference = "সহীহ আল-বুখারী ১৯৩৩ / ফিকহুস সুন্নাহ"
                ),
                ForumReply(
                    id = "r1_u1",
                    authorName = "আমিনা খাতুন",
                    replyText = "সুবহানাল্লাহ! আল্লাহর রহমত কত অপরিসীম। কাল ইফতারের প্রস্তুতি নেওয়ার সময় আমার মায়ের এমন হয়েছিল, এই মাসয়ালায় অনেক প্রশান্তি পেলাম।",
                    timeAgo = "১২ মিনিট আগে",
                    isAiModerator = false
                )
            )
        ),
        ForumPost(
            id = "p2",
            authorName = "মারিয়াম বেগম (চট্টগ্রাম)",
            timeAgo = "১ ঘণ্টা আগে",
            category = "ফিকহ ও নামাজ",
            questionText = "ছোট বাচ্চা বা কাজের কারণে মসজিদে যেতে না পারলে ঘরে একাকী বা পরিবারের সাথে তারাবীহর নামাজ পড়া যাবে কি?",
            likes = 29,
            isLiked = true,
            replies = listOf(
                ForumReply(
                    id = "r2_ai",
                    authorName = "এআই মডারেটর (মুফতি)",
                    replyText = "শরয়ী সমাধান: হ্যাঁ, ঘরে একাকী বা পরিবারের সদস্যদের সাথে তারাবীহর নামাজ সম্পূর্ণ আদায় করা জায়েজ। মা-বোনদের জন্য ঘরে তারাবীহ ও নামাজ আদায় করা অধিক সওয়াবের। পুরুষরাও ওজরের কারণে ঘরে একাকী বা জামাত করে ২০ রাকাত বা ৮ রাকাত আদায় করতে পারবেন।",
                    timeAgo = "৫০ মিনিট আগে",
                    isAiModerator = true,
                    verifiedReference = "সহীহ বুখারী ২০১২ / ফাতাওয়া শামী"
                )
            )
        ),
        ForumPost(
            id = "p3",
            authorName = "জায়েদ হাসান (সিলেট)",
            timeAgo = "৩ ঘণ্টা আগে",
            category = "দৈনিক জিকির ও দোয়া",
            questionText = "শবে কদর ও রমজানের শেষ দশকে বেশি বেশি পড়ার জন্য সবচেয়ে উত্তম দোয়া কোনটি?",
            likes = 54,
            isLiked = false,
            replies = listOf(
                ForumReply(
                    id = "r3_ai",
                    authorName = "এআই মডারেটর (মুফতি)",
                    replyText = "হযরত আয়েশা (রা.) রাসূলুল্লাহ ﷺ-কে জিজ্ঞাসা করেছিলেন: 'হে আল্লাহর রাসূল! আমি যদি লাইলাতুল কদর পাই, তবে কী দোয়া করব?' তিনি ﷺ বললেন: 'তুমি বলবে— اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي (আল্লাহুম্মা ইন্নাকা আফুউন, তুহিব্বুল আফওয়া, ফা'ফু আন্নী)। অর্থ: হে আল্লাহ! আপনি নিশ্চয়ই ক্ষমাশীল, ক্ষমা করা পছন্দ করেন; অতএব আমাকে ক্ষমা করে দিন।'",
                    timeAgo = "২ ঘণ্টা আগে",
                    isAiModerator = true,
                    verifiedReference = "জামে তিরমিযী ৩৫১৩ (সহীহ)"
                )
            )
        )
    )

    val quickDuas = listOf(
        QuickDua(
            title = "ইফতারের দোয়া",
            arabic = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
            transliteration = "জাহাবাজ জামাউ, ওয়াবতাল্লাতিল উরূক্বু, ওয়া ছাবাতাল আজরু ইনশাআল্লাহ।",
            translation = "পিপাসা নিবারিত হলো, শিরা-উপশিরা সিক্ত হলো এবং আল্লাহর ইচ্ছায় সওয়াব লিপিবদ্ধ হলো।",
            occasion = "ইফতারের সময়"
        ),
        QuickDua(
            title = "রোজার নিয়ত (সেহরির সময়)",
            arabic = "وَبِصَوْমِ غَدٍ نَّوَيْتُ مِنْ شَهْرِ رَمَضَانَ",
            transliteration = "নাওয়াইতু আন আছুমা গাদাম মিন শাহরি রামাদান।",
            translation = "আমি পবিত্র রমজান মাসের আগামীকালের রোজা রাখার নিয়ত করলাম। (মনে মনে সংকল্প করাই যথেষ্ট)",
            occasion = "সেহরির সময়"
        ),
        QuickDua(
            title = "সায়্যিদুল ইস্তিগফার (শ্রেষ্ঠ তওবা)",
            arabic = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ خَلَقْتَنِي وَأَنَا عَبْدُكَ وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ",
            transliteration = "আল্লাহুম্মা আনতা রব্বী, লা ইলাহা ইল্লা আনতা, খালাক্বতানী ওয়া আনা আবদুকা, ওয়া আনা আলা আহদিকা ওয়া ওয়া'দিকা মাস্তাত্বা'তু...",
            translation = "হে আল্লাহ! আপনি আমার রব, আপনি ছাড়া কোনো উপাস্য নেই। আপনি আমাকে সৃষ্টি করেছেন এবং আমি আপনার বান্দা...",
            occasion = "সকাল ও সন্ধ্যায়"
        ),
        QuickDua(
            title = "ইলম ও হিদায়াত বৃদ্ধির দোয়া",
            arabic = "رَّبِّ زِدْنِي عِلْمًا",
            transliteration = "রব্বি যিদনী ইলমা।",
            translation = "হে আমার প্রতিপালক! আমার জ্ঞান বৃদ্ধি করে দিন।",
            occasion = "সূরা ত্বা-হা ২০:১১৪"
        )
    )

    fun getRamadan30Days(): List<RamadanCalendarDay> {
        val days = mutableListOf<RamadanCalendarDay>()
        val dayNames = listOf("সোম", "মঙ্গল", "বুধ", "বৃহস্পতি", "শুক্র", "শনি", "রবি")

        for (i in 1..30) {
            val dayOfWeek = dayNames[(i + 2) % 7]
            val sehriMins = 4 * 60 + 55 - (i * 1) // gradual progression in Dhaka
            val iftarMins = 18 * 60 + 12 + (i * 1)

            val sH = sehriMins / 60
            val sM = sehriMins % 60
            val iH = (iftarMins / 60) - 12
            val iM = iftarMins % 60

            val sehriStr = String.format(Locale.US, "%02d:%02d পূর্বাহ্ন", sH, sM)
            val iftarStr = String.format(Locale.US, "%02d:%02d অপরাহ্ন", iH, iM)

            days.add(
                RamadanCalendarDay(
                    dayNumber = i,
                    dateString = "${toBengaliNumber(i)} রমজান",
                    dayOfWeek = dayOfWeek,
                    sehriTime = sehriStr,
                    iftarTime = iftarStr,
                    isToday = (i == 14)
                )
            )
        }
        return days
    }

    private fun toBengaliNumber(number: Int): String {
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

    val libraryItems = listOf(
        LibraryItem(
            id = "lib_surah_kahf",
            title = "সূরা আল-কাহাফ (The Cave)",
            subtitle = "১৮তম সূরা • ১১০ আয়াত • মাক্কী",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا",
            englishTranslation = "সমস্ত প্রশংসা আল্লাহর, যিনি তাঁর বান্দার ওপর এই কিতাব অবতীর্ণ করেছেন এবং এতে কোনো বক্রতা রাখেননি।",
            reference = "জুমার দিনে তিলাওয়াত করলে এক জুমা থেকে অপর জুমা পর্যন্ত নূর প্রজ্বলিত থাকে এবং দাজ্জালের ফিতনা থেকে রক্ষা পায়।",
            category = "সূরা"
        ),
        LibraryItem(
            id = "lib_surah_mulk",
            title = "সূরা আল-মুলক (তাবারকাল্লাযী)",
            subtitle = "৬৭তম সূরা • ৩০ আয়াত • মাক্কী",
            arabicText = "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ",
            englishTranslation = "বরকতময় তিনি, যাঁর হাতে সমস্ত রাজত্ব এবং তিনি সর্ববিষয়ে সর্বশক্তিমান।",
            reference = "নিয়মিত রাতে তিলাওয়াত করলে কবরের আজাব থেকে মুক্তিলাভের সুপারিশকারী হয় (তিরমিযী)।",
            category = "সূরা"
        ),
        LibraryItem(
            id = "lib_hadith_intentions",
            title = "আমলের নিয়তের গুরুত্ব সংক্রান্ত হাদিস",
            subtitle = "চল্লিশ হাদিসের ১ম হাদিস (ইমাম নববী)",
            arabicText = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            englishTranslation = "নিশ্চয়ই সমস্ত কাজ নিয়তের ওপর নির্ভরশীল এবং প্রত্যেক ব্যক্তি তা-ই পাবে যা সে নিয়ত করেছে।",
            reference = "সহীহ বুখারী ১ ও সহীহ মুসলিম ১৯০৭",
            category = "হাদিস"
        ),
        LibraryItem(
            id = "lib_name_ar_rahman",
            title = "আর-রহমান (পরম দয়ালু)",
            subtitle = "আল্লাহর গুণবাচক নাম #১",
            arabicText = "الرَّحْمَٰنُ عَلَى الْعَرْশِ اسْتَوَىٰ",
            englishTranslation = "যিনি অসীম দয়াবান, যাঁর দয়া সমগ্র সৃষ্টিজগত পরিবেষ্টন করে আছে।",
            reference = "সূরা ত্বা-হা ২০:৫",
            category = "আল্লাহর গুণবাচক নাম"
        ),
        LibraryItem(
            id = "lib_name_al_ghafoor",
            title = "আল-গাফুর (অত্যন্ত ক্ষমাশীল)",
            subtitle = "আল্লাহর গুণবাচক নাম #৩৪",
            arabicText = "وَهُوَ الْغَفُورُ الْوَدُودُ",
            englishTranslation = "যিনি পরম ক্ষমাশীল, বান্দার ভুলত্রুটি গোপনকারী ও পরম স্নেহময়।",
            reference = "সূরা আল-বুরুজ ৮৫:১৪",
            category = "আল্লাহর গুণবাচক নাম"
        )
    )
}
