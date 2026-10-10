package com.example.ui.screens

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.data.repository.IslamicUniqueRepository
import com.example.ui.components.*
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
    val showAsmaulHusnaSheet by viewModel.showAsmaulHusnaSheet.collectAsState()
    val showZakatSheet by viewModel.showZakatSheet.collectAsState()
    val showQuranReaderSheet by viewModel.showQuranReaderSheet.collectAsState()
    val showIslamicEventsSheet by viewModel.showIslamicEventsSheet.collectAsState()
    val showTazkiyahSheet by viewModel.showTazkiyahSheet.collectAsState()
    val showWaswasahSosDialog by viewModel.showWaswasahSosDialog.collectAsState()
    val badHabits by viewModel.badHabits.collectAsState()
    val showQuizDialog by viewModel.showQuizDialog.collectAsState()
    val showDawahCardMakerDialog by viewModel.showDawahCardMakerDialog.collectAsState()
    val showFajrBuddyDialog by viewModel.showFajrBuddyDialog.collectAsState()

    // Hijri Calendar Data
    val currentHijriDate by viewModel.currentHijriDate.collectAsState()
    val currentBanglaDate by viewModel.currentBanglaDate.collectAsState()
    val nextSignificantEvent by viewModel.nextSignificantEvent.collectAsState()
    val upcomingIslamicEvents by viewModel.upcomingIslamicEvents.collectAsState()

    // User & Auth State
    val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()

    val currentNasihot = IslamicRepository.dailyNasihotList[dailyNasihotIndex]
    val todayVerse = remember { IslamicUniqueRepository.getTodayVerse() }

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
            // App Header with dynamic location and User Login profile button
            AppHeaderRow(
                locationName = currentLocation.cityName,
                isLoggedIn = isUserLoggedIn,
                userName = userName,
                onLocationClick = { viewModel.openLocationPicker(true) },
                onAuthClick = { viewModel.openAuthDialog(true) }
            )

            // 1. 3-IN-1 TRIPLE CALENDAR & ISLAMIC SPECIAL DAYS TICKER (উপরে স্থির ৩-ইন-১ ক্যালেন্ডার ও নিচে স্লাইড আকারে বিশেষ দিনসমূহ)
            UnifiedCalendarEventsSliderCard(
                hijriDate = currentHijriDate,
                banglaDate = currentBanglaDate,
                upcomingEvents = upcomingIslamicEvents,
                onOpenCalendar = { viewModel.openHijriCalendar(true) },
                onOpenEvents = { viewModel.openIslamicEventsSheet(true) }
            )

            // 2. AL-HUJUR AI ASSISTANT & SACRED WISDOM (সরাসরি ভয়েস ও চ্যাটে প্রশ্ন করার সুবিধা সহ)
            AlHujurUnifiedWisdomCard(
                nasihot = currentNasihot,
                verse = todayVerse,
                currentIndex = dailyNasihotIndex,
                totalCount = IslamicRepository.dailyNasihotList.size,
                onNextWisdom = { viewModel.nextNasihot() },
                onPreviousWisdom = { viewModel.previousNasihot() },
                onOpenQuranReader = { viewModel.openQuranReader(true) },
                onAskAiClick = onNavigateToChat,
                onAskQuestionWithPrompt = { question ->
                    viewModel.sendChatMessage(question)
                    onNavigateToChat()
                }
            )

            // 3. DAILY PRAYER TIMES CALCULATOR & COUNTDOWN (৫ ওয়াক্ত নামাজের সময় ও কাউন্টডাউন)
            DailyPrayerTimesHomeCard(
                viewModel = viewModel
            )

            // 4. RAMADAN SPECIAL HUB: Upcoming Ramadan 1447 Facilities, Sehri/Iftar Live Timings, Fast Tracker & Duas
            RamadanSpecialHubCard(
                viewModel = viewModel,
                onOpenFullRamadanSchedule = { viewModel.openRamadanCalendar(true) }
            )

            // 5. DAILY AMAL TRACKER OVERVIEW (Room Database Connected)
            DailyAmalHomeOverviewCard(
                viewModel = viewModel,
                onNavigateToAmal = onNavigateToAmal
            )

            // 6. TAZKIYAH & BAD HABIT BREAKER CARD (খারাপ অভ্যাস বর্জন ও আত্মশুদ্ধি ট্র্যাকার)
            TazkiyahHabitHomeCard(
                habits = badHabits,
                onOpenFullTazkiyah = { viewModel.openTazkiyahSheet(true) },
                onOpenEmergencySos = { viewModel.openWaswasahSosDialog(true) },
                onMarkClean = { habitId -> viewModel.markHabitCleanToday(habitId) }
            )

            // 7. SADAKAH JARIYAH & VIRAL DAWAH HUB (ইসলামিক কুইজ, সোশ্যাল স্টোরি মেকার ও উম্মাহ দরূদ চেইন)
            ViralDawahHubCard(
                onOpenQuiz = { viewModel.openQuizDialog(true) },
                onOpenDawahCardMaker = { viewModel.openDawahCardMakerDialog(true) },
                onOpenFajrBuddy = { viewModel.openFajrBuddyDialog(true) }
            )

            // 8. GRID SECTION: Large Main Buttons
            MainActionsGrid(
                onAskAiClick = onNavigateToChat,
                onHijriCalendarClick = { viewModel.openHijriCalendar(true) },
                onRamadanCalendarClick = { viewModel.openRamadanCalendar(true) },
                onPrayerTimesClick = { viewModel.openPrayerTimesSheet(true) },
                onLibraryClick = { viewModel.openLibrarySheet(true) }
            )

            // 9. BOTTOM SECTION: Horizontal Scroll of Quick Tools & Unique Facilities
            QuickToolsSection(
                onQuranReaderClick = { viewModel.openQuranReader(true) },
                onTasbeehClick = { viewModel.openTasbeehSheet(true) },
                onDuaClick = { viewModel.openDuaSheet(true) },
                onQiblaClick = { viewModel.openQiblaSheet(true) },
                onAsmaulHusnaClick = { viewModel.openAsmaulHusnaSheet(true) },
                onZakatClick = { viewModel.openZakatSheet(true) },
                onIslamicEventsClick = { viewModel.openIslamicEventsSheet(true) },
                onTazkiyahClick = { viewModel.openTazkiyahSheet(true) },
                onQuizClick = { viewModel.openQuizDialog(true) },
                onDawahCardMakerClick = { viewModel.openDawahCardMakerDialog(true) },
                onFajrBuddyClick = { viewModel.openFajrBuddyDialog(true) }
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
        RamadanCalendarDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openRamadanCalendar(false) }
        )
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

    if (showAsmaulHusnaSheet) {
        AsmaulHusnaDialog(onDismiss = { viewModel.openAsmaulHusnaSheet(false) })
    }

    if (showZakatSheet) {
        ZakatCalculatorDialog(onDismiss = { viewModel.openZakatSheet(false) })
    }

    if (showQuranReaderSheet) {
        QuranReaderDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openQuranReader(false) }
        )
    }

    if (showIslamicEventsSheet) {
        IslamicSpecialEventsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openIslamicEventsSheet(false) }
        )
    }

    if (showTazkiyahSheet) {
        TazkiyahHabitBreakerDialog(
            habits = badHabits,
            onDismiss = { viewModel.openTazkiyahSheet(false) },
            onMarkClean = { id -> viewModel.markHabitCleanToday(id) },
            onResetRelapse = { id -> viewModel.resetHabitRelapse(id) },
            onAddCustomHabit = { title, reason, cat -> viewModel.addCustomBadHabit(title, reason, cat) },
            onDeleteHabit = { id -> viewModel.deleteBadHabit(id) },
            onOpenEmergencySos = {
                viewModel.openTazkiyahSheet(false)
                viewModel.openWaswasahSosDialog(true)
            },
            onConsultAi = { query ->
                viewModel.openTazkiyahSheet(false)
                viewModel.sendChatMessage(query)
                onNavigateToChat()
            }
        )
    }

    if (showWaswasahSosDialog) {
        WaswasahSosDialog(
            onDismiss = { viewModel.openWaswasahSosDialog(false) },
            onConsultAi = { query ->
                viewModel.openWaswasahSosDialog(false)
                viewModel.sendChatMessage(query)
                onNavigateToChat()
            }
        )
    }

    if (showQuizDialog) {
        IslamicQuizDialog(
            onDismiss = { viewModel.openQuizDialog(false) }
        )
    }

    if (showDawahCardMakerDialog) {
        DawahCardMakerDialog(
            onDismiss = { viewModel.openDawahCardMakerDialog(false) }
        )
    }

    if (showFajrBuddyDialog) {
        FajrBuddyDialog(
            onDismiss = { viewModel.openFajrBuddyDialog(false) }
        )
    }

    if (showAuthDialog) {
        AuthDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.openAuthDialog(false) }
        )
    }
}

@Composable
private fun AppHeaderRow(
    locationName: String,
    isLoggedIn: Boolean,
    userName: String,
    onLocationClick: () -> Unit,
    onAuthClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Islamic Mind",
                color = IslamicGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "সহীহ দ্বীন ও আত্মিক প্রশান্তি",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NavyCard,
                border = BorderStroke(1.dp, GoldBorder),
                modifier = Modifier.clickable { onLocationClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess)
                    )
                    Text(
                        text = locationName,
                        color = TextLight,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isLoggedIn) Color(0x33D4AF37) else NavyCard,
                border = BorderStroke(1.dp, if (isLoggedIn) IslamicGold else GoldBorder),
                modifier = Modifier
                    .clickable { onAuthClick() }
                    .testTag("home_auth_button")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.CheckCircle else Icons.Default.AccountCircle,
                        contentDescription = "প্রোফাইল ও লগইন",
                        tint = if (isLoggedIn) EmeraldSuccess else BrightGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = if (isLoggedIn) "প্রোফাইল" else "লগইন",
                        color = if (isLoggedIn) BrightGold else TextLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 1. TOP SECTION: Scholar Greeting Card with Avatar & Auto-Updating Daily Nasihot
// -------------------------------------------------------------
@Composable
private fun ScholarGreetingCard(
    nasihot: com.example.data.model.DailyNasihot,
    onRefreshNasihot: () -> Unit,
    onPreviousNasihot: () -> Unit = {},
    currentIndex: Int = 0,
    totalCount: Int = 10
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
                                .size(54.dp)
                                .clip(CircleShape)
                                .border(2.dp, IslamicGold, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_scholar_avatar),
                                contentDescription = "Islamic Mind AI Scholar",
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
                                    text = "ইসলামিক স্কলার এআই",
                                    color = TextWhite,
                                    fontSize = 16.sp,
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FiberManualRecord,
                                    contentDescription = null,
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(8.dp)
                                )
                                Text(
                                    text = "অটো-আপডেট বাণী (${com.example.data.service.HijriCalendarService.toBengaliDigits(currentIndex + 1)}/${com.example.data.service.HijriCalendarService.toBengaliDigits(totalCount)})",
                                    color = IslamicGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Navigation Controls: Previous, Shuffle/Next
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        IconButton(
                            onClick = onPreviousNasihot,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("previous_nasihot_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "পূর্ববর্তী বাণী",
                                tint = BrightGold,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        IconButton(
                            onClick = onRefreshNasihot,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("refresh_nasihot_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "পরবর্তী বাণী",
                                tint = BrightGold,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // Speech Bubble displaying Daily Advice with smooth animated transition
                AnimatedContent(
                    targetState = nasihot,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "NasihotAnimation"
                ) { currentQuote ->
                    Surface(
                        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                        color = NavySurface,
                        border = BorderStroke(1.dp, Color(0x33D4AF37)),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = currentQuote.title,
                                    color = BrightGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Text(
                                text = currentQuote.advice,
                                color = TextLight,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp
                            )

                            Text(
                                text = "রেফারেন্স: ${currentQuote.reference} • ${currentQuote.category}",
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
    onQuranReaderClick: () -> Unit,
    onTasbeehClick: () -> Unit,
    onDuaClick: () -> Unit,
    onQiblaClick: () -> Unit,
    onAsmaulHusnaClick: () -> Unit,
    onZakatClick: () -> Unit,
    onIslamicEventsClick: () -> Unit,
    onTazkiyahClick: () -> Unit = {},
    onQuizClick: () -> Unit = {},
    onDawahCardMakerClick: () -> Unit = {},
    onFajrBuddyClick: () -> Unit = {}
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
                text = "জরুরি টুলস ও সুবিধা",
                color = IslamicGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "সোয়াইপ করুন ➜",
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
                title = "ইসলামিক কুইজ",
                subtitle = "মেধা পরীক্ষা ও চ্যালেঞ্জ",
                icon = Icons.Default.EmojiEvents,
                badge = "কুইজ 🏆",
                onClick = onQuizClick,
                testTag = "quick_tool_quiz"
            )

            QuickToolPill(
                title = "সোশ্যাল স্টোরি মেকার",
                subtitle = "১-ক্লিকে আয়াত স্ট্যাটাস",
                icon = Icons.Outlined.PhotoCamera,
                badge = "দাওয়াহ ✨",
                onClick = onDawahCardMakerClick,
                testTag = "quick_tool_dawah_maker"
            )

            QuickToolPill(
                title = "ফজর ওয়েক-আপ ফ্রেন্ড",
                subtitle = "হোয়াটসঅ্যাপে ফজর দাওয়াহ",
                icon = Icons.Outlined.WbTwilight,
                badge = "সুন্নাহ 🌅",
                onClick = onFajrBuddyClick,
                testTag = "quick_tool_fajr_buddy"
            )

            QuickToolPill(
                title = "খারাপ অভ্যাস বর্জন",
                subtitle = "আত্মশুদ্ধি ও নফস নিয়ন্ত্রণ",
                icon = Icons.Default.Shield,
                badge = "তাজকিয়াহ",
                onClick = onTazkiyahClick,
                testTag = "quick_tool_tazkiyah"
            )

            QuickToolPill(
                title = "বিশেষ দিন ও দিন গণনা",
                subtitle = "হিজরি তাৎপর্যপূর্ণ দিবসসমূহ",
                icon = Icons.Outlined.EventAvailable,
                badge = "কাউন্টডাউন",
                onClick = onIslamicEventsClick,
                testTag = "quick_tool_islamic_events"
            )

            QuickToolPill(
                title = "পবিত্র কুরআন রিডার",
                subtitle = "আয়াতভিত্তিক তিলাওয়াত ও সার্চ",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                badge = "কুরআন",
                onClick = onQuranReaderClick,
                testTag = "quick_tool_quran_reader"
            )

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

            QuickToolPill(
                title = "আসমাউল হুসনা",
                subtitle = "আল্লাহর ৯৯টি পবিত্র নাম",
                icon = Icons.Default.Star,
                badge = "ফজিলত",
                onClick = onAsmaulHusnaClick,
                testTag = "quick_tool_asmaul_husna"
            )

            QuickToolPill(
                title = "যাকাত ক্যালকুলেটর",
                subtitle = "সম্পদ ও নিসাব পরিমাপ",
                icon = Icons.Default.Calculate,
                badge = "শারঈ হিসাব",
                onClick = onZakatClick,
                testTag = "quick_tool_zakat"
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
fun RamadanCalendarDialog(
    viewModel: AlHujurViewModel,
    onDismiss: () -> Unit
) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    val context = LocalContext.current
    var selectedCity by remember { mutableStateOf(currentLocation.cityName) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Timetable, 1: Duas, 2: Tarabi & Fitrah

    val offsetMinutes = remember(selectedCity) {
        IslamicRepository.getDistrictOffsetMinutes(selectedCity)
    }

    val ramadanDays = remember(selectedCity, offsetMinutes) {
        IslamicRepository.getRamadan30Days(offsetMinutes)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .testTag("ramadan_calendar_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "পবিত্র মাহে রমজান",
                                color = BrightGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "স্থানীয়ভাবে যাচাইকৃত সময়সূচি অনুসরণ করুন",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = NavySurface,
                    contentColor = BrightGold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("সময়সূচি", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("রমজানের দোয়া", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("তারাবীহ ও ফিতরা", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = NavySurface,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("যাচাইকৃত রমজান সময়সূচি পাওয়া যায়নি",
                                    color = BrightGold, fontWeight = FontWeight.Bold)
                                Text("আগের ৩০ দিনের সময়গুলো অনুমান করে তৈরি করা হতো; " +
                                    "সেগুলো সেহরি বা ইফতারের জন্য নিরাপদ নয়। " +
                                    "আপনার জেলার ইসলামিক ফাউন্ডেশন বা মসজিদের " +
                                    "প্রকাশিত সময়সূচি অনুসরণ করুন।", color = TextLight)
                            }
                        }
                    }
                    1 -> {
                        // TAB 1: RAMADAN DUAS
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(IslamicRepository.ramadanSpecialDuas) { dua ->
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = NavySurface,
                                    border = BorderStroke(1.dp, Color(0x33D4AF37)),
                                    modifier = Modifier.fillMaxWidth()
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
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText(
                                                        dua.title,
                                                        "${dua.title}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.pronunciationBn}\n\nঅর্থ: ${dua.meaningBn}\n[${dua.reference}]"
                                                    )
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "দোয়া কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "কপি",
                                                    tint = IslamicGold,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "পাঠ্য সময়: ${dua.occasion}",
                                            color = EmeraldSuccess,
                                            fontSize = 11.sp
                                        )

                                        // Arabic
                                        Text(
                                            text = dua.arabicText,
                                            color = TextWhite,
                                            fontSize = 16.sp,
                                            lineHeight = 26.sp,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // Pronunciation
                                        Text(
                                            text = "উচ্চারণ: ${dua.pronunciationBn}",
                                            color = TextLight,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        )

                                        // Meaning
                                        Text(
                                            text = "অর্থ: \"${dua.meaningBn}\"",
                                            color = BrightGold,
                                            fontSize = 12.5.sp,
                                            lineHeight = 18.sp,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Text(
                                            text = "উৎস: ${dua.reference}",
                                            color = TextMuted,
                                            fontSize = 10.5.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // TAB 2: TARABI & FITRAH GUIDE
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = NavySurface,
                                    border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "তারাবীহ নামাজের ২০ রাকাত সুন্নাহ নিয়ম",
                                            color = BrightGold,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "১. এশার ফরজ ও দুই রাকাত সুন্নাত আদায়ের পর তারাবীহ নামাজ পড়তে হয়।\n২. তারাবীহ মোট ২০ রাকাত, দুই রাকাত করে ১০ সালামে সমাপ্ত করা সুন্নাতে মুয়াক্কাদা।\n৩. প্রতি চার রাকাত পর কিছু সময় বিশ্রাম নিয়ে তাসবীহ ও ইস্তিগফার পাঠ করা মুস্তাহাব।\n৪. তারাবীহ শেষে তিন রাকাত বিতর নামাজ জামাতে বা একাকী আদায় করতে হয়।",
                                            color = TextLight,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            item {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = NavySurface,
                                    border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "সাদাকাতুল ফিতরা হিসাব (ইসলামিক ফাউন্ডেশন)",
                                            color = BrightGold,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "ঈদের দিন সুবহে সাদিকের সময় যার নিকট জাকাতের নেসাব পরিমাণ সম্পদ অতিরিক্ত থাকবে, তার নিজের ও পরিবারের পক্ষ থেকে ফিতরা দেওয়া ওয়াজিব।",
                                            color = TextLight,
                                            fontSize = 12.sp,
                                            lineHeight = 17.sp
                                        )
                                        Text(
                                            text = "ফিতরার হার প্রতি বছর পরিবর্তিত হতে পারে। বর্তমান বছরের সরকারি নির্ধারিত হার যাচাই করুন।",
                                            color = TextWhite,
                                            fontSize = 12.sp,
                                            lineHeight = 19.sp,
                                            fontWeight = FontWeight.SemiBold
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
