package com.example.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class UserLocationInfo(
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val isAutoDetected: Boolean
)

object LocationService {

    // Default Islamic Bangladesh presets
    val DHAKA = UserLocationInfo(23.8103, 90.4125, "ঢাকা", false)
    val CHITTAGONG = UserLocationInfo(22.3569, 91.7832, "চট্টগ্রাম", false)
    val SYLHET = UserLocationInfo(24.8949, 91.8687, "সিলেট", false)
    val RAJSHAHI = UserLocationInfo(24.3745, 88.6042, "রাজশাহী", false)
    val KHULNA = UserLocationInfo(22.8456, 89.5403, "খুলনা", false)
    val BARISHAL = UserLocationInfo(22.7010, 90.3535, "বরিশাল", false)
    val RANGPUR = UserLocationInfo(25.7439, 89.2752, "রংপুর", false)
    val MYMENSINGH = UserLocationInfo(24.7471, 90.4203, "ময়মনসিংহ", false)
    val CUMILLA = UserLocationInfo(23.4682, 91.1788, "কুমিল্লা", false)
    val COX_BAZAR = UserLocationInfo(21.4272, 92.0058, "কক্সবাজার", false)
    val BOGURA = UserLocationInfo(24.8465, 89.3777, "বগুড়া", false)
    val JASHORE = UserLocationInfo(23.1634, 89.2182, "যশোর", false)
    val NOAKHALI = UserLocationInfo(22.8696, 91.0994, "নোয়াখালী", false)
    val FARIDPUR = UserLocationInfo(23.6071, 89.8429, "ফরিদপুর", false)
    val KUSHTIA = UserLocationInfo(23.9013, 89.1204, "কুষ্টিয়া", false)
    val DINAJPUR = UserLocationInfo(25.6279, 88.6332, "দিনাজপুর", false)
    val PABNA = UserLocationInfo(24.0064, 89.2372, "পাবনা", false)
    val TANGAIL = UserLocationInfo(24.2513, 89.9167, "টাঙ্গাইল", false)
    val GAZIPUR = UserLocationInfo(24.0023, 90.4264, "গাজীপুর", false)
    val NARAYANGANJ = UserLocationInfo(23.6238, 90.5000, "নারায়ণগঞ্জ", false)
    val BRAHMANBARIA = UserLocationInfo(23.9608, 91.1115, "ব্রাহ্মণবাড়িয়া", false)
    val MAKKAH = UserLocationInfo(21.4225, 39.8262, "পবিত্র মক্কা মুকাররমা", false)
    val MADINAH = UserLocationInfo(24.4672, 39.6111, "মদীনা মুনাওয়ারা", false)

    val popularLocations = listOf(
        DHAKA, CHITTAGONG, SYLHET, RAJSHAHI, KHULNA, BARISHAL, RANGPUR, MYMENSINGH,
        CUMILLA, COX_BAZAR, BOGURA, JASHORE, NOAKHALI, FARIDPUR, KUSHTIA, DINAJPUR,
        PABNA, TANGAIL, GAZIPUR, NARAYANGANJ, BRAHMANBARIA, MAKKAH, MADINAH
    )

    fun hasLocationPermission(context: Context): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarsePermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return finePermission || coarsePermission
    }

    suspend fun getDeviceLocation(context: Context): UserLocationInfo? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission(context)) {
            return@withContext null
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return@withContext null

        try {
            var bestLocation: Location? = null
            val providers = locationManager.getProviders(true)
            for (provider in providers) {
                val l = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    bestLocation = l
                }
            }

            if (bestLocation != null) {
                val cityName = reverseGeocode(context, bestLocation.latitude, bestLocation.longitude)
                return@withContext UserLocationInfo(
                    latitude = bestLocation.latitude,
                    longitude = bestLocation.longitude,
                    cityName = cityName ?: String.format(Locale.US, "অক্ষাংশ: %.2f, দ্রাঘিমাংশ: %.2f", bestLocation.latitude, bestLocation.longitude),
                    isAutoDetected = true
                )
            }
        } catch (_: SecurityException) {
            // Permission not granted or revoked
        } catch (_: Exception) {
            // Fallback
        }
        return@withContext null
    }

    private fun reverseGeocode(context: Context, latitude: Double, longitude: Double): String? {
        return try {
            val geocoder = Geocoder(context, Locale("bn", "BD"))
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locality = address.locality ?: address.subAdminArea ?: address.adminArea
                val country = address.countryName
                if (locality != null && country != null) {
                    "$locality, $country"
                } else locality ?: country
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
