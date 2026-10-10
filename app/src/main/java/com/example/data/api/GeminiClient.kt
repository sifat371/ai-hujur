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
You are "Scholar AI" (স্কলার এআই / আল-হুজুর), a respectful AI educational assistant (not a human Mufti or qualified Islamic scholar) for Bengali-speaking Muslims.

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
            return@withContext UNAVAILABLE_MESSAGE
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
                .url(BASE_URL)
                .header("x-goog-api-key", apiKey)
                .post(body)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val json = JSONObject(responseBody)
                    val candidates = json.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val content = candidates.getJSONObject(0).optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (text.isNotBlank()) return@withContext text
                        }
                    }
                }
            }
            UNAVAILABLE_MESSAGE
        } catch (e: Exception) {
            UNAVAILABLE_MESSAGE
        }
    }

    suspend fun askScholar(prompt: String): String {
        return askScholarChat(emptyList(), prompt)
    }

    suspend fun generateModeratorSummary(question: String): String = withContext(Dispatchers.IO) {
        val prompt = "নিম্নলিখিত ইসলামিক প্রশ্নের অত্যন্ত সংক্ষিপ্ত, মার্জিত ও নির্ভরযোগ্য সমাধান বাংলাতে প্রদান করুন (২-৩ বাক্য): '$question'"
        askScholar(prompt)
    }

    const val UNAVAILABLE_MESSAGE =
        "Scholar AI এখন উত্তর দিতে পারছে না। পরে আবার চেষ্টা করুন। " +
        "গুরুত্বপূর্ণ ধর্মীয় বিষয়ে যোগ্য আলেমের পরামর্শ নিন।"
}
