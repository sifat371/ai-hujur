package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.UserSettingsStore
import com.example.data.service.LocationService
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class UserSettingsStoreTest {
    private lateinit var context: Context

    @Before fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("al_hujur_user_settings", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test fun userMustOptInToPrayerNotifications() {
        assertFalse(UserSettingsStore(context).prayerNotificationsEnabled)
    }

    @Test fun settingsSurviveRecreation() {
        val first = UserSettingsStore(context)
        first.location = LocationService.RAJSHAHI
        first.prayerNotificationsEnabled = true
        first.userName = "রহিম"
        first.hijriOffsetDays = 2
        val second = UserSettingsStore(context)
        assertEquals(LocationService.RAJSHAHI.latitude, second.location.latitude, 0.0001)
        assertEquals(LocationService.RAJSHAHI.longitude, second.location.longitude, 0.0001)
        assertTrue(second.prayerNotificationsEnabled)
        assertEquals("রহিম", second.userName)
        assertEquals(2, second.hijriOffsetDays)
    }
}
