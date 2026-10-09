package com.example.data.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

class PrayerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(PrayerNotificationScheduler.EXTRA_PRAYER_NAME) ?: "নামাজ"
        val prayerTimeStr = intent.getStringExtra(PrayerNotificationScheduler.EXTRA_PRAYER_TIME_STR) ?: ""
        val prayerArabic = intent.getStringExtra(PrayerNotificationScheduler.EXTRA_PRAYER_ARABIC) ?: ""
        val prayerId = intent.getIntExtra(PrayerNotificationScheduler.EXTRA_PRAYER_ID, 100)
        val locationName = intent.getStringExtra(PrayerNotificationScheduler.EXTRA_LOCATION_NAME) ?: "ঢাকা"
        val latitude = intent.getDoubleExtra(PrayerNotificationScheduler.EXTRA_LATITUDE, LocationService.DHAKA.latitude)
        val longitude = intent.getDoubleExtra(PrayerNotificationScheduler.EXTRA_LONGITUDE, LocationService.DHAKA.longitude)

        Log.d("PrayerAlarmReceiver", "Received 10-min alarm for $prayerName ($prayerTimeStr) at $locationName")

        // Ensure notification channel exists
        PrayerNotificationScheduler.createNotificationChannel(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            ?: return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("opened_from_prayer_alert", prayerName)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerId,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val spiritualHadith = getHadithForPrayer(prayerName)

        val contentTitle = "নামাজের ওয়াক্ত সতর্কতা: আর ১০ মিনিট বাকি"
        val contentShort = "আসন্ন $prayerName ওয়াক্ত শুরু হতে ১০ মিনিট বাকি ($prayerTimeStr)। ওজু করে নামাজের প্রস্তুতি নিন।"
        val contentLong = "আসন্ন $prayerName ($prayerArabic) ওয়াক্ত শুরু হতে ১০ মিনিট বাকি ($prayerTimeStr)।\n$spiritualHadith"

        val notification = NotificationCompat.Builder(context, PrayerNotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(contentTitle)
            .setContentText(contentShort)
            .setSubText("ওয়াক্ত: $prayerName")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("নামাজের প্রস্তুতি নিন — আর ১০ মিনিট বাকি")
                    .setSummaryText(prayerName)
                    .bigText(contentLong)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "অ্যাপ খুলুন", pendingIntent)
            .setColor(android.graphics.Color.parseColor("#D4AF37"))
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(prayerId, notification)

        // Reschedule upcoming alarms to keep continuous rotation active
        PrayerNotificationScheduler.scheduleAllPrayerAlerts(
            context = context,
            latitude = latitude,
            longitude = longitude,
            locationName = locationName
        )
    }

    private fun getHadithForPrayer(prayerName: String): String {
        return when {
            prayerName.contains("ফজর") ->
                "রাসূলুল্লাহ ﷺ বলেছেন: 'যে ব্যক্তি দুই শীতল সময়ের নামাজ (ফজর ও আসর) নিয়মিত আদায় করবে, সে জান্নাতে প্রবেশ করবে।' (সহীহ বুখারী ৫৭৪)"
            prayerName.contains("যোহর") ->
                "রাসূলুল্লাহ ﷺ যোহরের পূর্বে চার রাকাত সুন্নাত আদায় করতেন এবং বলতেন: 'এটি এমন সময় যখন আসমানের দরজাসমূহ খুলে দেওয়া হয়।' (তিরমিযী ৪৭৮)"
            prayerName.contains("আসর") ->
                "রাসূলুল্লাহ ﷺ সতর্ক করে বলেছেন: 'যার আসরের নামাজ ছুটে গেল, তার যেন সমস্ত পরিবার-পরিজন ও ধন-সম্পদ ধ্বংস হয়ে গেল।' (সহীহ বুখারী ৫৫২)"
            prayerName.contains("মাগরিব") ->
                "মাগরিবের ওয়াক্ত শুরু হতে যাচ্ছে। রোজাদারের জন্য এটি ইফতারের মোবারক মুহূর্ত—ইফতারের সময় দোয়া কবুল হয়।"
            prayerName.contains("এশা") ->
                "রাসূলুল্লাহ ﷺ বলেছেন: 'মানুষ যদি এশা ও ফজরের জামাতের অসীম সওয়াব জানত, তবে হামাগুড়ি দিয়ে হলেও জামাতে উপস্থিত হতো।' (সহীহ বুখারী ৬১৫)"
            else ->
                "রাসূলুল্লাহ ﷺ বলেছেন: 'কেয়ামতের দিন বান্দার কাছ থেকে সর্বপ্রথম যে আমলের হিসাব নেওয়া হবে, তা হলো নামাজ।' (আবু দাউদ ৮৬৪)"
        }
    }
}
