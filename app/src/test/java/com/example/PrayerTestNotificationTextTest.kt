package com.example

import com.example.data.service.PrayerTestNotificationText
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerTestNotificationTextTest {
    @Test fun testAlertIsExplicitlySampleAndNotAnActualTenMinuteWarning() {
        val title = PrayerTestNotificationText.TITLE
        val body = PrayerTestNotificationText.body("ফজর (আগামীকাল)", "০৪:৩৯ পূর্বাহ্ন")
        assertTrue(title.contains("পরীক্ষামূলক"))
        assertTrue(body.contains("এটি শুধু নোটিফিকেশন পরীক্ষা"))
        assertTrue(body.contains("ফজর (আগামীকাল)"))
        assertTrue(body.contains("০৪:৩৯"))
        assertFalse(body.contains("আসন্ন ফজর ওয়াক্ত শুরু হতে ১০ মিনিট বাকি"))
    }
}
