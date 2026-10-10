package com.example

import com.example.data.model.LearningQuizBank
import org.junit.Assert.*
import org.junit.Test

class LearningQuizBankTest {
    @Test fun uniqueQuestionsWithValidAnswers() {
        val items = LearningQuizBank.questions
        assertEquals(5, items.size)
        assertEquals(items.size, items.map { it.id }.distinct().size)
        items.forEach { question ->
            assertTrue(question.question.isNotBlank())
            assertEquals(4, question.choices.size)
            assertEquals(4, question.choices.distinct().size)
            assertTrue(question.correctChoice in question.choices.indices)
            assertTrue(question.explanation.isNotBlank())
        }
    }
}
