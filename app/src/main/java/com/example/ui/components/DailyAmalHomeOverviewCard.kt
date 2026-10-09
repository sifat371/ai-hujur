package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

/**
 * Modern, interactive Daily Amal Tracker Overview Card displayed on the Home Screen.
 * Connected directly with the local Room Database through [AlHujurViewModel].
 * Allows quick-logging of prayers, Quran reading progress, and Dhikr counts with instant reactivity.
 */
@Composable
fun DailyAmalHomeOverviewCard(
    viewModel: AlHujurViewModel,
    onNavigateToAmal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.amalProgress.collectAsState()
    val todayDhikrTotal by viewModel.todayDhikrTotal.collectAsState()

    val completedPrayers = progress.prayerCompletionCount()
    val overallPercent = progress.overallProgressPercent()
    val percentInt = (overallPercent * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_amal_overview_card"),
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
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .background(
                                Brush.linearGradient(
                                    listOf(IslamicGold.copy(alpha = 0.25f), BrightGold.copy(alpha = 0.1f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "দৈনিক আমল ট্র্যাকার",
                            color = BrightGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "লোকাল রুম (Room) ডাটাবেজে সংরক্ষিত",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // Navigate to full tracker button
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x33D4AF37)),
                    modifier = Modifier.clickable { onNavigateToAmal() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "বিস্তারিত",
                            color = IslamicGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "বিস্তারিত দেখুন",
                            tint = IslamicGold,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }

            // Progress Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavySurface,
                border = BorderStroke(0.5.dp, Color(0x22FFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "আজকের মোট অগ্রগতি:",
                                color = TextLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$percentInt%",
                                color = if (percentInt >= 80) EmeraldSuccess else BrightGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { overallPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (percentInt >= 80) EmeraldSuccess else IslamicGold,
                            trackColor = Color(0x33203A43)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Mini completion badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (percentInt >= 100) EmeraldSuccess.copy(alpha = 0.2f)
                                else IslamicGold.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$completedPrayers/৫",
                            color = if (percentInt >= 100) EmeraldSuccess else BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 1. Quick Salah Toggles (Fajr, Dhuhr, Asr, Maghrib, Isha)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "৫ ওয়াক্ত নামাজ কুইক চেক (ট্যাপ করে আপডেট করুন):",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val prayers = listOf(
                        Triple("ফজর", progress.fajrDone, "fajr"),
                        Triple("যোহর", progress.dhuhrDone, "dhuhr"),
                        Triple("আসর", progress.asrDone, "asr"),
                        Triple("মাগরিব", progress.maghribDone, "maghrib"),
                        Triple("এশা", progress.ishaDone, "isha")
                    )

                    prayers.forEach { (name, isDone, key) ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.togglePrayer(key) }
                                .testTag("quick_prayer_$key"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDone) EmeraldSuccess.copy(alpha = 0.18f) else NavySurface,
                            border = BorderStroke(
                                1.dp,
                                if (isDone) EmeraldSuccess else Color(0x33D4AF37)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDone) EmeraldSuccess else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDone) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = DeepNavy,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = name,
                                    color = if (isDone) EmeraldSuccess else TextLight,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            // 2. Quick Mini Metrics: Quran & Dhikr Loggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quran Reading Progress Item
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToAmal() },
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x22D4AF37))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "কুরআন তিলাওয়াত",
                                    color = TextLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${progress.quranPagesReadToday} পৃষ্ঠা (${progress.quranSurah.take(12)})",
                                    color = BrightGold,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // 1-Tap quick add page button
                        IconButton(
                            onClick = { viewModel.addQuranPages(1) },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IslamicGold.copy(alpha = 0.2f))
                                .testTag("home_add_quran_page_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "১ পৃষ্ঠা যোগ করুন",
                                tint = BrightGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Dhikr Count Item
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToAmal() },
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x22D4AF37))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SelfImprovement,
                                contentDescription = null,
                                tint = LightCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "দৈনিক জিকির",
                                    color = TextLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$todayDhikrTotal বার জপ",
                                    color = LightCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // 1-Tap quick +33 Tasbeeh log
                        IconButton(
                            onClick = {
                                viewModel.recordDhikrSession(
                                    dhikrName = "সুবহানাল্লাহ (কুইক)",
                                    count = 33,
                                    target = 33
                                )
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(LightCyan.copy(alpha = 0.18f))
                                .testTag("home_quick_dhikr_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "+৩৩ জিকির",
                                tint = LightCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
