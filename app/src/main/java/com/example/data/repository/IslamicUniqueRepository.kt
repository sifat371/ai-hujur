package com.example.data.repository

import com.example.data.model.AllahName
import com.example.data.model.QuranVerseOfTheDay

object IslamicUniqueRepository {

    val quranVerses = listOf(
        QuranVerseOfTheDay(
            surahNameBn = "সূরা আল-বাক্বারাহ",
            surahNumber = 2,
            ayahNumber = 186,
            arabicText = "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ ۖ فَلْيَسْتَجِيبُوا لِي وَلْيُؤْمِنُوا بِي لَعَلَّهُمْ يَرْشُدُونَ",
            pronunciationBn = "ওয়া ইযা সা'আলাকা ইবাদী আন্নী ফাইন্নী ক্বারীব, উজীবু দা'ওয়াতাদ দা'ই ইযা দা'আনি...",
            meaningBn = "আর আমার বান্দারা যখন আপনার কাছে আমার সম্পর্কে জিজ্ঞেস করে, নিশ্চয়ই আমি অত্যন্ত নিকটবর্তী। যখন কোনো আহ্বানকারী আমাকে ডাকে, আমি তার ডাকে সাড়া দিই। অতএব তারাও যেন আমার হুকুম মেনে নেয় এবং আমার প্রতি ঈমান আনে, যাতে তারা সৎপথে চলতে পারে।",
            reflectionBn = "মহান আল্লাহ বান্দার প্রতিটি আকুল আবেদন শোনেন। নিভৃতে যেকোনো সময় তাঁর কাছে মনের কথা খুলে বলুন।"
        ),
        QuranVerseOfTheDay(
            surahNameBn = "সূরা আল-ইনশিরাহ",
            surahNumber = 94,
            ayahNumber = 5,
            arabicText = "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝ إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            pronunciationBn = "ফাইন্না মা'আল উসরি ইউসরা। ইন্না মা'আল উসরি ইউসরা।",
            meaningBn = "নিশ্চয়ই কষ্টের সাথেই রয়েছে স্বস্তি। নিশ্চয়ই কষ্টের সাথেই রয়েছে স্বস্তি।",
            reflectionBn = "জীবনের যেকোনো কঠিন সংকট বা পরীক্ষায় ধৈর্য ধারণ করুন। প্রতিটি দুঃখের পরেই আল্লাহর রহমতের পথ খুলে যায়।"
        ),
        QuranVerseOfTheDay(
            surahNameBn = "সূরা আশ-শূরা",
            surahNumber = 42,
            ayahNumber = 19,
            arabicText = "اللَّهُ لَطِيفٌ بِعِبَادِهِ يَرْزُقُ مَن يَشَاءُ ۖ وَهُوَ الْقَوِيُّ الْعَزِيزُ",
            pronunciationBn = "আল্লাহু লাত্বীফুম বি-ইবাদিহী ইয়ারযুক্বু মাইঁইয়াশাউ, ওয়াহুওয়াল ক্বাবিয়্যুল আযীয।",
            meaningBn = "আল্লাহ তাঁর বান্দাদের প্রতি অতিশয় মেহেরবান। তিনি যাকে ইচ্ছা অপরিমিত রিজিক দান করেন। তিনি মহা পরাক্রমশালী, পরাক্রান্ত।",
            reflectionBn = "রিজিক নিয়ে অতিরিক্ত দুশ্চিন্তা না করে আল্লাহর ওপর ভরসা করুন এবং হালাল উপার্জনে নিমগ্ন থাকুন।"
        ),
        QuranVerseOfTheDay(
            surahNameBn = "সূরা আন-নূর",
            surahNumber = 24,
            ayahNumber = 35,
            arabicText = "اللَّهُ نُورُ السَّمَاوَاتِ وَالْأَرْضِ ۚ مَثَلُ نُورِهِ كَمِشْكَاةٍ فِيهَا مِصْبَاحٌ",
            pronunciationBn = "আল্লাহু নূরুস সামাওয়াতি ওয়াল আরদ্ব, মাছালু নূরিহী কামিশকাতিন ফীহা মিছবাহ...",
            meaningBn = "আল্লাহ আসমান ও জমিনের জ্যোতি। তাঁর জ্যোতির উপমা যেন একটি দীপাধার, যার মধ্যে রয়েছে একটি প্রদীপ...",
            reflectionBn = "আল্লাহর নূর হৃদয়ে ধারণ করতে পবিত্র কুরআন তেলাওয়াত ও সুন্নাহর অনুকরণ অপরিহার্য।"
        )
    )

    fun getTodayVerse(): QuranVerseOfTheDay {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        return quranVerses[dayOfYear % quranVerses.size]
    }

    val asmaulHusnaList = listOf(
        AllahName(
            number = 1,
            arabic = "الرَّحْمَنُ",
            transliteration = "আর-রাহমান (Ar-Rahman)",
            meaningBn = "পরম দয়ালু",
            explanationBn = "যিনি সৃষ্টিজগতের প্রতিটি প্রাণীর উপর অসীম ও সার্বজনীন করুণা বর্ষণ করেন।",
            spiritualBenefitBn = "অধিক পাঠে হৃদয়ে দয়া ও নম্রতা সৃষ্টি হয় এবং আল্লাহর বিশেষ রহমত লাভ হয়।"
        ),
        AllahName(
            number = 2,
            arabic = "الرَّحِيمُ",
            transliteration = "আর-রাহীম (Ar-Raheem)",
            meaningBn = "পরম করুণাময়",
            explanationBn = "বিশেষ করে মুমিন ও বিশ্বাসীদের জন্য পরকালে চিরস্থায়ী রহমত প্রদানকারী।",
            spiritualBenefitBn = "দৈনিক জিকিরে বিপদ-আপদ ও দুশ্চিন্তা দূর হয়।"
        ),
        AllahName(
            number = 3,
            arabic = "الْمَلِكُ",
            transliteration = "আল-মালিক (Al-Malik)",
            meaningBn = "সার্বভৌম ক্ষমতার অধিকারী / বাদশাহ",
            explanationBn = "যিনি সমগ্র মহাবিশ্ব ও সকল সৃষ্টির প্রকৃত মালিক এবং চিরন্তন শাসক।",
            spiritualBenefitBn = "আত্মমর্যাদা ও অন্তরের স্বাধীনতা বৃদ্ধি পায়।"
        ),
        AllahName(
            number = 4,
            arabic = "الْقُدُّوسُ",
            transliteration = "আল-কুদ্দুস (Al-Quddus)",
            meaningBn = "নিষ্কলুষ / অতি পবিত্র",
            explanationBn = "সকল ত্রুটি, সীমাবদ্ধতা এবং অপূর্ণতা থেকে যিনি সম্পূর্ণরূপে পবিত্র।",
            spiritualBenefitBn = "অন্তর ও চিন্তার পবিত্রতা অর্জনে ফলপ্রসূ।"
        ),
        AllahName(
            number = 5,
            arabic = "السَّلَامُ",
            transliteration = "আস-সালাম (As-Salam)",
            meaningBn = "শান্তি দানকারী / সুরক্ষাকারী",
            explanationBn = "যিনি সকল অশান্তি ও বিপদ থেকে শান্তি ও নিরাপত্তা প্রদান করেন।",
            spiritualBenefitBn = "পারিবারিক শান্তি ও মানসিক স্থিরতার জন্য সহায়ক।"
        ),
        AllahName(
            number = 6,
            arabic = "الْمُؤْمِنُ",
            transliteration = "আল-মু'মিন (Al-Mu'min)",
            meaningBn = "নিরাপত্তা ও ঈমান দানকারী",
            explanationBn = "যিনি তাঁর বান্দাদের অন্তরে সত্যের বিশ্বাস স্থাপন করান এবং নিরাপদ রাখেন।",
            spiritualBenefitBn = "ভয় ও অনিরাপত্তাবোধ দূর হয়।"
        ),
        AllahName(
            number = 7,
            arabic = "الْمُهَيْمِنُ",
            transliteration = "আল-মুহাইমিন (Al-Muhaymin)",
            meaningBn = "রক্ষাকর্তা / অভিভাবক",
            explanationBn = "যিনি প্রতিটি সৃষ্টির প্রতিটি মুহূর্ত প্রত্যক্ষ করেন ও হেফাজত করেন।",
            spiritualBenefitBn = "আল্লাহর অভিভাবকত্বে অবিচল আস্থা তৈরি হয়।"
        ),
        AllahName(
            number = 8,
            arabic = "الْعَزِيزُ",
            transliteration = "আল-আযীয (Al-Aziz)",
            meaningBn = "মহা পরাক্রমশালী / সম্মানিত",
            explanationBn = "যাঁর ক্ষমতার সামনে কোনো শক্তির প্রতিরোধ টিকতে পারে না।",
            spiritualBenefitBn = "মানুষের কাছে সম্মান ও সম্মানজনক জীবিকার পথ সুগম হয়।"
        ),
        AllahName(
            number = 9,
            arabic = "الْجَبَّارُ",
            transliteration = "আল-জাব্বার (Al-Jabbar)",
            meaningBn = "মহাপ্রতাপশালী / সংশোধনকারী",
            explanationBn = "যিনি ভগ্ন হৃদয় জোড়া লাগান এবং সবকিছুকে পূর্ণতা দেন।",
            spiritualBenefitBn = "ভাঙ্গা মন জোড়া লাগাতে ও প্রতিকূলতা মোকাবিলায় শক্তি যোগায়।"
        ),
        AllahName(
            number = 10,
            arabic = "الْمُتَكَبِّرُ",
            transliteration = "আল-মুতাকাব্বির (Al-Mutakabbir)",
            meaningBn = "সর্বশ্রেষ্ঠ মর্যাদাবান",
            explanationBn = "প্রকৃত অহংকার ও শ্রেষ্ঠত্ব কেবল তাঁর জন্যই শোভা পায়।",
            spiritualBenefitBn = "অহংকার দূর করে বিনয়ী হতে সাহায্য করে।"
        ),
        AllahName(
            number = 11,
            arabic = "الْخَالِقُ",
            transliteration = "আল-খালিক্ব (Al-Khaliq)",
            meaningBn = "সৃষ্টিকর্তা",
            explanationBn = "যিনি অনস্তিত্ব থেকে সমস্ত কিছুকে অস্তিত্বে আনয়ন করেছেন।",
            spiritualBenefitBn = "সৃষ্টির সৌন্দর্য উপলব্ধি ও কৃতজ্ঞতাবোধ জাগ্রত হয়।"
        ),
        AllahName(
            number = 12,
            arabic = "الْغَفَّارُ",
            transliteration = "আল-গাফ্ফার (Al-Ghaffar)",
            meaningBn = "পরম ক্ষমাশীল",
            explanationBn = "যিনি বান্দার বারবার করা গুনাহসমূহ বারবার মার্জনা করে ঢেকে দেন।",
            spiritualBenefitBn = "গুনাহ মাফ ও আল্লাহর সন্তুষ্টি অর্জনে কার্যকরী।"
        ),
        AllahName(
            number = 13,
            arabic = "الْوَهَّابُ",
            transliteration = "আল-ওয়াহ্হাব (Al-Wahhab)",
            meaningBn = "মহাদাতা",
            explanationBn = "যিনি কোনো বিনিময় প্রত্যাশা ছাড়া অফুরন্ত নেয়ামত দান করেন।",
            spiritualBenefitBn = "অভাব দূরীকরণ ও অপ্রত্যাশিত কল্যাণের জন্য অত্যন্ত মোবারক।"
        ),
        AllahName(
            number = 14,
            arabic = "الرَّزَّاقُ",
            transliteration = "আর-রায্যাক্ব (Ar-Razzaq)",
            meaningBn = "রিজিকদাতা",
            explanationBn = "যিনি প্রত্যেকটি প্রাণীর আহার, শক্তি ও আধ্যাত্মিক খাদ্যের ব্যবস্থা করেন।",
            spiritualBenefitBn = "হালাল রিজিকের সন্ধান ও আর্থিক বরকতের জন্য পঠিতব্য।"
        ),
        AllahName(
            number = 15,
            arabic = "الْفَتَّاحُ",
            transliteration = "আল-ফাত্তাহ (Al-Fattah)",
            meaningBn = "বিজয় ও পথ উন্মোচনকারী",
            explanationBn = "যিনি প্রতিটি জটিলতা নিরসন করেন এবং রহমতের দ্বার খুলে দেন।",
            spiritualBenefitBn = "কঠিন কাজের সহজ সমাধান ও সফলতার জন্য বিশেষ সহায়ক।"
        ),
        AllahName(
            number = 16,
            arabic = "الْعَلِيمُ",
            transliteration = "আল-আলীম (Al-Aleem)",
            meaningBn = "সর্বজ্ঞ",
            explanationBn = "অতীত, বর্তমান ও ভবিষ্যতের দৃশ্য-অদৃশ্য সবকিছুর পূর্ণ জ্ঞান রাখেন।",
            spiritualBenefitBn = "জ্ঞান ও মেধা বিকাশের জন্য বিশেষ উপকারী।"
        ),
        AllahName(
            number = 17,
            arabic = "السَّمِيعُ",
            transliteration = "আস-সামী' (As-Samee)",
            meaningBn = "সর্বশ্রোতা",
            explanationBn = "যিনি মনের প্রতিটি নিঃশব্দ ফিসফিসানি ও চোখের কান্নার শব্দও শোনেন।",
            spiritualBenefitBn = "দোয়া কবুলিয়াত ও সার্বক্ষণিক সচেতনতা তৈরি করে।"
        ),
        AllahName(
            number = 18,
            arabic = "الْبَصِيرُ",
            transliteration = "আল-বাশীর (Al-Baseer)",
            meaningBn = "সর্বদ্রষ্টা",
            explanationBn = "যাঁর দৃষ্টি থেকে অন্ধকারের একটি সূক্ষ্মতম পিপীলিকার গতিবিধিও গোপন নয়।",
            spiritualBenefitBn = "তাকওয়া ও খোদাভীতি অর্জনে সাহায্য করে।"
        ),
        AllahName(
            number = 19,
            arabic = "الْحَكِيمُ",
            transliteration = "আল-হাকীম (Al-Hakeem)",
            meaningBn = "পরম প্রজ্ঞাময়",
            explanationBn = "যাঁর প্রতিটি সিদ্ধান্তে এবং বিধানে অপরিসীম প্রজ্ঞা ও কল্যাণ নিহিত।",
            spiritualBenefitBn = "তাকদীরের ফয়সালায় সন্তুষ্ট থাকার মনোবল জোগায়।"
        ),
        AllahName(
            number = 20,
            arabic = "الْوَدُودُ",
            transliteration = "আল-ওয়াদূদ (Al-Wadood)",
            meaningBn = "প্রেমময় ও পরম ভালোবাসাময়",
            explanationBn = "যিনি তাঁর নেককার বান্দাদের অকৃত্রিম গভীর ভালোবাসায় বেষ্টন করে রাখেন।",
            spiritualBenefitBn = "পারিবারিক পারস্পরিক ভালোবাসা ও সম্প্রীতি বৃদ্ধিতে কল্যাণকর।"
        )
    )
}
