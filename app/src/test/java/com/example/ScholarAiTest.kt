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
    fun testServiceUnavailableMessageNeverPretendsToBeAFatwa() {
        assertTrue(GeminiClient.UNAVAILABLE_MESSAGE.contains("পারছে না"))
        assertTrue(GeminiClient.UNAVAILABLE_MESSAGE.contains("যোগ্য আলেম"))
    }

    @Test
    fun testMissingKeyCannotGenerateFakeReligiousAdvice() = runBlocking {
        if (BuildConfig.GEMINI_API_KEY == "MY_GEMINI_API_KEY" || BuildConfig.GEMINI_API_KEY.isBlank()) {
            val response = GeminiClient.askScholarChat(listOf(
                ChatMessage(text = "আসসালামু আলাইকুম", isFromUser = true)
            ), "দলিল জানতে চাই")
            assertEquals(GeminiClient.UNAVAILABLE_MESSAGE, response)
        }
    }
}
