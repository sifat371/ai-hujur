package com.example.data.model

/** Small, explicitly curated QA quiz bank. Religious editorial review is still required. */
data class LearningQuestion(
    val id: Int,
    val question: String,
    val choices: List<String>,
    val correctChoice: Int,
    val explanation: String
)

object LearningQuizBank {
    val questions: List<LearningQuestion> = listOf(
        LearningQuestion(1, "পবিত্র কুরআনে মোট কতটি সূরা আছে?",
            listOf("১১২", "১১৪", "১১৬", "১২০"), 1,
            "পবিত্র কুরআনে ১১৪টি সূরা আছে।"),
        LearningQuestion(2, "পবিত্র কুরআনের প্রথম সূরা কোনটি?",
            listOf("আল-বাকারা", "আল-ফাতিহা", "আল-ইখলাস", "আন-নাস"), 1,
            "সূরা আল-ফাতিহা কুরআনের প্রথম সূরা।"),
        LearningQuestion(3, "পবিত্র কুরআনের শেষ সূরা কোনটি?",
            listOf("আন-নাস", "আল-ফালাক", "আল-ইখলাস", "আল-কাওসার"), 0,
            "সূরা আন-নাস কুরআনের ১১৪ নম্বর সূরা।"),
        LearningQuestion(4, "প্রতিদিন ফরজ সালাতের ওয়াক্ত কয়টি?",
            listOf("৩টি", "৪টি", "৫টি", "৬টি"), 2,
            "দিনে পাঁচ ওয়াক্ত ফরজ সালাত রয়েছে।"),
        LearningQuestion(5, "রমজান মাসে কোন ইবাদত ফরজ?",
            listOf("হজ", "রোজা", "কুরবানি", "যাকাতুল ফিতর"), 1,
            "রমজান মাসে যোগ্য মুসলিমদের জন্য রোজা ফরজ।")
    )
}
