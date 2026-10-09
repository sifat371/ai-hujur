package com.example

import com.example.data.api.GeminiClient
import com.example.data.model.ChatMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScholarAiTest {

    @Test
    fun testRozaOfflineGuidanceReturnsRespectfulBengaliResponseWithQuranReference() {
        val guidance = GeminiClient.getOfflineScholarGuidance("রমজানে অসুস্থ ব্যক্তির রোজা কাজা করার বিধান কী?")
        assertNotNull(guidance)
        assertTrue("Must start with Islamic greeting in Bengali", guidance.contains("আসসালামু আলাইকুম"))
        assertTrue("Must contain Surah Al-Baqarah reference", guidance.contains("আল-বাকারা"))
        assertTrue("Must contain scholarly ruling on kaza", guidance.contains("কাজা"))
        assertTrue("Must end with respectful dua", guidance.contains("আমীন"))
    }

    @Test
    fun testDuaOfflineGuidanceReturnsAuthenticHadithAndArabicPronunciation() {
        val guidance = GeminiClient.getOfflineScholarGuidance("দুশ্চিন্তা ও অশান্তি দূর করার দোয়া কোনটি?")
        assertNotNull(guidance)
        assertTrue("Must contain greeting", guidance.contains("আসসালামু আলাইকুম"))
        assertTrue("Must contain Arabic Dua or Hadith reference", guidance.contains("اللَّهُمَّ") || guidance.contains("আল্লাহুম্মা"))
        assertTrue("Must contain Bukhari reference", guidance.contains("বুখারী"))
    }

    @Test
    fun testZakatOfflineGuidanceReturnsNisabRules() {
        val guidance = GeminiClient.getOfflineScholarGuidance("সোনা ও টাকার যাকাতের নেসাব কত?")
        assertNotNull(guidance)
        assertTrue("Must mention percentage 2.5%", guidance.contains("২.৫%"))
        assertTrue("Must mention Nisab rules", guidance.contains("নেসাব"))
    }

    @Test
    fun testAskScholarChatWithHistoryExecutesGracefully() = runBlocking {
        val history = listOf(
            ChatMessage(text = "আসসালামু আলাইকুম", isFromUser = true),
            ChatMessage(text = "ওয়ালাইকুমুস সালাম ওয়া রাহমাতুল্লাহ", isFromUser = false)
        )
        val response = GeminiClient.askScholarChat(history, "তাহাজ্জুদ নামাজের সময় কখন?")
        assertNotNull(response)
        assertTrue("Should return non-empty scholarly response", response.isNotBlank())
        assertTrue("Response should contain Bengali characters", response.any { it in '\u0980'..'\u09FF' })
    }
}
