package com.example.data.model

data class QuizQuestion(
    val id: String,
    val questionBn: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationBn: String,
    val referenceBn: String,
    val category: String
)

data class DawahCardTemplate(
    val id: String,
    val titleBn: String,
    val arabicCalligraphy: String,
    val banglaTranslation: String,
    val referenceBn: String,
    val category: String,
    val reflectionBn: String
)

data class FajrReminderTemplate(
    val id: String,
    val titleBn: String,
    val messageText: String,
    val hadithProof: String
)
