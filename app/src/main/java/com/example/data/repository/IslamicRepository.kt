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
        ),
        DailyNasihot(
            title = "নামাজের খুশু ও আন্তরিকতা",
            advice = "নামাজে দাঁড়ানোর সময় এমন অনুভূতি হৃদয়ে জাগ্রত করুন যেন আপনি সরাসরি মহান রাব্বুল আলামিনের সামনে দণ্ডায়মান। প্রতিটি সিজদায় নিজের পাপের ক্ষমা ও দুনিয়া-আখেরাতের কল্যাণ প্রার্থনা করুন।",
            reference = "সহীহ মুসলিম ৩৯৭ / সূরা আল-মুমিনূন ২",
            category = "সালাত",
            date = "নসীহত"
        ),
        DailyNasihot(
            title = "পবিত্র কুরআন তিলাওয়াত ও তাদাব্বুর",
            advice = "প্রতিদিন অন্তত এক রুকু হলেও অর্থ ও তাদাব্বুর (গভীর অনুধাবন) সহকারে কুরআন তিলাওয়াত করুন। কুরআন পরকালে তার তিলাওয়াতকারীর জন্য মহান আল্লাহর দরবারে সুপারিশ করবে।",
            reference = "সহীহ মুসলিম ৮০৪",
            category = "কুরআন",
            date = "হিদায়াত"
        ),
        DailyNasihot(
            title = "গোপনে সদকা ও দানের বরকত",
            advice = "গোপনে দান আল্লাহর ক্রোধ প্রশমিত করে এবং বালা-মুসিবত দূর করে। সামর্থ্য যতটুকুই হোক, প্রতিদিন একটি ভালো কাজের মাধ্যমে সদকার সওয়াব অর্জন করুন। একটি মিষ্টি হাসিও সদকা।",
            reference = "জামে তিরমিযী ৬৬৪",
            category = "সদকা",
            date = "ফজিলত"
        ),
        DailyNasihot(
            title = "আল্লাহর উপর তাওয়াক্কুল (পরম নির্ভরতা)",
            advice = "যে ব্যক্তি আল্লাহর ওপর পূর্ণ ভরসা করে, তার যাবতীয় প্রয়োজনে আল্লাহই যথেষ্ট। জীবনের যেকোনো কঠিন পরিস্থিতিতে চেষ্টা চালিয়ে যান এবং ফলাফল আল্লাহর হাতে সঁপে দিন।",
            reference = "সূরা আত-তালাক ৬৫:৩",
            category = "ঈমান",
            date = "হিকমত"
        ),
        DailyNasihot(
            title = "উত্তম চরিত্র ও ক্ষমাশীলতা",
            advice = "রাসূলুল্লাহ ﷺ বলেছেন: কিয়ামতের দিন মুমিনের আমলনামায় সচ্চরিত্রের চেয়ে ভারী আর কিছুই থাকবে না। মানুষের ভুলত্রুটি ক্ষমা করে উদার মন নিয়ে সমাজে চলুন।",
            reference = "জামে তিরমিযী ২০০২",
            category = "আখলাক",
            date = "সদাচরণ"
        ),
        DailyNasihot(
            title = "তাহাজ্জুদ ও শেষ রাতের মুনাজাত",
            advice = "রাতের শেষ তৃতীয়াংশে মহান আল্লাহ প্রথম আসমানে অবতীর্ণ হয়ে ডাক দেন: 'কে আছো ক্ষমা চাওয়ার, যাকে আমি ক্ষমা করব?' এই অমূল্য সময়ে দুই রাকাত নামাজ পড়ে হাত তুলে মনের কথা বলুন।",
            reference = "সহীহ বুখারী ১১৪৫",
            category = "ইবাদত",
            date = "নূরানী প্রহর"
        )
    )

    fun getTodayPrayerTimes(
        latitude: Double = 23.8103,
        longitude: Double = 90.4125,
        locationName: String = "ঢাকা (বাংলাদেশ)",
        calendar: Calendar = Calendar.getInstance()
    ): List<PrayerTimeInfo> {
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        val calculated = com.example.data.service.PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            calendar = calendar,
            locationName = locationName
        )

        val schedule = listOf(
            Triple("ফজর (সুবহে সাদিক)", "الفجر", calculated.fajrMinutes),
            Triple("সূর্যোদয়", "الشروق", calculated.sunriseMinutes),
            Triple("যোহর", "الظهر", calculated.dhuhrMinutes),
            Triple("আসর (হানাফী)", "العصر", calculated.asrMinutes),
            Triple("মাগরিব ও ইফতার", "المغرب", calculated.maghribMinutes),
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
            val tomorrow = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
            val nextFajr = com.example.data.service.PrayerTimeCalculatorService.calculatePrayerTimes(
                latitude, longitude, tomorrow, locationName
            )
            result[0] = fajr.copy(name = "ফজর (আগামীকাল)", isNext = true, isPassed = false,
                timeMinutes = nextFajr.fajrMinutes,
                timeString = nextFajr.formatTime(nextFajr.fajrMinutes))
        }

        return result
    }

    fun getDetailedPrayerSchedule(
        latitude: Double = 23.8103,
        longitude: Double = 90.4125,
        locationName: String = "ঢাকা (বাংলাদেশ)"
    ): com.example.data.service.PrayerTimeCalculatorService.CalculatedPrayerSchedule {
        return com.example.data.service.PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = latitude,
            longitude = longitude,
            calendar = Calendar.getInstance(),
            locationName = locationName
        )
    }

    fun getNextPrayerCountdown(
        latitude: Double = 23.8103,
        longitude: Double = 90.4125,
        locationName: String = "ঢাকা (বাংলাদেশ)",
        calendar: Calendar = Calendar.getInstance()
    ): Triple<String, String, Long> {
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
        val tomorrow = (calendar.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }
        val tomorrowFajr = com.example.data.service.PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude, longitude, tomorrow, locationName
        ).fajrMinutes

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
                val diff = (secondsUntilMidnight + tomorrowFajr * 60).toLong()
                Triple("ফজর (সেহরি শেষ)", "আগামীকালের রোজা শুরু", diff)
            }
        }
    }

    val initialForumPosts = listOf(
        ForumPost(
            id = "p_dua_1",
            authorName = "মুহাম্মদ আব্দুল্লাহ (ময়মনসিংহ)",
            authorRole = "দ্বীনি ভাই",
            timeAgo = "১০ মিনিট আগে",
            category = "দোয়ার দরখাস্ত",
            questionText = "আসসালামু আলাইকুম প্রিয় দ্বীনি ভাই-বোনেরা। আমার শ্রদ্ধেয় পিতা হঠাৎ অসুস্থ হয়ে হাসপাতালে ভর্তি আছেন। আপনারা সকলেই আল্লাহর ওয়াস্তে তাঁর দ্রুত আরোগ্য ও মাগফিরাতের জন্য খাস দোয়া করবেন। আল্লাহ আপনাদের উত্তম প্রতিদান দান করুন। 🤲",
            likes = 48,
            isLiked = true,
            aminCount = 65,
            isAminGiven = true,
            sharesCount = 5,
            replies = listOf(
                ForumReply(
                    id = "r_dua_1",
                    authorName = "হাফেজ মাহমুদ হাসান",
                    replyText = "আমিন ইয়া রব্বাল আলামীন। আল্লাহ তাআলা চাচাকে শিফায়ে কামিলা ও আজিলা নসিব করুন। এই মাসনূন দোয়াটি পাঠ করবেন: 'আল্লাহুম্মা রব্বান নাস, আজহিবিল বা'স, ইশফি আনতাশ শাফী...'",
                    timeAgo = "৫ মিনিট আগে",
                    isAiModerator = false
                ),
                ForumReply(
                    id = "r_dua_ai",
                    authorName = "আল-হুজুর এআই স্কলার",
                    replyText = "আমিন। অসুস্থ ব্যক্তির জন্য রাসূলুল্লাহ ﷺ-এর শেখানো অন্যতম শ্রেষ্ঠ দোয়া: 'আসআলুল্লাহাল আযীম, রব্বাল আরশিল আযীম, আঁই ইয়াশফিয়াক' (আমি মহান আল্লাহর কাছে প্রার্থনা করছি যিনি মহান আরশের রব, তিনি যেন আপনাকে আরোগ্য দান করেন)। এটি ৭ বার পাঠ করা অত্যন্ত বরকতময়। (আবু দাউদ ৩১০৬)",
                    timeAgo = "৩ মিনিট আগে",
                    isAiModerator = true,
                    verifiedReference = "সুনানে আবু দাউদ ৩১০৬ / সহীহুল জামি ১৮০৫"
                )
            )
        ),
        ForumPost(
            id = "p_hadith_2",
            authorName = "মুফতি আব্দুর রহমান (রাজশাহী)",
            authorRole = "স্কলার ও শিক্ষক",
            isVerified = true,
            timeAgo = "৩৫ মিনিট আগে",
            category = "সুন্নাহ ও হাদিস",
            questionText = "দৈনন্দিন জীবনে জিহ্বা ও অন্তরের পবিত্রতা রক্ষা করা মুমিনের অন্যতম শ্রেষ্ঠ সৌন্দর্য। আসুন আমরা প্রত্যেকে আজ অন্তত ১০০ বার ইস্তিগফার পাঠ করি।",
            quoteOrAyatText = "مَنْ كَانَ يُؤْمِنُ بِاللَّهِ وَالْيَوْمِ الآخِرِ فَلْيَقُلْ خَيْرًا أَوْ لِيَصْمُتْ",
            quoteReference = "যে ব্যক্তি আল্লাহ ও পরকালের প্রতি ঈমান রাখে, সে যেন ভালো কথা বলে অথবা চুপ থাকে। — সহীহ বুখারী ৬০১৮, সহীহ মুসলিম ৪৭",
            likes = 92,
            isLiked = false,
            sharesCount = 28,
            replies = listOf(
                ForumReply(
                    id = "r_h2_1",
                    authorName = "সাদিয়া জামান",
                    replyText = "জাযাকাল্লাহু খাইরান শায়খ। এই হাদিসটি অন্তরে গভীরভাবে দাগ কাটে। আল্লাহ আমাদের জবানকে গীবত থেকে হেফাজত করুন।",
                    timeAgo = "২০ মিনিট আগে"
                )
            )
        ),
        ForumPost(
            id = "p1",
            authorName = "ডেমো প্রশ্নকারী",
            authorRole = "দ্বীনি ভাই",
            timeAgo = "৪৫ মিনিট আগে",
            category = "রমজান ও রোজা",
            questionText = "ভুলবশত বা অন্যমনস্ক হয়ে রোজার মধ্যে কিছু খেয়ে ফেললে বা পানি পান করলে কি রোজা ভেঙে যাবে?",
            likes = 36,
            isLiked = false,
            sharesCount = 12,
            replies = listOf(
                ForumReply(
                    id = "r1_ai",
                    authorName = "আল-হুজুর এআই স্কলার",
                    replyText = "শরয়ী সমাধান: ভুলবশত পানাহার করলে রোজা নষ্ট হয় না, রোজা সম্পূর্ণ সহীহ থাকবে। রাসূলুল্লাহ ﷺ বলেছেন: 'যে ব্যক্তি রোজা থাকা অবস্থায় ভুলে গিয়ে পানাহার করল, সে যেন তার রোজা পূর্ণ করে। কারণ আল্লাহই তাকে খাইয়েছেন এবং পান করিয়েছেন।' (সহীহ বুখারী ১৯৩৩, মুসলিম ১১৫৫)। এর জন্য কোনো কাজা বা কাফফারা ওয়াজিব হয় না।",
                    timeAgo = "৪০ মিনিট আগে",
                    isAiModerator = true,
                    verifiedReference = "সহীহ আল-বুখারী ১৯৩৩ / ফিকহুস সুন্নাহ"
                ),
                ForumReply(
                    id = "r1_u1",
                    authorName = "ডেমো ব্যবহারকারী",
                    replyText = "সুবহানাল্লাহ! আল্লাহর রহমত কত অপরিসীম। কাল ইফতারের প্রস্তুতি নেওয়ার সময় আমার মায়ের এমন হয়েছিল, এই মাসয়ালায় অনেক প্রশান্তি পেলাম।",
                    timeAgo = "২৫ মিনিট আগে",
                    isAiModerator = false
                )
            )
        ),
        ForumPost(
            id = "p2",
            authorName = "মারিয়াম বেগম (চট্টগ্রাম)",
            authorRole = "দ্বীনি বোন",
            timeAgo = "১ ঘণ্টা আগে",
            category = "ফিকহ ও নামাজ",
            questionText = "ছোট বাচ্চা বা কাজের কারণে মসজিদে যেতে না পারলে ঘরে একাকী বা পরিবারের সাথে তারাবীহর নামাজ পড়া যাবে কি?",
            likes = 29,
            isLiked = true,
            sharesCount = 8,
            replies = listOf(
                ForumReply(
                    id = "r2_ai",
                    authorName = "আল-হুজুর এআই স্কলার",
                    replyText = "শরয়ী সমাধান: হ্যাঁ, ঘরে একাকী বা পরিবারের সদস্যদের সাথে তারাবীহর নামাজ সম্পূর্ণ আদায় করা জায়েজ। মা-বোনদের জন্য ঘরে তারাবীহ ও নামাজ আদায় করা অধিক সওয়াবের। পুরুষরাও ওজরের কারণে ঘরে একাকী বা জামাত করে ২০ রাকাত আদায় করতে পারবেন।",
                    timeAgo = "৫০ মিনিট আগে",
                    isAiModerator = true,
                    verifiedReference = "সহীহ বুখারী ২০১২ / ফাতাওয়া শামী"
                )
            )
        ),
        ForumPost(
            id = "p3",
            authorName = "জায়েদ হাসান (সিলেট)",
            authorRole = "তালেবে ইলম",
            timeAgo = "৩ ঘণ্টা আগে",
            category = "কুরআন ও নাসীহত",
            questionText = "শবে কদর ও রমজানের শেষ দশকে বেশি বেশি পড়ার জন্য সবচেয়ে উত্তম দোয়া কোনটি এবং এর অর্থ কী?",
            quoteOrAyatText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            quoteReference = "হে আল্লাহ! আপনি নিশ্চয়ই ক্ষমাশীল, ক্ষমা করা পছন্দ করেন; অতএব আমাকে ক্ষমা করে দিন। — জামে তিরমিযী ৩৫১৩ (সহীহ)",
            likes = 78,
            isLiked = false,
            sharesCount = 34,
            replies = listOf(
                ForumReply(
                    id = "r3_ai",
                    authorName = "আল-হুজুর এআই স্কলার",
                    replyText = "হযরত আয়েশা (রা.) রাসূলুল্লাহ ﷺ-কে জিজ্ঞাসা করেছিলেন: 'হে আল্লাহর রাসূল! আমি যদি লাইলাতুল কদর পাই, তবে কী দোয়া করব?' তিনি ﷺ বললেন: 'তুমি বলবে— আল্লাহুম্মা ইন্নাকা আফুউন, তুহিব্বুল আফওয়া, ফা'ফু আন্নী।' এটি ছোট কিন্তু পরম ব্যাপক একটি দোয়া যাতে দুনিয়া ও আখেরাতের সর্বশ্রেষ্ঠ নিয়ামত অর্থাৎ মাগফিরাত চাওয়া হয়েছে।",
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

    val ramadanSpecialDuas = listOf(
        RamadanSpecialDua(
            id = "dua_sehri_niyyah",
            title = "রোজার নিয়ত (সাহরির পর)",
            occasion = "সাহরি খাওয়া সম্পন্ন করার পর মনে মনে বা মুখে নিয়ত",
            arabicText = "نَوَيْتُ أَنْ أَصُومَ غَدًا مِنْ شَهْرِ رَمَضَانَ الْمُبَارَكِ فَرْضًا لَكَ يَا اللَّهُ فَتَقَبَّلْ مِنِّي إِنَّكَ أَنْتَ السَّمِيعُ الْعَلِيمُ",
            pronunciationBn = "নাওয়াইতু আন আসূমা গাদাম মিন শাহরি রমাদানাল মুবারাকি ফারদাল্লাকা ইয়া আল্লাহু ফাতাকাব্বাল মিন্নী, ইন্নাকা আন্তাস সামী'উল 'আলীম।",
            meaningBn = "হে আল্লাহ! আমি আগামীকাল পবিত্র রমজান মাসের ফরজ রোজা রাখার নিয়ত করলাম। অতএব আপনি আমার পক্ষ থেকে তা কবুল করুন, নিশ্চয়ই আপনি সর্বশ্রোতা ও সর্বজ্ঞ।",
            reference = "ফাতাওয়া তাতারখানিয়া • মনে মনে রোজার সংকল্প করাই মূল নিয়ত।"
        ),
        RamadanSpecialDua(
            id = "dua_iftar_time",
            title = "ইফতারের সময়ের দোয়া",
            occasion = "ইফতার মুখে দেওয়ার পূর্বে পাঠ্য মাসনূন দোয়া",
            arabicText = "اللَّهُمَّ لَكَ صُمْتُ وَعَلَى رِزْقِكَ أَفْطَرْتُ",
            pronunciationBn = "আল্লাহুম্মা লাকা সুমতু ওয়া 'আলা রিযক্বিকা আফতারতু।",
            meaningBn = "হে আল্লাহ! আমি আপনার সন্তুষ্টির জন্যই রোজা রেখেছি এবং আপনার দেওয়া রিজিক দিয়েই ইফতার করছি।",
            reference = "আবু দাউদ ২৩৫৮, মিশকাতুল মাসাবীহ"
        ),
        RamadanSpecialDua(
            id = "dua_iftar_after",
            title = "ইফতারের পর পঠিত সুন্নাত দোয়া",
            occasion = "খেজুর ও পানি পানের পর রাসূলুল্লাহ ﷺ এর পঠিত সহীহ দোয়া",
            arabicText = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
            pronunciationBn = "জাহাবায জামা'উ ওয়াবতাল্লাতিল 'উরূকু ওয়া সাবাতাল আজরু ইনশাআল্লাহ।",
            meaningBn = "পিপাসা দূরীভূত হলো, শিরা-উপশিরা সিক্ত হলো এবং ইনশাআল্লাহ প্রতিদান নিশ্চিত হলো।",
            reference = "সুনানে আবু দাউদ ২৩৫৭, আল-আলবানী সহীহ বলেছেন"
        ),
        RamadanSpecialDua(
            id = "dua_tarabi_tasbeeh",
            title = "তারাবীহর প্রতি চার রাকাত পর পঠিত তাসবীহ",
            occasion = "তারাবীহর প্রতি চার রাকাত পর বিশ্রামের সময় পঠিত তাসবীহ",
            arabicText = "سُبْحَانَ ذِي الْمُلْكِ وَالْمَلَكُوتِ، سُبْحَانَ ذِي الْعِزَّةِ وَالْعَظَمَةِ وَالْهَيْبَةِ وَالْقُدْرَةِ وَالْكِبْرِيَاءِ وَالْجَبَرُوتِ، سُبْحَانَ الْمَلِكِ الْحَيِّ الَّذِي لَا يَنَامُ وَلَا يَمُوتُ، سُبُّوحٌ قُدُّوسٌ رَبُّنَا وَرَبُّ الْمَلَائِكَةِ وَالرُّوحِ",
            pronunciationBn = "সুবহানা যিল মুলকি ওয়াল মালাকূত, সুবহানা যিল 'ইয্যাতি ওয়াল 'আজমাতি ওয়াল হাইবাতি ওয়াল কুদরাতি ওয়াল কিবরিয়ায়ি ওয়াল জাবারূত। সুবহানাল মালিকিল হাইয়্যিল্লাজি লা ইয়ানামু ওয়ালা ইয়ামূতু, সুব্বূহুন কুদ্দূসুন রব্বুনা ওয়া রব্বুল মালাইকাতি ওয়ার রূহ।",
            meaningBn = "পবিত্র সেই সত্তা যিনি পার্থিব ও স্বর্গীয় সাম্রাজ্যের মালিক। পবিত্র সেই সত্তা যিনি সম্মান, মাহাত্ম্য, ভয়-ভীতি, কুদরত, শ্রেষ্ঠত্ব ও প্রভাব-প্রতিপত্তির অধিকারী। পবিত্র সেই চিরঞ্জীব বাদশা যিনি নিদ্রা যান না এবং কখনো মৃত্যুবরণ করবেন না। মহাপবিত্র ও সমস্ত ত্রুটিমুক্ত আমাদের পালনকর্তা এবং ফেরেশতাকুল ও জিব্রাইল (আ.)-এর পালনকর্তা।",
            reference = "হানাফী ফিকহ ও প্রচলিত সুন্নাহ মুস্তাহাব তাসবীহ"
        ),
        RamadanSpecialDua(
            id = "dua_tarabi_munajat",
            title = "তারাবীহ নামাজের বিশেষ মুনাজাত",
            occasion = "তারাবীহ নামাজ সমাপ্ত হওয়ার পর মহান আল্লাহর দরবারে ক্ষমা প্রার্থনার দোয়া",
            arabicText = "اللَّهُمَّ إِنَّا نَسْأَلُكَ الْجَنَّةَ وَنَعُوذُ بِكَ مِنَ النَّارِ، يَا خَالِقَ الْجَنَّةِ وَالنَّارِ، بِرَحْمَتِكَ يَا عَزِيزُ يَا غَفَّارُ يَا كَرِيمُ يَا سَتَّارُ يَا رَحِيمُ يَا مُجِيرُ",
            pronunciationBn = "আল্লাহুম্মা ইন্না নাসআলুকাল জান্নাতা ওয়া না'ঊযু বিকা মিনান নার, ইয়া খলিক্বাল জান্নাতি ওয়ান নার, বিরাহমাতিকা ইয়া 'আযীযু ইয়া গাফফারু ইয়া কারীমু ইয়া সাত্তারু ইয়া রাহীমু ইয়া মুজীর।",
            meaningBn = "হে আল্লাহ! আমরা আপনার নিকট জান্নাতের আবেদন করছি এবং জাহান্নামের আগুন থেকে আশ্রয় প্রার্থনা করছি। হে জান্নাত ও জাহান্নামের স্রষ্টা! আপনার দয়ার উসিলায় আমাদেরকে রক্ষা করুন, হে পরাক্রমশালী, হে ক্ষমাশীল, হে দয়াবান, হে দোষ গোপনকারী, হে পরম করুণাময়, হে উদ্ধারকারী!",
            reference = "মুস্তাহাব তারাবীহ মুনাজাত"
        ),
        RamadanSpecialDua(
            id = "dua_laylatul_qadr",
            title = "লাইলাতুল কদরের বিশেষ দোয়া",
            occasion = "রমজানের শেষ দশকে বিশেষ করে বেজোড় রজনীগুলোতে পাঠ্য",
            arabicText = "اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي",
            pronunciationBn = "আল্লাহুম্মা ইন্নাকা 'আফুউয়্যুন তুহিব্বুল 'আফওয়া ফা'ফু 'আন্নী।",
            meaningBn = "হে আল্লাহ! আপনি পরম ক্ষমাশীল, ক্ষমা করাকে আপনি ভালোবাসেন, অতএব আমাকে ক্ষমা করে দিন।",
            reference = "তিরমিযী ৩৫১৩, ইবনে মাজাহ ৩৮৫০"
        )
    )

    /** No invented times. UI displays a clear availability warning. */
    fun getRamadan30Days(offsetMinutes: Int = 0): List<RamadanCalendarDay> = emptyList()

    fun getDistrictOffsetMinutes(cityName: String): Int {
        return when {
            cityName.contains("চট্টগ্রাম") || cityName.contains("কক্সবাজার") -> -5
            cityName.contains("সিলেট") || cityName.contains("ব্রাহ্মণবাড়িয়া") -> -6
            cityName.contains("কুমিল্লা") || cityName.contains("নোয়াখালী") -> -4
            cityName.contains("ময়মনসিংহ") || cityName.contains("গাজীপুর") || cityName.contains("টাঙ্গাইল") -> 0
            cityName.contains("বরিশাল") -> 1
            cityName.contains("খুলনা") || cityName.contains("যশোর") -> 5
            cityName.contains("রাজশাহী") || cityName.contains("পাবনা") -> 7
            cityName.contains("রংপুর") || cityName.contains("দিনাজপুর") || cityName.contains("বগুড়া") -> 6
            cityName.contains("কুষ্টিয়া") || cityName.contains("ফরিদপুর") -> 4
            else -> 0
        }
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
