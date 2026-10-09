package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.DhikrLogEntity
import com.example.data.local.entity.QuranLogEntity
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun AmalScreen(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.amalProgress.collectAsState()
    val todayDhikrTotal by viewModel.todayDhikrTotal.collectAsState()
    val allTimeDhikrTotal by viewModel.allTimeDhikrTotal.collectAsState()
    val recentDhikrLogs by viewModel.recentDhikrLogs.collectAsState()
    val recentQuranLogs by viewModel.recentQuranLogs.collectAsState()
    val totalQuranPages by viewModel.totalQuranPages.collectAsState()

    var showLogQuranDialog by remember { mutableStateOf(false) }
    var showLogDhikrDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidnightBlue, DeepNavy, Color(0xFF070E22))
                )
            )
    ) {
        IslamicGeometricBackground(alpha = 0.035f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "দৈনিক আমল ট্র্যাকার",
                    color = IslamicGold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "দৈনিক পাঁচ ওয়াক্ত নামাজ, কুরআন তিলাওয়াত ও জিকির হিসাব রাখুন (লোকাল ডাটাবেজ সংরক্ষিত)",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            // Summary Card: Overall Progress (Salah, Quran, Dhikr)
            AmalSummaryCard(
                progressPercent = progress.overallProgressPercent(),
                prayersCompleted = progress.prayerCompletionCount(),
                quranPages = progress.quranPagesReadToday,
                dhikrTotal = todayDhikrTotal
            )

            // 1. SECTION: 5 Daily Prayers Checkboxes (Salah)
            DailyPrayersCard(
                fajrDone = progress.fajrDone,
                dhuhrDone = progress.dhuhrDone,
                asrDone = progress.asrDone,
                maghribDone = progress.maghribDone,
                ishaDone = progress.ishaDone,
                onTogglePrayer = { viewModel.togglePrayer(it) }
            )

            // 2. SECTION: Daily Quran Reading Progress (Quran recitation)
            QuranProgressCard(
                juz = progress.quranJuz,
                surah = progress.quranSurah,
                pagesRead = progress.quranPagesReadToday,
                goalPages = progress.quranDailyGoalPages,
                allTimePages = totalQuranPages,
                onAddPage = { viewModel.addQuranPages(1) },
                onLogDialog = { showLogQuranDialog = true }
            )

            // 3. SECTION: Daily Dhikr & Tasbeeh Tracker (Dhikr)
            DailyDhikrCard(
                todayTotal = todayDhikrTotal,
                allTimeTotal = allTimeDhikrTotal,
                recentLogs = recentDhikrLogs,
                onQuickDhikr = { name, count ->
                    viewModel.recordDhikrSession(dhikrName = name, count = count, target = count)
                },
                onOpenLogDialog = { showLogDhikrDialog = true }
            )

            // 4. SECTION: Ramadan Sunnah & Additional Deeds
            RamadanSunnahCard(
                fastingDone = progress.fastingToday,
                taraweehDone = progress.taraweehDone,
                tahajjudDone = progress.tahajjudDone,
                charityDone = progress.charityGiven,
                morningAdhkar = progress.morningAdhkarDone,
                eveningAdhkar = progress.eveningAdhkarDone,
                onToggle = { viewModel.togglePrayer(it) }
            )
        }
    }

    if (showLogQuranDialog) {
        LogQuranDialog(
            currentJuz = progress.quranJuz,
            currentSurah = progress.quranSurah,
            currentPages = progress.quranPagesReadToday,
            onDismiss = { showLogQuranDialog = false },
            onSave = { juz, surah, pages ->
                viewModel.setQuranProgress(juz, surah, pages)
                showLogQuranDialog = false
            }
        )
    }

    if (showLogDhikrDialog) {
        LogDhikrDialog(
            onDismiss = { showLogDhikrDialog = false },
            onSave = { name, count, target ->
                viewModel.recordDhikrSession(name, count = count, target = target)
                showLogDhikrDialog = false
            }
        )
    }
}

@Composable
private fun AmalSummaryCard(
    progressPercent: Float,
    prayersCompleted: Int,
    quranPages: Int,
    dhikrTotal: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("amal_summary_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "আজকের আমল স্কোর",
                        color = BrightGold,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "৫ ওয়াক্তের মধ্যে $prayersCompleted ওয়াক্ত ফরজ নামাজ সম্পন্ন",
                        color = TextLight,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "“আল্লাহর নিকট সর্বাধিক প্রিয় আমল তা-ই, যা নিয়মিত করা হয়”",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(NavySurface)
                        .border(2.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${(progressPercent * 100).toInt()}%",
                            color = BrightGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "স্কোর",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Mini statistics badge row: Salah, Quran, Dhikr (persisted locally)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x22D4AF37))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🕌 নামাজ", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "$prayersCompleted/৫ ওয়াক্ত",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldSuccess
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x22D4AF37))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📖 কুরআন", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "$quranPages পৃষ্ঠা",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrightGold
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x22D4AF37))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📿 জিকির", fontSize = 11.sp, color = TextMuted)
                        Text(
                            text = "$dhikrTotal বার",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = LightCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyPrayersCard(
    fajrDone: Boolean,
    dhuhrDone: Boolean,
    asrDone: Boolean,
    maghribDone: Boolean,
    ishaDone: Boolean,
    onTogglePrayer: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_prayers_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "৫ ওয়াক্ত ফরজ নামাজ",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LightGoldTint
                ) {
                    Text(
                        text = "ফরজ",
                        color = IslamicGold,
                        fontSize = 10.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Checkbox List for 5 Prayers
            PrayerCheckboxRow(
                name = "ফজর (ভোর)",
                arabic = "صلاة الفجر",
                checked = fajrDone,
                onCheckedChange = { onTogglePrayer("fajr") },
                testTag = "checkbox_prayer_fajr"
            )

            PrayerCheckboxRow(
                name = "যোহর (দুপুর)",
                arabic = "صلاة الظهر",
                checked = dhuhrDone,
                onCheckedChange = { onTogglePrayer("dhuhr") },
                testTag = "checkbox_prayer_dhuhr"
            )

            PrayerCheckboxRow(
                name = "আসর (বিকাল)",
                arabic = "صلاة العصر",
                checked = asrDone,
                onCheckedChange = { onTogglePrayer("asr") },
                testTag = "checkbox_prayer_asr"
            )

            PrayerCheckboxRow(
                name = "মাগরিব (সন্ধ্যা)",
                arabic = "صلاة المغرب",
                checked = maghribDone,
                onCheckedChange = { onTogglePrayer("maghrib") },
                testTag = "checkbox_prayer_maghrib"
            )

            PrayerCheckboxRow(
                name = "এশা (রাত)",
                arabic = "صلاة العشاء",
                checked = ishaDone,
                onCheckedChange = { onTogglePrayer("isha") },
                testTag = "checkbox_prayer_isha"
            )
        }
    }
}

@Composable
private fun PrayerCheckboxRow(
    name: String,
    arabic: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (checked) Color(0xFF132E4B) else NavySurface,
        border = BorderStroke(
            1.dp,
            if (checked) EmeraldSuccess else Color(0x22D4AF37)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onCheckedChange() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = EmeraldSuccess,
                        checkmarkColor = MidnightBlue,
                        uncheckedColor = IslamicGold
                    ),
                    modifier = Modifier.testTag(testTag)
                )

                Column {
                    Text(
                        text = name,
                        color = if (checked) EmeraldSuccess else TextWhite,
                        fontSize = 14.5.sp,
                        fontWeight = if (checked) FontWeight.Bold else FontWeight.Medium
                    )
                    Text(
                        text = arabic,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (checked) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EmeraldContainer
                ) {
                    Text(
                        text = "আদায় সম্পন্ন",
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

// -------------------------------------------------------------
// 2. SECTION: Daily Quran Reading Progress
// -------------------------------------------------------------
@Composable
private fun QuranProgressCard(
    juz: Int,
    surah: String,
    pagesRead: Int,
    goalPages: Int,
    allTimePages: Int = 0,
    onAddPage: () -> Unit,
    onLogDialog: () -> Unit
) {
    val progressRatio = (pagesRead.toFloat() / goalPages.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quran_progress_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "দৈনিক কুরআন তিলাওয়াত",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightGoldTint,
                    border = BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier.clickable { onLogDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "হালনাগাদ করুন",
                            color = IslamicGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Current Reading Bookmark info
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavySurface,
                border = BorderStroke(1.dp, Color(0x22D4AF37))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "বর্তমান বুকমার্ক",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = surah,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LightGoldTint
                    ) {
                        Text(
                            text = "পারা $juz",
                            color = BrightGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Reading Progress Bar
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "আজকের পঠিত পৃষ্ঠা: $pagesRead / $goalPages",
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${(progressRatio * 100).toInt()}%",
                        color = BrightGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = IslamicGold,
                    trackColor = Color(0x33D4AF37)
                )
            }

            if (allTimePages > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সর্বমোট পঠিত পৃষ্ঠা:",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$allTimePages পৃষ্ঠা (ডাটাবেজ সংরক্ষিত)",
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Quick Add Page Button
            OutlinedButton(
                onClick = onAddPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_quran_page_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold),
                border = BorderStroke(1.dp, GoldBorder)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("আরো ১ পৃষ্ঠা পড়েছি (+১)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SECTION: Ramadan Sunnah Deeds
// -------------------------------------------------------------
@Composable
private fun RamadanSunnahCard(
    fastingDone: Boolean,
    taraweehDone: Boolean,
    tahajjudDone: Boolean,
    charityDone: Boolean,
    morningAdhkar: Boolean,
    eveningAdhkar: Boolean,
    onToggle: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ramadan_sunnah_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = BrightGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "রমজানের সুন্নত ও নেক আমল",
                    color = BrightGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            SunnahCheckboxItem(
                title = "আজকের রোজা (সওম)",
                subtitle = "সুন্নাহ মেনে সংযমের সাথে রোজা পালন",
                checked = fastingDone,
                onCheckedChange = { onToggle("fasting") },
                testTag = "checkbox_fasting"
            )

            SunnahCheckboxItem(
                title = "তারাবীহ নামাজ",
                subtitle = "রমজানের বিশেষ নৈশ নামাজ",
                checked = taraweehDone,
                onCheckedChange = { onToggle("taraweeh") },
                testTag = "checkbox_taraweeh"
            )

            SunnahCheckboxItem(
                title = "তাহাজ্জুদ ও নফল সালাত",
                subtitle = "রাতের শেষ তৃতীয়াংশে খাস ইবাদত",
                checked = tahajjudDone,
                onCheckedChange = { onToggle("tahajjud") },
                testTag = "checkbox_tahajjud"
            )

            SunnahCheckboxItem(
                title = "দান-সাদাকাহ",
                subtitle = "গরিব-অসহায়কে দান বা ভালো আচরণ",
                checked = charityDone,
                onCheckedChange = { onToggle("charity") },
                testTag = "checkbox_charity"
            )

            SunnahCheckboxItem(
                title = "সকাল ও সন্ধ্যার মাসনূন জিকির",
                subtitle = "আল্লাহর স্মরণ ও হেফাজতের দোয়া",
                checked = morningAdhkar && eveningAdhkar,
                onCheckedChange = {
                    onToggle("morning_adhkar")
                    onToggle("evening_adhkar")
                },
                testTag = "checkbox_adhkar"
            )
        }
    }
}

@Composable
private fun SunnahCheckboxItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NavySurface,
        border = BorderStroke(1.dp, if (checked) EmeraldSuccess else Color(0x22D4AF37)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
                colors = CheckboxDefaults.colors(
                    checkedColor = EmeraldSuccess,
                    checkmarkColor = MidnightBlue,
                    uncheckedColor = IslamicGold
                ),
                modifier = Modifier.testTag(testTag)
            )

            Column {
                Text(
                    text = title,
                    color = if (checked) EmeraldSuccess else TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun LogQuranDialog(
    currentJuz: Int,
    currentSurah: String,
    currentPages: Int,
    onDismiss: () -> Unit,
    onSave: (Int, String, Int) -> Unit
) {
    var juz by remember { mutableStateOf(currentJuz.toString()) }
    var surah by remember { mutableStateOf(currentSurah) }
    var pages by remember { mutableStateOf(currentPages.toString()) }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "কুরআন তিলাওয়াত আপডেট",
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                OutlinedTextField(
                    value = surah,
                    onValueChange = { surah = it },
                    label = { Text("সুরার নাম", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                OutlinedTextField(
                    value = juz,
                    onValueChange = { juz = it },
                    label = { Text("পারা নম্বর (১ - ৩০)", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                OutlinedTextField(
                    value = pages,
                    onValueChange = { pages = it },
                    label = { Text("আজকের পঠিত পৃষ্ঠা সংখ্যা", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                Button(
                    onClick = {
                        val j = juz.toIntOrNull() ?: currentJuz
                        val p = pages.toIntOrNull() ?: currentPages
                        onSave(j, surah.ifBlank { currentSurah }, p)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SECTION: Daily Dhikr & Tasbeeh Tracker (Room Database)
// -------------------------------------------------------------
@Composable
private fun DailyDhikrCard(
    todayTotal: Int,
    allTimeTotal: Int,
    recentLogs: List<DhikrLogEntity>,
    onQuickDhikr: (name: String, count: Int) -> Unit,
    onOpenLogDialog: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_dhikr_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
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
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = null,
                        tint = LightCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "দৈনিক জিকির ও তাসবীহ",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightGoldTint,
                    border = BorderStroke(1.dp, GoldBorder),
                    modifier = Modifier.clickable { onOpenLogDialog() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "কাস্টম জিকির",
                            color = IslamicGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Stats row
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavySurface,
                border = BorderStroke(1.dp, Color(0x22D4AF37))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "আজকের মোট জিকির",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$todayTotal বার",
                            color = LightCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "সর্বমোট জিকির (ডাটাবেজ)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "$allTimeTotal বার",
                            color = BrightGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Quick 1-tap buttons
            Text(
                text = "১-ট্যাপে দ্রুত জিকির যোগ করুন:",
                color = TextLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickDhikrChip(
                    label = "সুবহানাল্লাহ",
                    count = 33,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDhikr("সুবহানাল্লাহ", 33) }
                )
                QuickDhikrChip(
                    label = "আলহামদুলিল্লাহ",
                    count = 33,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDhikr("আলহামদুলিল্লাহ", 33) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickDhikrChip(
                    label = "আল্লাহু আকবার",
                    count = 34,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDhikr("আল্লাহু আকবার", 34) }
                )
                QuickDhikrChip(
                    label = "আস্তাগফিরুল্লাহ",
                    count = 100,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDhikr("আস্তাগফিরুল্লাহ", 100) }
                )
            }

            // Recent Logs preview if any
            if (recentLogs.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "আজকের জিকির সেশন (${recentLogs.size}টি সম্পন্ন)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    recentLogs.take(4).forEach { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x22132E4B))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = log.dhikrName,
                                color = TextWhite,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${log.count} বার",
                                color = LightCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickDhikrChip(
    label: String,
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = NavySurface,
        border = BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = LightGoldTint
            ) {
                Text(
                    text = "+$count",
                    color = BrightGold,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun LogDhikrDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, count: Int, target: Int) -> Unit
) {
    var dhikrName by remember { mutableStateOf("লা ইলাহা ইল্লাল্লাহ") }
    var countText by remember { mutableStateOf("100") }
    var targetText by remember { mutableStateOf("100") }

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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "নতুন জিকির সেশন সংরক্ষণ",
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                OutlinedTextField(
                    value = dhikrName,
                    onValueChange = { dhikrName = it },
                    label = { Text("জিকির বা দোয়ার নাম", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                OutlinedTextField(
                    value = countText,
                    onValueChange = { countText = it },
                    label = { Text("কতবার পাঠ করেছেন (সংখ্যা)", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("টার্গেট (যেমন: ৩৩, ১০০)", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                Button(
                    onClick = {
                        val c = countText.toIntOrNull() ?: 33
                        val t = targetText.toIntOrNull() ?: 33
                        onSave(dhikrName.ifBlank { "জিকির" }, c, t)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ডাটাবেজে সংরক্ষণ করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
