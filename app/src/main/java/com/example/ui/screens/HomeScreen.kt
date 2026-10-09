package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.repository.IslamicRepository
import com.example.ui.components.DailyAmalHomeOverviewCard
import com.example.ui.components.DailyPrayerTimesHomeCard
import com.example.ui.components.HijriCalendarDialog
import com.example.ui.components.HijriDateHomeCard
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.components.LocationPickerDialog
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun HomeScreen(
    viewModel: AlHujurViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToAmal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dailyNasihotIndex by viewModel.dailyNasihotIndex.collectAsState()
    val countdownText by viewModel.countdownText.collectAsState()
    val nextPrayerTitle by viewModel.nextPrayerTitle.collectAsState()
    val nextPrayerSubtitle by viewModel.nextPrayerSubtitle.collectAsState()
    val countdownProgress by viewModel.countdownProgress.collectAsState()

    // Modals
    val showRamadanCalendar by viewModel.showRamadanCalendar.collectAsState()
    val showHijriCalendar by viewModel.showHijriCalendar.collectAsState()
    val showPrayerTimesSheet by viewModel.showPrayerTimesSheet.collectAsState()
    val showLocationPicker by viewModel.showLocationPicker.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val showLibrarySheet by viewModel.showLibrarySheet.collectAsState()
    val showTasbeehSheet by viewModel.showTasbeehSheet.collectAsState()
    val showDuaSheet by viewModel.showDuaSheet.collectAsState()
    val showQiblaSheet by viewModel.showQiblaSheet.collectAsState()

    // Hijri Calendar Data
    val currentHijriDate by viewModel.currentHijriDate.collectAsState()
    val nextSignificantEvent by viewModel.nextSignificantEvent.collectAsState()

    val currentNasihot = IslamicRepository.dailyNasihotList[dailyNasihotIndex]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MidnightBlue, DeepNavy, Color(0xFF060B1B))
                )
            )
    ) {
        IslamicGeometricBackground(alpha = 0.04f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // App Header with dynamic location and current Islamic/Gregorian date
            AppHeaderRow(
                locationName = currentLocation.cityName,
                hijriDate = currentHijriDate,
                onLocationClick = { viewModel.openLocationPicker(true) },
                onDateClick = { viewModel.openHijriCalendar(true) }
            )

            // 1. TOP SECTION: Greeting Card with Scholar Avatar & Daily Nasihot
            ScholarGreetingCard(
                nasihot = currentNasihot,
                onRefreshNasihot = { viewModel.nextNasihot() }
            )

            // 2. HIJRI CALENDAR & SIGNIFICANT ISLAMIC EVENTS MODULE
            HijriDateHomeCard(
                hijriDate = currentHijriDate,
                nextEvent = nextSignificantEvent,
                onOpenCalendar = { viewModel.openHijriCalendar(true) }
            )

            // 3. MIDDLE SECTION: Formatted Daily Prayer Times Calculator Card
            DailyPrayerTimesHomeCard(
                viewModel = viewModel
            )

            // 4. DAILY AMAL TRACKER OVERVIEW (Room Database Connected)
            DailyAmalHomeOverviewCard(
                viewModel = viewModel,
                onNavigateToAmal = onNavigateToAmal
            )

            // 5. GRID SECTION: Large Main Buttons
            MainActionsGrid(
                onAskAiClick = onNavigateToChat,
                onHijriCalendarClick = { viewModel.openHijriCalendar(true) },
                onRamadanCalendarClick = { viewModel.openRamadanCalendar(true) },
                onPrayerTimesClick = { viewModel.openPrayerTimesSheet(true) },
                onLibraryClick = { viewModel.openLibrarySheet(true) }
            )

            // 6. BOTTOM SECTION: Horizontal Scroll of Quick Tools
            QuickToolsSection(
                onTasbeehClick = { viewModel.openTasbeehSheet(true) },
                onDuaClick = { viewModel.openDuaSheet(true) },
                onQiblaClick = { viewModel.openQiblaSheet(true) }
            )
        }
    }

    // --- MODAL DIALOGS / SHEETS ---
    if (showLocationPicker) {
        LocationPickerDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openLocationPicker(false) }
        )
    }

    if (showHijriCalendar) {
        HijriCalendarDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openHijriCalendar(false) }
        )
    }

    if (showRamadanCalendar) {
        RamadanCalendarDialog(onDismiss = { viewModel.openRamadanCalendar(false) })
    }

    if (showPrayerTimesSheet) {
        PrayerTimesDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openPrayerTimesSheet(false) }
        )
    }

    if (showLibrarySheet) {
        IslamicLibraryDialog(onDismiss = { viewModel.openLibrarySheet(false) })
    }

    if (showTasbeehSheet) {
        TasbeehDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openTasbeehSheet(false) }
        )
    }

    if (showDuaSheet) {
        DailyDuaDialog(onDismiss = { viewModel.openDuaSheet(false) })
    }

    if (showQiblaSheet) {
        QiblaCompassDialog(onDismiss = { viewModel.openQiblaSheet(false) })
    }
}

@Composable
private fun AppHeaderRow(
    locationName: String,
    hijriDate: com.example.data.model.HijriDateInfo,
    onLocationClick: () -> Unit,
    onDateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.clickable { onDateClick() }
        ) {
            Text(
                text = "আল-হুজুর এআই",
                color = IslamicGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = hijriDate.formattedHijriBn,
                    color = BrightGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "${com.example.data.service.HijriCalendarService.toBengaliDigits(hijriDate.gregorianDay)} ${hijriDate.gregorianMonthNameBn}",
                    color = TextLight.copy(alpha = 0.85f),
                    fontSize = 12.sp
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NavyCard,
            border = BorderStroke(1.dp, GoldBorder),
            modifier = Modifier.clickable { onLocationClick() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess)
                )
                Text(
                    text = locationName,
                    color = TextLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 1. TOP SECTION: Scholar Greeting Card with Avatar & Daily Nasihot
// -------------------------------------------------------------
@Composable
private fun ScholarGreetingCard(
    nasihot: com.example.data.model.DailyNasihot,
    onRefreshNasihot: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(24.dp))
            .testTag("scholar_greeting_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Box {
            // Subtle gold gradient highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color.Transparent, IslamicGold, BrightGold, Color.Transparent)
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Scholar Profile Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Scholar Avatar with golden ring
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .border(2.dp, IslamicGold, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_scholar_avatar),
                                contentDescription = "AI Hujur Scholar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "আল-হুজুর এআই",
                                    color = TextWhite,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = EmeraldContainer
                                ) {
                                    Text(
                                        text = "অনলাইন মুফতি",
                                        color = EmeraldSuccess,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "দৈনিক নসিহত • ${nasihot.category}",
                                color = IslamicGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onRefreshNasihot,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("refresh_nasihot_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Advice",
                            tint = BrightGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Speech Bubble displaying Daily Advice
                Surface(
                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x33D4AF37))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = nasihot.title,
                                color = BrightGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = nasihot.advice,
                            color = TextLight,
                            fontSize = 13.5.sp,
                            lineHeight = 20.sp
                        )

                        Text(
                            text = "রেফারেন্স: ${nasihot.reference}",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. MIDDLE SECTION: Live Countdown Timer Card
// -------------------------------------------------------------
@Composable
private fun PrayerCountdownCard(
    title: String,
    subtitle: String,
    countdown: String,
    progress: Float,
    onViewAllTimes: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(24.dp))
            .testTag("prayer_countdown_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.5.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = title,
                            color = BrightGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = subtitle,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightGoldTint,
                    border = BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier.clickable { onViewAllTimes() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "সময়সূচী",
                            color = IslamicGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Big Live Countdown Clock
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MidnightBlue,
                border = BorderStroke(1.dp, Color(0x22D4AF37))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "বাকি সময় (কাউন্টডাউন)",
                        color = IslamicGold.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = countdown,
                        color = TextWhite,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    // Linear progress bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = IslamicGold,
                        trackColor = Color(0x33D4AF37)
                    )

                    Text(
                        text = "বাংলাদেশ নামাজের সময়সূচী অনুযায়ী লাইভ কাউন্টডাউন",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GRID SECTION: Large Main Buttons & Ramadan Banner
// -------------------------------------------------------------
@Composable
private fun MainActionsGrid(
    onAskAiClick: () -> Unit,
    onHijriCalendarClick: () -> Unit,
    onRamadanCalendarClick: () -> Unit,
    onPrayerTimesClick: () -> Unit,
    onLibraryClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "ইসলামিক সেবাসমূহ",
            color = IslamicGold,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainGridButton(
                title = "Scholar AI",
                subtitle = "ফতোয়া ও মাসয়ালা",
                icon = Icons.Default.AutoAwesome,
                accentColor = BrightGold,
                onClick = onAskAiClick,
                testTag = "button_ask_ai",
                modifier = Modifier.weight(1f)
            )

            MainGridButton(
                title = "হিজরি ক্যালেন্ডার",
                subtitle = "তারিখ ও বিশেষ দিবস",
                icon = Icons.Default.CalendarMonth,
                accentColor = IslamicGold,
                onClick = onHijriCalendarClick,
                testTag = "button_hijri_calendar",
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainGridButton(
                title = "নামাজের সময়",
                subtitle = "৫ ওয়াক্ত ও আজান",
                icon = Icons.Default.Mosque,
                accentColor = EmeraldSuccess,
                onClick = onPrayerTimesClick,
                testTag = "button_prayer_times",
                modifier = Modifier.weight(1f)
            )

            MainGridButton(
                title = "ইসলামিক লাইব্রেরি",
                subtitle = "কুরআন, হাদিস ও নাম",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                accentColor = CoralAccent,
                onClick = onLibraryClick,
                testTag = "button_islamic_library",
                modifier = Modifier.weight(1f)
            )
        }

        // Full-width Ramadan Calendar banner card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRamadanCalendarClick() }
                .testTag("button_ramadan_calendar"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = NavyCard),
            border = BorderStroke(1.dp, GoldBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x22D4AF37)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NightsStay,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "রমজান সময়সূচি ও ক্যালেন্ডার",
                            color = BrightGold,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "৩০ দিনের সেহরি, ইফতার ও ফজিলতপূর্ণ আমল",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun MainGridButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = accentColor.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. BOTTOM SECTION: Horizontal Scroll of Quick Tools
// -------------------------------------------------------------
@Composable
private fun QuickToolsSection(
    onTasbeehClick: () -> Unit,
    onDuaClick: () -> Unit,
    onQiblaClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "জরুরি টুলস",
                color = IslamicGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "সোয়াইপ করুন",
                color = TextMuted,
                fontSize = 12.sp
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickToolPill(
                title = "ডিজিটাল তাসবীহ",
                subtitle = "জিকির গণনা (৩৩/৯৯)",
                icon = Icons.Default.Fingerprint,
                badge = "কাউন্টার",
                onClick = onTasbeehClick,
                testTag = "quick_tool_tasbeeh"
            )

            QuickToolPill(
                title = "দৈনিক দোয়া",
                subtitle = "সকাল-সন্ধ্যার মোনাজাত",
                icon = Icons.Default.Favorite,
                badge = "হিসনুল মুসলিম",
                onClick = onDuaClick,
                testTag = "quick_tool_dua"
            )

            QuickToolPill(
                title = "ক্বিবলা কম্পাস",
                subtitle = "পবিত্র কাবার সঠিক দিক",
                icon = Icons.Default.Explore,
                badge = "মক্কা",
                onClick = onQiblaClick,
                testTag = "quick_tool_qibla"
            )
        }
    }
}

@Composable
private fun QuickToolPill(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .width(190.dp)
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightGoldTint,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x22D4AF37)
                ) {
                    Text(
                        text = badge,
                        color = IslamicGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGS & DETAIL SHEETS
// -------------------------------------------------------------

@Composable
fun RamadanCalendarDialog(onDismiss: () -> Unit) {
    val ramadanDays = remember { IslamicRepository.getRamadan30Days() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "পবিত্র রমজান ক্যালেন্ডার ১৪৪৭",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "৩০ দিনের সেহরি ও ইফতারের সময়সূচী (ঢাকা ও বাংলাদেশ)",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hero Ramadan banner image
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ramadan_banner),
                        contentDescription = "রমজান মোবারক",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timetable Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavySurface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("রমজান / বার", color = IslamicGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
                    Text("সেহরি শেষ", color = IslamicGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
                    Text("ইফতার", color = IslamicGold, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.End)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ramadanDays) { day ->
                        val isToday = day.isToday
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isToday) NavyCardElevated else NavyCard,
                            border = if (isToday) BorderStroke(1.dp, BrightGold) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1.1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = day.dateString,
                                        color = if (isToday) BrightGold else TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = "(${day.dayOfWeek})",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = day.sehriTime,
                                    color = TextLight,
                                    fontSize = 12.5.sp,
                                    modifier = Modifier.weight(1.2f),
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = day.iftarTime,
                                    color = if (isToday) BrightGold else IslamicGold,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.weight(1.1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrayerTimesDialog(viewModel: AlHujurViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val prayerTimes by viewModel.prayerTimes.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val prayerNotifications by viewModel.prayerNotificationsEnabled.collectAsState()

    var testNotificationSent by remember { mutableStateOf(false) }

    // Android 13+ runtime notification permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            if (!prayerNotifications) {
                viewModel.togglePrayerNotifications()
            }
        }
    }

    val requestNotificationPermissionAndToggle = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.togglePrayerNotifications()
            }
        } else {
            viewModel.togglePrayerNotifications()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
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
                            text = "দৈনিক ৫ ওয়াক্ত নামাজের সময়",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${currentLocation.cityName} • ইসলামিক ফাউন্ডেশন মানদণ্ড",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                // 10-Minute Reminder Notification Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_notification_alert_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (prayerNotifications) Color(0xFF132247) else NavyCard
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (prayerNotifications) IslamicGold else GoldBorder.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (prayerNotifications) Color(0x33D4AF37) else Color(0x22FFFFFF),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (prayerNotifications) Icons.Default.NotificationsActive else Icons.Outlined.NotificationsOff,
                                            contentDescription = null,
                                            tint = if (prayerNotifications) BrightGold else TextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "১০ মিনিট পূর্বে নোটিফিকেশন সতর্কতা",
                                        color = if (prayerNotifications) BrightGold else TextLight,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (prayerNotifications) "প্রতি ওয়াক্ত শুরুর ১০ মিনিট আগে স্থানীয় অ্যালার্ট" else "সতর্কবার্তা বন্ধ রয়েছে",
                                        color = if (prayerNotifications) EmeraldSuccess else TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = prayerNotifications,
                                onCheckedChange = { requestNotificationPermissionAndToggle() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MidnightBlue,
                                    checkedTrackColor = BrightGold,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = NavySurface
                                ),
                                modifier = Modifier.testTag("toggle_prayer_notifications_dialog")
                            )
                        }

                        Text(
                            text = "প্রতিটি ওয়াক্ত শুরু হওয়ার ঠিক ১০ মিনিট পূর্বে আপনার ফোনে স্থানীয় নোটিফিকেশন আসবে যাতে সময়মতো ওজু সম্পন্ন করে জামাতে নামাজের প্রস্তুতি নেওয়া যায়।",
                            color = TextLight.copy(alpha = 0.8f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )

                        // Test Notification Action Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val hasPermission = ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (!hasPermission) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                            return@OutlinedButton
                                        }
                                    }
                                    viewModel.sendTestPrayerAlert()
                                    testNotificationSent = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, IslamicGold),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = BrightGold
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("button_test_prayer_notification")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "টেস্ট নোটিফিকেশন পাঠান",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (testNotificationSent) {
                                Text(
                                    text = "✓ নোটিফিকেশন পাঠানো হয়েছে",
                                    color = EmeraldSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "ওয়াক্তের তালিকা",
                    color = IslamicGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // 5 Daily Prayers List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    prayerTimes.forEach { prayer ->
                        val isNext = prayer.isNext
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isNext) NavyCardElevated else NavyCard,
                            border = if (isNext) BorderStroke(1.5.dp, BrightGold) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isNext) Icons.Default.NotificationsActive else Icons.Outlined.Notifications,
                                        contentDescription = null,
                                        tint = if (isNext) BrightGold else IslamicGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = prayer.name,
                                            color = if (isNext) BrightGold else TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            text = prayer.arabicName,
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = prayer.timeString,
                                        color = if (isNext) BrightGold else TextLight,
                                        fontWeight = if (isNext) FontWeight.Black else FontWeight.Medium,
                                        fontSize = 14.5.sp
                                    )
                                    if (isNext) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldContainer
                                        ) {
                                            Text(
                                                text = "চলতি",
                                                color = EmeraldSuccess,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
    }
}

@Composable
fun IslamicLibraryDialog(onDismiss: () -> Unit) {
    val items = remember { IslamicRepository.libraryItems }
    var selectedCategory by remember { mutableStateOf("সবগুলো") }
    val categories = listOf("সবগুলো", "সূরা", "হাদিস", "আল্লাহর গুণবাচক নাম")

    val filteredItems = remember(selectedCategory) {
        if (selectedCategory == "সবগুলো") items else items.filter { it.category == selectedCategory }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ইসলামিক লাইব্রেরি",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "কুরআনের গুরুত্বপূর্ণ সূরা, হাদিস ও আল্লাহর আসমাউল হুসনা",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                // Filter tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = (selectedCategory == cat),
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGold,
                                selectedLabelColor = MidnightBlue,
                                containerColor = NavySurface,
                                labelColor = TextLight
                            )
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            border = BorderStroke(1.dp, Color(0x33D4AF37))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        color = BrightGold,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = LightGoldTint
                                    ) {
                                        Text(
                                            text = item.category,
                                            color = IslamicGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = item.subtitle,
                                    color = TextMuted,
                                    fontSize = 11.5.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MidnightBlue,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = item.arabicText,
                                        color = BrightGold,
                                        fontSize = 16.sp,
                                        lineHeight = 26.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }

                                Text(
                                    text = item.englishTranslation,
                                    color = TextLight,
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp
                                )

                                Text(
                                    text = "• ${item.reference}",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasbeehDialog(viewModel: AlHujurViewModel, onDismiss: () -> Unit) {
    val count by viewModel.tasbeehCount.collectAsState()
    val target by viewModel.tasbeehTarget.collectAsState()
    val dhikr by viewModel.selectedDhikr.collectAsState()

    val dhikrPresets = listOf(
        "সুবহানাল্লাহ (سُبْحَانَ اللَّهِ)",
        "আলহামদুলিল্লাহ (الْحَمْدُ لِلَّهِ)",
        "আল্লাহু আকবার (اللَّهُ أَكْبَرُ)",
        "আস্তাগফিরুল্লাহ (أَسْتَغْفِرُ اللَّهَ)",
        "লা ইলাহা ইল্লাল্লাহ (لَا إِلٰهَ إِلَّا اللَّهُ)"
    )

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
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ডিজিটাল তাসবীহ",
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                Text(
                    text = dhikr,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                // Large Interactive Circular Counter Button
                Surface(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(3.dp, IslamicGold, CircleShape)
                        .clickable { viewModel.incrementTasbeeh() }
                        .testTag("tasbeeh_tap_button"),
                    color = NavySurface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$count",
                            color = BrightGold,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "টার্গেট: $target বার",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "গণনা করতে চাপুন",
                            color = IslamicGold.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Controls row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.resetTasbeeh() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight),
                        border = BorderStroke(1.dp, GoldBorder)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("রিসেট", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val nextIndex = (dhikrPresets.indexOf(dhikr) + 1) % dhikrPresets.size
                            viewModel.setDhikr(dhikrPresets[nextIndex], 33)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = MidnightBlue)
                    ) {
                        Text("পরবর্তী জিকির", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DailyDuaDialog(onDismiss: () -> Unit) {
    val duas = remember { IslamicRepository.quickDuas }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "দৈনিক প্রয়োজনীয় দোয়া (হিসনুল মুসলিম)",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "উচ্চারণ, বাংলা অর্থ ও ফজিলতসহ",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(duas) { dua ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyCard),
                            border = BorderStroke(1.dp, Color(0x33D4AF37))
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dua.title,
                                        color = BrightGold,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = LightGoldTint
                                    ) {
                                        Text(
                                            text = dua.occasion,
                                            color = IslamicGold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MidnightBlue,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = dua.arabic,
                                        color = BrightGold,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }

                                Text(
                                    text = "উচ্চারণ: ${dua.transliteration}",
                                    color = TextLight,
                                    fontSize = 12.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )

                                Text(
                                    text = "অর্থ: \"${dua.translation}\"",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QiblaCompassDialog(onDismiss: () -> Unit) {
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
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ক্বিবলা কম্পাস (দিক-নির্ণয়)",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "পবিত্র কাবা শরীফ (মক্কা মুকাররমা)-এর দিক",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                // Compass Dial
                Surface(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .border(3.dp, IslamicGold, CircleShape),
                    color = MidnightBlue
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Degree markers
                        Text(
                            text = "উ",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 8.dp)
                        )
                        Text(
                            text = "দ",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 8.dp)
                        )
                        Text(
                            text = "প",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                        )
                        Text(
                            text = "পূ",
                            color = TextMuted,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                        )

                        // Center Kaaba Indicator
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(42.dp)
                            )
                            Text(
                                text = "২৯৪° পশ্চিম",
                                color = BrightGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "কাবার অভিমুখ",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, GoldBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("দূরত্ব (বাংলাদেশ)", color = TextMuted, fontSize = 11.sp)
                            Text("~৪,২৮০ কিমি", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("সেন্সর স্ট্যাটাস", color = TextMuted, fontSize = 11.sp)
                            Text("সক্রিয়", color = EmeraldSuccess, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("দিক", color = TextMuted, fontSize = 11.sp)
                            Text("ক্বিবলামুখী", color = BrightGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
