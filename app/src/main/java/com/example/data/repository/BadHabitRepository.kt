package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.BadHabit
import com.example.data.model.WaswasahEmergencyStep
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BadHabitRepository {

    private const val PREFS_NAME = "tazkiyah_bad_habits_prefs"
    private const val KEY_HABITS_DATA = "saved_habits_data"

    fun getDefaultHabits(): List<BadHabit> {
        return listOf(
            BadHabit(
                id = "habit_lower_gaze",
                titleBn = "কুদৃষ্টি ও অশ্লীল কন্টেন্ট পরিহার",
                category = "দৃষ্টির সংযম",
                iconCategory = "visibility",
                streakDays = 3,
                lastCleanDate = "",
                totalCleanDays = 7,
                reasonToQuit = "চোখের গুনাহ অন্তরের নূর কেড়ে নেয় এবং ইবাদতের মিষ্টতা নষ্ট করে।",
                spiritualRemedy = "মোবাইল বা স্ক্রিন সাথে সাথে বন্ধ করে ৩ বার 'আস্তাগফিরুল্লাহ' পাঠ করুন এবং অজু করে অন্য কক্ষে যান।",
                quranAyahOrHadith = "মুমিনদের বলুন, তারা যেন তাদের দৃষ্টিকে সংযত রাখে এবং তাদের লজ্জাস্থানের হেফাজত করে।",
                reference = "সূরা আন-নূর: ৩০"
            ),
            BadHabit(
                id = "habit_anger",
                titleBn = "অতিরিক্ত রাগ ও কটু ভাষা দমন",
                category = "জিহ্বার হেফাজত",
                iconCategory = "sentiment_very_dissatisfied",
                streakDays = 5,
                lastCleanDate = "",
                totalCleanDays = 12,
                reasonToQuit = "রাগ শয়তানের বিষবাণ; এটি মানুষের আমল ও সম্পর্ক মুহূর্তে ধ্বংস করে দেয়।",
                spiritualRemedy = "দাঁড়িয়ে থাকলে বসে পড়ুন, বসে থাকলে শুয়ে পড়ুন এবং মুখে ঠাণ্ডা পানির ঝাপটা দিয়ে অজু করুন।",
                quranAyahOrHadith = "প্রকৃত বীর সে নয় যে কুস্তিতে কাউকে পরাস্ত করে, বরং সে যে রাগের সময় নিজেকে নিয়ন্ত্রণ করে।",
                reference = "সহীহ বুখারী ও মুসলিম"
            ),
            BadHabit(
                id = "habit_lying_backbiting",
                titleBn = "মিথ্যা বলা ও গীবত/পরনিন্দা ত্যাগ",
                category = "জিহ্বার হেফাজত",
                iconCategory = "record_voice_over",
                streakDays = 4,
                lastCleanDate = "",
                totalCleanDays = 10,
                reasonToQuit = "গীবত ও মিথ্যা নিজের নেক আমল নষ্ট করে যার গীবত করা হয়েছে তাকে সপে দেয়।",
                spiritualRemedy = "কারো অনুপস্থিতিতে খারাপ বলতে ইচ্ছা হলে সাথে সাথে তার জন্য আল্লাহর কাছে মাগফিরাত প্রার্থনা করুন।",
                quranAyahOrHadith = "তোমাদের কেউ যেন অন্যের গীবত না করে; সে কি তার মৃত ভাইয়ের গোশত খাওয়া পছন্দ করবে?",
                reference = "সূরা আল-হুজুরাত: ১২"
            ),
            BadHabit(
                id = "habit_salah_delay",
                titleBn = "নামাজে অলসতা ও সময় পার করা বর্জন",
                category = "আমল ও ইবাদত",
                iconCategory = "alarm",
                streakDays = 6,
                lastCleanDate = "",
                totalCleanDays = 15,
                reasonToQuit = "নামাজ ইচ্ছাকৃত কাজা করা সবচেয়ে ভয়াবহ আত্মঘাতী গুনাহগুলোর একটি।",
                spiritualRemedy = "আজান শোনা মাত্রই সকল কাজ ৫ মিনিটের জন্য স্থগিত রেখে অজুর উদ্দেশ্যে উঠে পড়ুন।",
                quranAyahOrHadith = "কেয়ামতের দিন সর্বপ্রথম বান্দার সালাতের হিসাব নেওয়া হবে; সালাত ঠিক থাকলে বাকি সবই ঠিক থাকবে।",
                reference = "সুনান আত-তিরমিযী"
            ),
            BadHabit(
                id = "habit_smoking_addiction",
                titleBn = "ধূমপান ও ক্ষতিকর আসক্তি ত্যাগ",
                category = "দেহের পবিত্রতা",
                iconCategory = "smoke_free",
                streakDays = 2,
                lastCleanDate = "",
                totalCleanDays = 6,
                reasonToQuit = "আল্লাহর দেওয়া পবিত্র শরীরকে বিষাক্ত ধোঁয়ায় বিষিয়ে তোলা অপব্যয় ও আত্মহননের শামিল।",
                spiritualRemedy = "নেশার তীব্র তাগিদ উঠলে মুখে লবঙ্গ বা পানি নিন, গভীর শ্বাস নিন এবং 'লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ' পাঠ করুন।",
                quranAyahOrHadith = "তোমরা নিজেদের হাতে নিজেদের ধ্বংসের মুখে নিক্ষেপ করো না।",
                reference = "সূরা আল-বাকারা: ১৯৫"
            ),
            BadHabit(
                id = "habit_time_wasting",
                titleBn = "অপ্রয়োজনীয় ফোন স্ক্রলিং ও সময় অপচয়",
                category = "সময়ের মূল্য",
                iconCategory = "hourglass_empty",
                streakDays = 3,
                lastCleanDate = "",
                totalCleanDays = 8,
                reasonToQuit = "জীবনের প্রতিটি সেকেন্ড অত্যন্ত দামি যা একবার চলে গেলে আর কখনোই ফেরত আসবে না।",
                spiritualRemedy = "সোশ্যাল মিডিয়া ব্যবহারের পূর্বে নির্দিষ্ট নিয়ত ও কাজ ঠিক করুন এবং দৈনিক ৩০ মিনিটের বেশি নয়।",
                quranAyahOrHadith = "দুটি নিয়ামতের ব্যাপারে অধিকাংশ মানুষ ক্ষতিগ্রস্ত ও প্রতারিত—স্বাস্থ্য এবং অবসর সময়।",
                reference = "সহীহ বুখারী"
            )
        )
    }

    fun getEmergencySteps(): List<WaswasahEmergencyStep> {
        return listOf(
            WaswasahEmergencyStep(
                stepNumber = 1,
                titleBn = "আউযুবিল্লাহ পাঠ ও থুথু নিক্ষেপ",
                actionBn = "৩ বার পাঠ করুন: 'আউযুবিল্লাহি মিনাশ শাইতানির রাজিম' এবং বাঁ দিকে ৩ বার আলতোভাবে থুতু ফেলার ভঙ্গি করুন।",
                hadithProofBn = "রাসূলুল্লাহ (সা.) নির্দেশ দিয়েছেন শয়তানের কুমন্ত্রণা এলেই আল্লাহর আশ্রয় চাইতে। (সহীহ মুসলিম)",
                iconType = "shield"
            ),
            WaswasahEmergencyStep(
                stepNumber = 2,
                titleBn = "তাত্ক্ষণিক অজু করুন",
                actionBn = "রাগ বা খারাপ বাসনা জাগ্রত হলেই পানির কাছে যান এবং শীতল পানি দিয়ে সুন্নত মোতাবেক অজু সম্পন্ন করুন।",
                hadithProofBn = "শয়তান আগুনের তৈরি, আর আগুন পানি দিয়ে নেভানো হয়। তাই তোমাদের কেউ উত্তেজিত হলে অজু করুক। (আবু দাউদ)",
                iconType = "water_drop"
            ),
            WaswasahEmergencyStep(
                stepNumber = 3,
                titleBn = "স্থান ত্যাগ ও দৃষ্টি পরিবর্তন",
                actionBn = "বর্তমান রুম বা বিছানা ত্যাগ করুন, মোবাইল টেবিলের উপর উল্টো করে রেখে হাঁটাচলা করুন বা পরিবারের কাছে যান।",
                hadithProofBn = "একা থাকা অবস্থায় শয়তানের কুমন্ত্রণা তীব্র হয়। জামাত ও সঙ্গ শয়তানকে তাড়িয়ে দেয়।",
                iconType = "directions_walk"
            ),
            WaswasahEmergencyStep(
                stepNumber = 4,
                titleBn = "২ রাকাত সালাতুত তাওবা বা সাইয়্যিদুল ইস্তিগফার",
                actionBn = "বিনীতভাবে জায়নামাজে দাঁড়িয়ে ২ রাকাত তাওবার সালাত আদায় করুন অথবা সাইয়্যিদুল ইস্তিগফার পাঠ করুন।",
                hadithProofBn = "যে ব্যক্তি গুনাহ করার পর পবিত্র হয়ে দুই রাকাত সালাত আদায় করে ক্ষমা চায়, আল্লাহ তাকে ক্ষমা করেন। (তিরমিযী)",
                iconType = "mosque"
            )
        )
    }

    fun loadHabits(context: Context): List<BadHabit> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedData = prefs.getString(KEY_HABITS_DATA, null)
        if (savedData.isNullOrBlank()) {
            val defaults = getDefaultHabits()
            saveHabits(context, defaults)
            return defaults
        }

        return try {
            parseHabitsString(savedData)
        } catch (e: Exception) {
            getDefaultHabits()
        }
    }

    fun saveHabits(context: Context, habits: List<BadHabit>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val serialized = serializeHabits(habits)
        prefs.edit().putString(KEY_HABITS_DATA, serialized).apply()
    }

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return sdf.format(Date())
    }

    private fun serializeHabits(habits: List<BadHabit>): String {
        val sb = StringBuilder()
        for (h in habits) {
            val line = listOf(
                escape(h.id),
                escape(h.titleBn),
                escape(h.category),
                escape(h.iconCategory),
                h.streakDays.toString(),
                escape(h.lastCleanDate),
                h.totalCleanDays.toString(),
                escape(h.reasonToQuit),
                escape(h.spiritualRemedy),
                escape(h.quranAyahOrHadith),
                escape(h.reference),
                h.isTracking.toString()
            ).joinToString("|||")
            sb.append(line).append("###")
        }
        return sb.toString()
    }

    private fun parseHabitsString(data: String): List<BadHabit> {
        val result = mutableListOf<BadHabit>()
        val records = data.split("###").filter { it.isNotBlank() }
        for (rec in records) {
            val parts = rec.split("|||")
            if (parts.size >= 12) {
                result.add(
                    BadHabit(
                        id = unescape(parts[0]),
                        titleBn = unescape(parts[1]),
                        category = unescape(parts[2]),
                        iconCategory = unescape(parts[3]),
                        streakDays = parts[4].toIntOrNull() ?: 0,
                        lastCleanDate = unescape(parts[5]),
                        totalCleanDays = parts[6].toIntOrNull() ?: 0,
                        reasonToQuit = unescape(parts[7]),
                        spiritualRemedy = unescape(parts[8]),
                        quranAyahOrHadith = unescape(parts[9]),
                        reference = unescape(parts[10]),
                        isTracking = parts[11].toBooleanStrictOrNull() ?: true
                    )
                )
            }
        }
        return if (result.isEmpty()) getDefaultHabits() else result
    }

    private fun escape(text: String): String {
        return text.replace("|||", " ").replace("###", " ")
    }

    private fun unescape(text: String): String {
        return text
    }
}
