package com.example

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.UserSettingsStore
import com.example.data.service.BootCompletedReceiver
import com.example.data.service.LocationService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class UserSettingsStoreTest {
    private lateinit var context: Context

    @Before
    fun resetSettings() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("al_hujur_user_settings", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun savedSettingsSurviveStoreRecreation() {
        val first = UserSettingsStore(context)
        first.prayerNotificationsEnabled = false
        first.aiDailyRemindersEnabled = false
        first.location = LocationService.RAJSHAHI
        first.userName = "রহিম"
        first.spiritualGoal = "নিয়মিত আমল"
        first.hijriOffsetDays = 1
        val restored = UserSettingsStore(context)
        assertFalse(restored.prayerNotificationsEnabled)
        assertFalse(restored.aiDailyRemindersEnabled)
        assertEquals(LocationService.RAJSHAHI.latitude, restored.location.latitude, 0.000001)
        assertEquals(LocationService.RAJSHAHI.longitude, restored.location.longitude, 0.000001)
        assertEquals("রহিম", restored.userName)
        assertEquals("নিয়মিত আমল", restored.spiritualGoal)
        assertEquals(1, restored.hijriOffsetDays)
    }

    @Test
    fun invalidCoordinatesNeverReplaceSafeDefault() {
        context.getSharedPreferences("al_hujur_user_settings", Context.MODE_PRIVATE)
            .edit().putString("latitude", "9999").putString("longitude", "NaN").commit()
        assertEquals(LocationService.DHAKA, UserSettingsStore(context).location)
    }

    @Test
    fun rebootWithRemindersDisabledDoesNotEnableThemAgain() {
        UserSettingsStore(context).prayerNotificationsEnabled = false
        BootCompletedReceiver().onReceive(context, Intent(Intent.ACTION_BOOT_COMPLETED))
        assertFalse(UserSettingsStore(context).prayerNotificationsEnabled)
    }

    @Test
    fun defaultsAreExplicitAndHijriOffsetIsLimited() {
        val settings = UserSettingsStore(context)
        assertTrue(settings.prayerNotificationsEnabled)
        assertEquals("ব্যবহারকারী", settings.userName)
        settings.hijriOffsetDays = 100
        assertEquals(2, UserSettingsStore(context).hijriOffsetDays)
    }
}
