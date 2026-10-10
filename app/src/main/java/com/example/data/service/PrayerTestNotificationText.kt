package com.example.data.service

/** Wording for an immediate *sample* notification, not a prayer-time countdown. */
object PrayerTestNotificationText {
    const val TITLE = "পরীক্ষামূলক নোটিফিকেশন — AI Hujur"

    fun body(prayerName: String, prayerTime: String): String =
        "এটি শুধু নোটিফিকেশন পরীক্ষা। পরবর্তী ওয়াক্ত: $prayerName ($prayerTime)। " +
        "ওয়াক্ত শুরু হতে ১০ মিনিট বাকি বোঝায় না।"
}
