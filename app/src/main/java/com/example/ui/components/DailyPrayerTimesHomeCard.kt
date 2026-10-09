package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PrayerTimeInfo
import com.example.data.service.LocationService
import com.example.data.service.UserLocationInfo
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

/**
 * Modern, beautifully styled Prayer Times Card for the Home Screen.
 * Automatically displays current location, recalculates 5 daily prayer times,
 * highlights the upcoming/active prayer with a live indicator, and provides instant
 * location detection & switching.
 */
@Composable
fun DailyPrayerTimesHomeCard(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier
) {
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()
    val countdownText by viewModel.countdownText.collectAsState()
    val nextPrayerTitle by viewModel.nextPrayerTitle.collectAsState()
    val nextPrayerSubtitle by viewModel.nextPrayerSubtitle.collectAsState()
    val countdownProgress by viewModel.countdownProgress.collectAsState()

    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .testTag("daily_prayer_times_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.5.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row: Title & Location Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LightGoldTint,
                        border = BorderStroke(1.dp, GoldBorder),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "দৈনিক ৫ ওয়াক্ত নামাজের সময়সূচী",
                            color = BrightGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = currentLocation.cityName,
                                color = TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Location Selector button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier
                        .clickable { viewModel.openLocationPicker(true) }
                        .testTag("button_change_location")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (isLocating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = IslamicGold,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.MyLocation,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "স্থান পরিবর্তন",
                            color = IslamicGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Live Countdown Sub-Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MidnightBlue,
                border = BorderStroke(1.dp, Color(0x22D4AF37))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(BrightGold)
                            )
                            Text(
                                text = nextPrayerTitle,
                                color = BrightGold,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = nextPrayerSubtitle,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "বাকি সময়",
                            color = IslamicGold.copy(alpha = 0.8f),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = countdownText,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // The 5 Main Daily Prayer Times (Fajr, Dhuhr, Asr, Maghrib, Isha) formatted grid/list
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                // Filter and show the core 5 daily prayers (plus sunrise for clarification)
                prayerTimes.forEach { prayer ->
                    PrayerTimeRowItem(prayer = prayer)
                }
            }

            // Calculation Standard & Quick Actions Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "• জ্যোতির্বিদ্যা ও ইসলামিক ফাউন্ডেশন মানদণ্ড",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                TextButton(
                    onClick = { viewModel.openPrayerTimesSheet(true) },
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "বিস্তারিত সময়সূচী",
                        color = IslamicGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrayerTimeRowItem(prayer: PrayerTimeInfo) {
    val isNext = prayer.isNext
    val isPassed = prayer.isPassed

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = when {
            isNext -> NavyCardElevated
            isPassed -> NavySurface.copy(alpha = 0.6f)
            else -> NavySurface
        },
        border = if (isNext) BorderStroke(1.5.dp, BrightGold) else BorderStroke(0.5.dp, Color(0x22D4AF37))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Bengali Name + Arabic Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = when {
                        isNext -> Icons.Default.NotificationsActive
                        isPassed -> Icons.Default.CheckCircle
                        else -> Icons.Default.Schedule
                    },
                    contentDescription = null,
                    tint = when {
                        isNext -> BrightGold
                        isPassed -> EmeraldSuccess.copy(alpha = 0.7f)
                        else -> IslamicGold.copy(alpha = 0.8f)
                    },
                    modifier = Modifier.size(17.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = prayer.name,
                        color = if (isNext) BrightGold else TextWhite,
                        fontSize = 13.5.sp,
                        fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium
                    )

                    Text(
                        text = prayer.arabicName,
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                }
            }

            // Right: Formatted Time + Status Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = prayer.timeString,
                    color = if (isNext) BrightGold else TextLight,
                    fontSize = 13.5.sp,
                    fontWeight = if (isNext) FontWeight.ExtraBold else FontWeight.SemiBold
                )

                if (isNext) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EmeraldContainer
                    ) {
                        Text(
                            text = "পরবর্তী",
                            color = EmeraldSuccess,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (isPassed) {
                    Text(
                        text = "সমাপ্ত",
                        color = TextMuted.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * Location Selection & GPS Auto-Detection Dialog
 */
@Composable
fun LocationPickerDialog(
    viewModel: AlHujurViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentLocation by viewModel.currentLocation.collectAsState()
    val isLocating by viewModel.isLocating.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "নামাজের অবস্থান নির্ধারণ",
                            color = BrightGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "সঠিক অক্ষাংশ ও দ্রাঘিমাংশ অনুযায়ী নামাজের হিসাব",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ", tint = TextLight)
                    }
                }

                // Current GPS Location Button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !isLocating) {
                            viewModel.detectCurrentLocation(context)
                        }
                        .testTag("button_detect_gps"),
                    shape = RoundedCornerShape(14.dp),
                    color = NavyCardElevated,
                    border = BorderStroke(1.dp, BrightGold)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LightGoldTint,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isLocating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = BrightGold,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = null,
                                        tint = BrightGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isLocating) "জিপিএস অবস্থান খোঁজা হচ্ছে..." else "বর্তমান অবস্থান অটো-ডিটেক্ট করুন",
                                color = BrightGold,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ডিভাইসের লোকেশন সেন্সর থেকে সঠিক দ্রাঘিমাংশ",
                                color = TextLight.copy(alpha = 0.8f),
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }

                Text(
                    text = "জনপ্রিয় জেলা ও শহরসমূহ:",
                    color = IslamicGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                // List of preset locations
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 260.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(LocationService.popularLocations) { loc ->
                        val isSelected = loc.cityName == currentLocation.cityName
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLocation(loc)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) NavyCardElevated else NavyCard,
                            border = if (isSelected) BorderStroke(1.dp, BrightGold) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = if (isSelected) BrightGold else IslamicGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = loc.cityName,
                                        color = if (isSelected) BrightGold else TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = BrightGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
