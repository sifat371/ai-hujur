package com.example.data.repository

import android.content.Context
import android.content.Intent
import com.example.data.model.DawahCardTemplate
import com.example.data.model.FajrReminderTemplate
import com.example.data.model.QuizQuestion

object ViralFeaturesRepository {

    private const val PREFS_NAME = "viral_features_prefs"
    private const val KEY_MY_DUROOD_COUNT = "my_durood_count"
    private const val KEY_QUIZ_BEST_SCORE = "quiz_best_score"
    private const val KEY_QUIZ_COMPLETED_TODAY = "quiz_completed_today"

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = "q1",
            questionBn = "পবিত্র কুরআনে সর্বমোট কতটি সূরা এবং সিজদার আয়াত রয়েছে?",
            options = listOf("১১৪টি সূরা ও ১৪টি সিজদা", "১১২টি সূরা ও ১৫টি সিজদা", "১১৪টি সূরা ও ১২টি সিজদা", "১১৬টি সূরা ও ১৪টি সিজদা"),
            correctIndex = 0,
            explanationBn = "পবিত্র কুরআনে মোট ১১৪টি সূরা রয়েছে এবং সর্বসম্মত মতে ১৪টি সিজদার আয়াত রয়েছে।",
            referenceBn = "উসুলুল কুরআন",
            category = "কুরআন মাজীদ"
        ),
        QuizQuestion(
            id = "q2",
            questionBn = "পবিত্র কুরআনের কোন সূরাকে 'উম্মুল কুরআন' (কুরআনের মা/মূল) বলা হয়?",
            options = listOf("সূরা আল-ইখলাস", "সূরা ইয়াসিন", "সূরা আল-ফাতিহা", "সূরা আল-বাকারা"),
            correctIndex = 2,
            explanationBn = "রাসূলুল্লাহ (সা.) সূরা আল-ফাতিহাকে 'উম্মুল কুরআন' এবং 'সাবউ আল-মাসানী' (পুনরাবৃত্ত সাত আয়াত) আখ্যা দিয়েছেন।",
            referenceBn = "সহীহ বুখারী: ৪৭০৪",
            category = "কুরআন মাজীদ"
        ),
        QuizQuestion(
            id = "q3",
            questionBn = "ইসলামের দ্বিতীয় খলিফা হযরত উমর (রা.)-এর উপাধি কী ছিল?",
            options = listOf("আস-সিদ্দীক", "আল-ফারুক", "যুন-নুরাইন", "আসাদুল্লাহ"),
            correctIndex = 1,
            explanationBn = "হযরত উমর (রা.)-কে সত্য ও মিথ্যার মধ্যে স্পষ্ট পার্থক্যকারী হিসেবে 'আল-ফারুক' উপাধিতে ভূষিত করা হয়।",
            referenceBn = "তারীখে তাবারি",
            category = "সাহাবায়ে কেরাম"
        ),
        QuizQuestion(
            id = "q4",
            questionBn = "কেয়ামতের দিন বান্দার কাছ থেকে সর্বপ্রথম কোন আমলের হিসাব নেওয়া হবে?",
            options = listOf("পিতা-মাতার খেদমত", "রমজানের রোজা", "সালাত (নামাজ)", "যাকাত আদায়"),
            correctIndex = 2,
            explanationBn = "রাসূলুল্লাহ (সা.) বলেছেন: কিয়ামতের দিন বান্দার আমলসমূহের মধ্যে সর্বপ্রথম সালাতের হিসাব নেওয়া হবে। সালাত সঠিক হলে বাকি সব ঠিক থাকবে।",
            referenceBn = "সুনান আত-তিরমিযী: ৪১৩",
            category = "সালাত ও আমল"
        ),
        QuizQuestion(
            id = "q5",
            questionBn = "হিজরি ক্যালেন্ডারের কোন মাসে পবিত্র লাইলাতুল কদর অবস্থিত?",
            options = listOf("মুহাররম", "রজব", "রমজান", "জিলহজ্জ"),
            correctIndex = 2,
            explanationBn = "রমজান মাসের শেষ দশকের বেজোড় রাতসমূহে মহিমান্বিত লাইলাতুল কদর অন্বেষণ করতে বলা হয়েছে যা হাজার মাসের চেয়েও উত্তম।",
            referenceBn = "সূরা আল-কদর: ৩",
            category = "রমজান ও রোজা"
        ),
        QuizQuestion(
            id = "q6",
            questionBn = "রাসূলুল্লাহ (সা.)-এর দুধমাতার নাম কী ছিল যিনি তাঁর শৈশবে লালন-পালন করেছিলেন?",
            options = listOf("হযরত হালিমা আস-সাদিয়া (রা.)", "হযরত আমেনা (রা.)", "হযরত ফাতিমা (রা.)", "হযরত খাদিজা (রা.)"),
            correctIndex = 0,
            explanationBn = "হযরত হালিমা আস-সাদিয়া (রা.) বনু সাদ গোত্রে পরম মমতায় নবী করীম (সা.)-কে দুধপান ও লালন-পালন করেন।",
            referenceBn = "আর-রাহীকুল মাখতুম",
            category = "সীরাতুন্নবী (সা.)"
        ),
        QuizQuestion(
            id = "q7",
            questionBn = "পবিত্র কুরআনের কোন আয়াতটিকে সর্বোত্তম ও সবচেয়ে মহিমান্বিত আয়াত বলা হয়েছে?",
            options = listOf("আয়াতুল কুরসী", "আমনার রাসূল", "সূরা ইখলাসের শেষ আয়াত", "সূরা ফালাকের প্রথম আয়াত"),
            correctIndex = 0,
            explanationBn = "সূরা আল-বাকারার ২৫৫ নম্বর আয়াত (আয়াতুল কুরসী) হলো কুরআনের সর্বশ্রেষ্ঠ আয়াত।",
            referenceBn = "সহীহ মুসলিম: ৮১০",
            category = "কুরআন মাজীদ"
        ),
        QuizQuestion(
            id = "q8",
            questionBn = "ইসলামের মৌলিক স্তম্ভ (আরকানুল ইসলাম) কয়টি?",
            options = listOf("৩টি", "৪টি", "৫টি", "৬টি"),
            correctIndex = 2,
            explanationBn = "ইসলামের ভিত্তি পাঁচটি: ঈমান/শাহাদাহ, সালাত, যাকাত, হজ এবং রমজানের সিয়াম।",
            referenceBn = "সহীহ বুখারী: ৮",
            category = "ইসলামিক জ্ঞান"
        )
    )

    val dawahTemplates: List<DawahCardTemplate> = listOf(
        DawahCardTemplate(
            id = "d1",
            titleBn = "কঠিন মুহূর্তে সান্ত্বনা ও ভরসা",
            arabicCalligraphy = "لَا تَحْزَنْ إِنَّ اللَّهَ مَعَنَا",
            banglaTranslation = "\"হতাশ হয়ো না, নিশ্চয়ই আল্লাহ আমাদের সাথে আছেন।\"",
            referenceBn = "সূরা আত-তাওবা: ৪০",
            category = "আশাবাদ ও তাওয়াক্কুল",
            reflectionBn = "জীবনের যেকোনো সংকট বা একাকীত্বে একমাত্র আল্লাহর উপর পূর্ণ তাওয়াক্কুল রাখুন, নিশ্চয়ই তিনি যথেষ্ট।"
        ),
        DawahCardTemplate(
            id = "d2",
            titleBn = "শান্তিময় অন্তরের রহস্য",
            arabicCalligraphy = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            banglaTranslation = "\"জেনে রাখো, কেবল আল্লাহর স্মরণেই অন্তরসমূহ প্রশান্তি লাভ করে।\"",
            referenceBn = "সূরা আর-রাদ: ২৮",
            category = "অন্তরের প্রশান্তি",
            reflectionBn = "ডিজিটাল ব্যস্ততায় মন বিষণ্ণ লাগলে অজু করে কিছু সময় আল্লাহর জিকিরে নিমগ্ন হোন।"
        ),
        DawahCardTemplate(
            id = "d3",
            titleBn = "ক্ষমা ও তওবার দরজা",
            arabicCalligraphy = "إِنَّ اللَّهَ يَغْفِرُ الذُّنُوبَ جَمِيعًا",
            banglaTranslation = "\"নিশ্চয়ই আল্লাহ সমস্ত গুনাহ ক্ষমা করে দেন।\"",
            referenceBn = "সূরা আয-যুমার: ৫৩",
            category = "মাগফিরাত ও তওবা",
            reflectionBn = "কখনো আল্লাহর রহমত থেকে নিরাশ হবেন না; আন্তরিক এক ফোঁটা অশ্রুই গুনাহ মুছে দেওয়ার জন্য যথেষ্ট।"
        ),
        DawahCardTemplate(
            id = "d4",
            titleBn = "ধৈর্য ও সফলতার সুসংবাদ",
            arabicCalligraphy = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            banglaTranslation = "\"নিশ্চয়ই কষ্টের সাথেই স্বস্তি রয়েছে।\"",
            referenceBn = "সূরা আল-ইনশিরাহ: ৬",
            category = "সবর ও সুসংবাদ",
            reflectionBn = "রাত যত গভীর হয়, ভোর তত কাছে আসে। আপনার প্রতিটি ধৈর্য আল্লাহর দরবারে সংরক্ষিত।"
        ),
        DawahCardTemplate(
            id = "d5",
            titleBn = "শ্রেষ্ঠ চরিত্র ও মুচকি হাসি",
            arabicCalligraphy = "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            banglaTranslation = "\"তোমার ভাইয়ের মুখের দিকে তাকিয়ে তোমার মুচকি হাসিও একটি সদকা।\"",
            referenceBn = "সুনান আত-তিরমিযী: ১৯৫৬",
            category = "সাদাকাহ ও আখলাক",
            reflectionBn = "আজ পরিবার ও সহকর্মীদের সাথে হাসিমুখে কথা বলুন এবং সদকার সওয়াব অর্জন করুন।"
        )
    )

    val fajrReminders: List<FajrReminderTemplate> = listOf(
        FajrReminderTemplate(
            id = "f1",
            titleBn = "আল্লাহর বিশেষ জিম্মাদারী",
            messageText = "আসসালামু আলাইকুম প্রিয় ভাই/বোন! ফজরের আজানের সুর ভেসে এসেছে।\n\nরাসূলুল্লাহ (সা.) ইরশাদ করেছেন:\n\"যে ব্যক্তি ফজরের নামাজ আদায় করল, সে আল্লাহর প্রত্যক্ষ জিম্মাদারীতে চলে গেল।\" (সহীহ মুসলিম)\n\nআসুন বিছানার আরাম ত্যাগ করে আল্লাহর সন্তুষ্টির উদ্দেশ্যে সালাতে দাঁড়াই।",
            hadithProof = "সহীহ মুসলিম: ৬৫৭"
        ),
        FajrReminderTemplate(
            id = "f2",
            titleBn = "ঘুমের চেয়ে নামাজ উত্তম",
            messageText = "আসসালামু আলাইকুম! 'আস-সালাতু খাইরুম মিনান নাওম'—ঘুমের চেয়ে সালাত উত্তম।\n\nফজরের দুই রাকাত সুন্নত সারা পৃথিবী ও তার মধ্যকার সবকিছুর চেয়েও উত্তম। বরকতময় দিন শুরু করতে এখনই অজু করে সালাতে আসুন।",
            hadithProof = "সহীহ মুসলিম: ৭২৫"
        ),
        FajrReminderTemplate(
            id = "f3",
            titleBn = "কিয়ামতের দিন পূর্ণাঙ্গ নূর",
            messageText = "আসসালামু আলাইকুম! ফজরের শুভ সকাল।\n\nরাসূলুল্লাহ (সা.) বলেছেন:\n\"যারা অন্ধকারে মসজিদের দিকে হেঁটে যায়, তাদেরকে কিয়ামতের দিন পূর্ণাঙ্গ আলোর সুসংবাদ দাও।\" (তিরমিযী)\n\nচলুন আল্লাহর ঘরে জামাতে সালাত আদায় করি।",
            hadithProof = "সুনান আত-তিরমিযী: ২২৩"
        )
    )

    fun shareTextToSocial(context: Context, text: String, title: String = "দ্বীনি দাওয়াহ শেয়ার করুন") {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(intent, title))
        } catch (_: Exception) {}
    }

    fun getMyDuroodCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_MY_DUROOD_COUNT, 33)
    }

    fun addMyDuroodCount(context: Context, delta: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_MY_DUROOD_COUNT, 33)
        val newCount = current + delta
        prefs.edit().putInt(KEY_MY_DUROOD_COUNT, newCount).apply()
        return newCount
    }

    fun getUmmahDuroodProgress(context: Context): Pair<Int, Int> {
        val target = 100_000
        val myCount = getMyDuroodCount(context)
        // Realistic dynamic community baseline + user recitations
        val baseCommunity = 64_850 + (myCount * 3)
        val current = minOf(baseCommunity, target)
        return Pair(current, target)
    }

    fun getQuizBestScore(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_QUIZ_BEST_SCORE, 0)
    }

    fun saveQuizScore(context: Context, score: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentBest = prefs.getInt(KEY_QUIZ_BEST_SCORE, 0)
        if (score > currentBest) {
            prefs.edit().putInt(KEY_QUIZ_BEST_SCORE, score).apply()
        }
        prefs.edit().putBoolean(KEY_QUIZ_COMPLETED_TODAY, true).apply()
    }
}
