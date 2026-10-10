package com.example

import com.example.data.api.GeminiClient
import com.example.data.model.ChatMessage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ScholarAiTest {
    @Test
    fun serviceFailureDoesNotInventReligiousAdvice() {
        assertTrue(GeminiClient.UNAVAILABLE_MESSAGE.contains("এখন উত্তর দিতে পারছে না"))
        assertTrue(GeminiClient.UNAVAILABLE_MESSAGE.contains("যোগ্য আলেম"))
    }
    @Test
    fun missingKeyFailsClearly() = runBlocking {
        if (com.example.BuildConfig.GEMINI_API_KEY == "MY_GEMINI_API_KEY" ||
            com.example.BuildConfig.GEMINI_API_KEY.isBlank()
        ) {
            val response = GeminiClient.askScholarChat(
                listOf(ChatMessage(text = "আসসালামু আলাইকুম", isFromUser = true)),
                "একটি গুরুত্বপূর্ণ মাসআলা জানতে চাই"
            )
            assertEquals(GeminiClient.UNAVAILABLE_MESSAGE, response)
        }
    }
}
