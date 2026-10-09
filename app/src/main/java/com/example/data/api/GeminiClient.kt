package com.example.data.api

import com.example.BuildConfig
import com.example.data.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SCHOLAR_SYSTEM_INSTRUCTION = """
You are "Scholar AI" (স্কলার এআই / আল-হুজুর), a dignified, deeply knowledgeable, compassionate, and authoritative Islamic scholar, Mufti, and spiritual mentor for Bengali-speaking Muslims.

Your primary mission is to provide authentic, respectful, and scholarly guidance in refined, fluent, and polite Bengali (বাংলা) for questions on Islamic theology, Salah, Ramadan, Fasting, Zakat, Quranic interpretation (Tafsir), Hadith, Dua, and daily Islamic ethics.

Core Guidelines for Tone & Demeanor:
1. Tone & Manner:
   - Extremely respectful, humble, compassionate, and dignified (শ্রদ্ধাশীল, মার্জিত, নম্র ও আন্তরিক).
   - Address the user courteously with respect: "সম্মানিত দ্বীনি ভাই/বোন" or "শ্রদ্ধেয় প্রশ্নকারী".
   - Open your answer with: "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু" and "বিসমিল্লাহির রাহমানির রাহীম".

2. Authentic Scholarly Sources:
   - Ground all answers strictly in the Holy Quran (কুরআনুল কারীম) and authentic Prophetic Sunnah (সহীহ বুখারী, সহীহ মুসলিম, সুনানে আবু দাউদ, জামে তিরমিযী ইত্যাদি).
   - Whenever mentioning a Quranic verse, cite the Surah name and Ayat number (যেমন: সূরা আল-বাকারা, আয়াত: ১৮৩).
   - Whenever citing Hadith, mention the book and reference clearly.
   - Respect mainstream Islamic consensus (জমহুর ও হানাফী ফিকহের অনুসরণে, যা বাংলাদেশে সর্বাধিক অনুসৃত)।

3. Dual Arabic & Bengali Formatting:
   - For all relevant Quranic verses, prophetic Duas, and Islamic terms, provide the original Arabic text followed by accurate Bengali pronunciation (উচ্চারণ) and Bengali meaning (অর্থ).

4. Humility & Real-World Arbitration:
   - For complex disputes (e.g. intricate inheritance, divorce, personal conflict), state the standard general Shariah principles and courteously recommend consulting trustworthy local Islamic scholars or recognized Darul Ifta in Bangladesh.

5. Concluding Dua:
   - Conclude every response with a warm, sincere dua for the asker (e.g., "আল্লাহ তায়ালা আপনাকে ও আপনার পরিবারকে নেক আমলের তাওফিক দান করুন এবং উভয় জগতে উত্তম প্রতিদান প্রদান করুন। আমীন।").

6. Readability:
   - Use clear bullet points, elegant spacing, and easily scannable sections for mobile readability.
"""

    /**
     * Sends a multi-turn chat request to Gemini API incorporating prior conversation context.
     */
    suspend fun askScholarChat(
        history: List<ChatMessage>,
        newPrompt: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineScholarGuidance(newPrompt)
        }

        try {
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray()

                // Include the last 8 conversation turns for contextual multi-turn dialogue
                val recentHistory = history.takeLast(8)
                for (msg in recentHistory) {
                    val role = if (msg.isFromUser) "user" else "model"
                    val turnObj = JSONObject().apply {
                        put("role", role)
                        val partsArr = JSONArray().apply {
                            put(JSONObject().apply { put("text", msg.text) })
                        }
                        put("parts", partsArr)
                    }
                    contentsArray.put(turnObj)
                }

                // Add the current new prompt as user turn
                val currentTurn = JSONObject().apply {
                    put("role", "user")
                    val partsArr = JSONArray().apply {
                        put(JSONObject().apply { put("text", newPrompt) })
                    }
                    put("parts", partsArr)
                }
                contentsArray.put(currentTurn)
                put("contents", contentsArray)

                // System Instruction specifying Scholar persona & Bengali tone
                val systemInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", SCHOLAR_SYSTEM_INSTRUCTION) })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemInstructionObj)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = requestJson.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val json = JSONObject(responseBody)
                val candidates = json.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val content = candidates.getJSONObject(0).optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        val text = parts.getJSONObject(0).optString("text")
                        if (text.isNotBlank()) {
                            return@withContext text
                        }
                    }
                }
            }
            getOfflineScholarGuidance(newPrompt)
        } catch (e: Exception) {
            getOfflineScholarGuidance(newPrompt)
        }
    }

    suspend fun askScholar(prompt: String): String {
        return askScholarChat(emptyList(), prompt)
    }

    suspend fun generateModeratorSummary(question: String): String = withContext(Dispatchers.IO) {
        val prompt = "নিম্নলিখিত ইসলামিক প্রশ্নের অত্যন্ত সংক্ষিপ্ত, মার্জিত ও নির্ভরযোগ্য সমাধান বাংলাতে প্রদান করুন (২-৩ বাক্য): '$question'"
        askScholar(prompt)
    }

    fun getOfflineScholarGuidance(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("fast") || lower.contains("ramadan") || lower.contains("roza") || lower.contains("রোজা") || lower.contains("রমজান") || lower.contains("কাজা") || lower.contains("সেহরি") || lower.contains("ইফতার") -> {
                "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\n" +
                "বিসমিল্লাহির রাহমানির রাহীম।\n\n" +
                "সম্মানিত দ্বীনি ভাই/বোন, আপনার প্রশ্নের জন্য ধন্যবাদ। মাহে রমজানের রোজা ইসলামের অন্যতম মৌলিক ও ফরজ বিধান।\n\n" +
                "📖 পবিত্র কুরআনের ইরশাদ:\n" +
                "«يَا أَيُّهَا الَّذِينَ آمَنُوا كُتِبَ عَلَيْكُمُ الصِّيَامُ كَمَا كُتِبَ عَلَى الَّذِينَ مِن قَبْلِكُمْ لَعَلَّكُمْ تَتَّقُونَ»\n" +
                "অর্থ: 'হে ঈমানদারগণ! তোমাদের ওপর সিয়াম ফরজ করা হয়েছে, যেমন ফরজ করা হয়েছিল তোমাদের পূর্ববর্তীদের ওপর, যাতে তোমরা তাকওয়া অর্জন করতে পার।' (সূরা আল-বাকারা, ২:১৮৩)\n\n" +
                "📜 শরঈ দিকনির্দেশনা ও বিধান:\n" +
                "• অসুস্থতা বা দীর্ঘ সফরের কারণে রোজা ভঙ্গ হলে তা পরবর্তীতে কাজা (একটি রোজার বদলে একটি) আদায় করে নেওয়া ওয়াজিব।\n" +
                "• স্থায়ী অসুস্থতা বা বার্ধক্যের কারণে রোজা রাখতে অপারগ হলে প্রতিটি রোজার জন্য একজন অভাবী ব্যক্তিকে দুবেলা পেটভরে খাওয়ানো (ফিদয়া) প্রদান করতে হয়।\n" +
                "• ইচ্ছাকৃতভাবে কোনো শারঈ ওজর ছাড়া রোজা ভাঙা মারাত্মক কবিরা গোনাহ। এতে কাজা এবং কাফফারা (ধারাবাহিক ৬০টি রোজা রাখা বা ৬০ জন মিসকিনকে আহার করানো) উভয়ই আবশ্যক হয়।\n" +
                "• সুবহে সাদিকের পূর্বেই মনে মনে সংকল্প করাই রোজার মূল নিয়ত।\n\n" +
                "আল্লাহ তায়ালা আপনার প্রতিটি আমল কবুল করুন এবং তাকওয়া অর্জনের তাওফিক দিন। আমীন।"
            }
            lower.contains("dua") || lower.contains("prayer") || lower.contains("দোয়া") || lower.contains("অশান্তি") || lower.contains("দুশ্চিন্তা") || lower.contains("পেরেশানি") || lower.contains("মন খারাপ") -> {
                "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\n" +
                "বিসমিল্লাহির রাহমানির রাহীম।\n\n" +
                "সম্মানিত প্রশ্নকারী ভাই/বোন, আল্লাহ তায়ালা আপনার অন্তরকে প্রশান্ত করুন। প্রিয় নবীজি ﷺ বলেছেন:\n" +
                "«الدُّعَاءُ هُوَ الْعِبَادَةُ» — 'দোয়াই হলো ইবাদতের মূল।' (জামে তিরমিযী: ৩৩৭২)\n\n" +
                "🤲 দুশ্চিন্তা ও পেরেশানি মুক্তির শ্রেষ্ঠ সুন্নতি দোয়া:\n" +
                "«اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَالْعَجْزِ وَالْكَسَلِ، وَالْجُبْنِ وَالْبُخْلِ، وَضَلَعِ الدَّيْنِ وَغَلَبَةِ الرِّجَالِ»\n\n" +
                "উচ্চারণ: 'আল্লাহুম্মা ইন্নী আ'ঊজু বিকা মিনাল হাম্মি ওয়াল হাযানি, ওয়াল 'আজযি ওয়াল কাসালি, ওয়াল জুবনি ওয়াল বুখলি, ওয়া দ্বালাইদ দাইনি ওয়া গালাবাতির রিজাল।'\n\n" +
                "অর্থ: 'হে আল্লাহ! আমি আপনার নিকট আশ্রয় প্রার্থনা করছি দুশ্চিন্তা ও দুঃখ-বেদনা থেকে, অপারগতা ও অলসতা থেকে, ভীরুতা ও কৃপণতা থেকে এবং ঋণের বোঝা ও মানুষের অন্যায় চাপ থেকে।' (সহীহ বুখারী: ২৮৯৩)\n\n" +
                "💡 আমল:\n" +
                "১. বেশি বেশি ইস্তিগফার পাঠ করুন (আস্তাগফিরুল্লাহিল 'আযীম)।\n" +
                "২. ইউনুস (আ.)-এর দোয়া পাঠ করুন: «لَا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ»\n" +
                "৩. মনে রাখবেন, «أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ» — 'জেনে রেখো, আল্লাহর জিকির দ্বারাই অন্তরসমূহ শান্তি লাভ করে।' (সূরা আর-রাদ: ২৮)\n\n" +
                "আল্লাহ আপনাকে সর্বাবস্থায় মানসিক স্বস্তি ও শান্তি দান করুন। আমীন।"
            }
            lower.contains("tahajjud") || lower.contains("তাহাজ্জুদ") || lower.contains("কিয়ামুল লাইল") || lower.contains("শব") || lower.contains("কদর") -> {
                "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\n" +
                "বিসমিল্লাহির রাহমানির রাহীম।\n\n" +
                "শ্রদ্ধেয় দ্বীনি ভাই/বোন, রাতের নির্জনতায় তাহাজ্জুদের নামাজ মুমিনের আত্মিক উন্নতি ও আল্লাহর নৈকট্য লাভের শ্রেষ্ঠ মাধ্যম।\n\n" +
                "📖 হাদিসের মহিমান্বিত সুসংবাদ:\n" +
                "রাসূলুল্লাহ ﷺ ইরশাদ করেছেন:\n" +
                "«أَفْضَلُ الصَّلَاةِ بَعْدَ الصَّلَاةِ الْمَكْتُوبَةِ الصَّلَاةُ فِي جَوْفِ اللَّيْلِ»\n" +
                "অর্থ: 'ফরজ নামাজের পর সর্বোত্তম নামাজ হলো রাতের নামাজ (তাহাজ্জুদ)।' (সহীহ মুসলিম: ১১৬৩)\n\n" +
                "📜 তাহাজ্জুদের নিয়ম ও রীতিনীতি:\n" +
                "• ওয়াক্ত: ইশার নামাজের পর থেকেই শেষ রাত পর্যন্ত পড়া যায়, তবে রাতের শেষ তৃতীয়াংশে (সেহরির পূর্বে) পড়া সর্বোত্তম।\n" +
                "• রাকাত সংখ্যা: ২ রাকাত করে যত রাকাত ইচ্ছা (সাধারণত ৪, ৮ বা ১২ রাকাত) পড়া যায়। প্রিয় নবীজি ﷺ অধিকাংশ সময় বিতরসহ ১১ রাকাত পড়তেন।\n" +
                "• শেষ রাতে আল্লাহ তায়ালা প্রথম আকাশে অবতরণ করে ঘোষণা দেন: 'কে আছো ক্ষমা প্রার্থনাকারী, আমি তাকে ক্ষমা করব? কে আছো আহ্বানকারী, আমি তার ডাকে সাড়া দেব?' (সহীহ বুখারী: ১১৪৫)\n\n" +
                "শবে কদরে বেশি বেশি এই দোয়াটি পাঠ করুন:\n" +
                "«اللَّهُمَّ إِنَّكَ عَفُوٌّ تُحِبُّ الْعَفْوَ فَاعْفُ عَنِّي»\n" +
                "উচ্চারণ: 'আল্লাহুম্মা ইন্নাকা 'আফুওবুন তুহিব্বুল 'আফওয়া ফা'ফু 'আন্নী।'\n\n" +
                "আল্লাহ আপনাকে তাহাজ্জুদের নিয়মিত অভ্যাস গড়ার তাওফিক দিন। আমীন।"
            }
            lower.contains("namaz") || lower.contains("নামাজ") || lower.contains("সালাত") || lower.contains("মনোযোগ") || lower.contains("খুশু") -> {
                "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\n" +
                "বিসমিল্লাহির রাহমানির রাহীম।\n\n" +
                "সম্মানিত প্রশ্নকারী, নামাজে মনোযোগ (খুশু-খুজু) রক্ষা করা মুমিনের কামিয়াবির প্রধান চাবিকাঠি। আল্লাহ তায়ালা বলেন:\n" +
                "«قَدْ أَفْلَحَ الْمُؤْمِنُونَ الَّذِينَ هُمْ فِي صَلَاتِهِمْ خَاشِعُونَ»\n" +
                "অর্থ: 'নিশ্চয়ই মুমিনগণ সফলকাম হয়েছে, যারা তাদের নামাজে বিনম্র ও একাগ্র।' (সূরা আল-মুমিনুন: ১-২)\n\n" +
                "🌿 নামাজে একাগ্রতা বৃদ্ধির ৫টি কার্যকর সুন্নতি উপায়:\n" +
                "১. নামাজের পূর্বে ধীরস্থিরে উত্তমরূপে সুন্নত তরিকায় অজু সম্পন্ন করুন।\n" +
                "২. তাকবীরে তাহরীমা বাঁধার সময় অন্তরে ভাবুন—আপনি নিখিল জাহানের মহান স্রষ্টা আল্লাহ সুবহানাহু ওয়া তায়ালার সামনে দাঁড়িয়ে আছেন।\n" +
                "৩. পঠিত সূরা, কিরাত ও তাসবীহগুলোর অর্থ অনুধাবন করার চেষ্টা করুন।\n" +
                "৪. সিজদার স্থানে দৃষ্টি নিবদ্ধ রাখুন এবং কোনো তাড়াহুড়া না করে প্রতিটি রুকন ধীরস্থিরে আদায় করুন।\n" +
                "৫. ভাবুন এটিই হয়তো আপনার জীবনের শেষ নামাজ।\n\n" +
                "আল্লাহ তায়ালা আমাদের সকলকে বিনম্র ও জীবন্ত নামাজ আদায়ের তাওফিক দিন। আমীন।"
            }
            lower.contains("charity") || lower.contains("zakat") || lower.contains("যাকাত") || lower.contains("সদকা") || lower.contains("ফিতরা") || lower.contains("দান") -> {
                "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\n" +
                "বিসমিল্লাহির রাহমানির রাহীম।\n\n" +
                "সম্মানিত দ্বীনি ভাই/বোন, যাকাত ও সদকা ইসলামে অর্থনৈতিক ইনসাফ ও আত্মশুদ্ধির মহান ইবাদত।\n\n" +
                "📖 শরঈ নীতিমালা ও নেসাব:\n" +
                "• স্বর্ণের নেসাব: ৭.৫ তোলা (৮৭.৪৮ গ্রাম) খাঁটি স্বর্ণ।\n" +
                "• রূপার নেসাব: ৫২.৫ তোলা (৬১২.৩৬ গ্রাম) রূপা বা এর সমমূল্যের উদ্বৃত্ত নগদ অর্থ/ব্যবসায়িক পণ্য।\n" +
                "• নেসাব পরিমাণ সম্পদের ওপর পূর্ণ এক চন্দ্রবছর অতিক্রান্ত হলে শতকরা আড়াই শতাংশ (২.৫%) বা চল্লিশ ভাগের এক ভাগ হকদারদের কাছে পৌঁছানো ফরজ।\n" +
                "• সদকাতুল ফিতর রমজানের শেষে ঈদের নামাজের পূর্বেই আদায় করা আবশ্যক, যাতে সমাজের দরিদ্র মানুষেরাও ঈদের আনন্দ উদযাপন করতে পারে।\n\n" +
                "পবিত্র কুরআনে আল্লাহ বলেন:\n" +
                "«خُذْ مِنْ أَمْوَالِهِمْ صَدَقَةً تُطَهِّرُهُمْ وَتُزَكِّيهِم بِهَا»\n" +
                "অর্থ: 'তাদের ধন-সম্পদ থেকে সদকা গ্রহণ করুন, যা দ্বারা আপনি তাদের পবিত্র করবেন ও পরিশুদ্ধ করবেন।' (সূরা আত-তাওবাহ: ১০৩)\n\n" +
                "আল্লাহ আপনার সম্পদে বরকত দান করুন এবং তা দ্বীনের পথে ব্যয়ের তাওফিক দিন। আমীন।"
            }
            else -> {
                "আসসালামু আলাইকুম ওয়া রাহমাতুল্লাহি ওয়া বারাকাতুহু।\n" +
                "বিসমিল্লাহির রাহমানির রাহীম।\n\n" +
                "সম্মানিত দ্বীনি ভাই/বোন, 'Scholar AI'-তে আপনাকে স্বাগতম। বিশুদ্ধ নিয়তে দ্বীনের ইলম অন্বেষণ জান্নাতের পথ সুগম করে। প্রিয় নবীজি ﷺ ইরশাদ করেছেন:\n\n" +
                "«مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ بِهِ طَرِيقًا إِلَى الْجَنَّةِ»\n" +
                "অর্থ: 'যে ব্যক্তি জ্ঞান অন্বেষণের পথে বের হয়, আল্লাহ তায়ালা তার জন্য জান্নাতের পথকে সহজ করে দেন।' (সহীহ মুসলিম: ২৬৯৯)\n\n" +
                "আপনার যে কোনো ইসলামিক মাসআলা, নামাজ, রোজা, যাকাত, কুরআন-হাদিসের তাফসির বা পারিবারিক বিধান সংক্রান্ত প্রশ্ন বিস্তারিতভাবে বাংলায় জিজ্ঞাসা করতে পারেন।\n\n" +
                "আল্লাহ সুবহানাহু ওয়া তায়ালা আমাদের সকলকে দ্বীনের সঠিক বুঝ দান করুন এবং হিদায়াতের ওপর প্রতিষ্ঠিত রাখুন। আমীন।"
            }
        }
    }
}

