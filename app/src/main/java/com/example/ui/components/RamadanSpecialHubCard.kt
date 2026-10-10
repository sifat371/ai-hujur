package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RamadanSpecialDua
import com.example.data.repository.IslamicRepository
import com.example.data.service.HijriCalendarService
import com.example.data.service.PrayerTimeCalculatorService
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel
import java.util.Calendar
import java.util.Locale

/**
 * Ramadan Special Hub Card
 * Comprehensive facilities for the upcoming holy month of Ramadan:
 * - Accurate Sehri & Iftar timings based on location and Islamic Foundation Bangladesh
 * - Live countdowns
 * - Interactive Fast Tracker (synced with Room Database)
 * - Sehri & Iftar Masnoon Duas (Niyyah, Iftar, Post-Iftar, Tarabi, Laylatul Qadr)
 * - 30-Day timetable shortcut & Tarabi/Fitrah tools
 */
@Composable
fun RamadanSpecialHubCard(
    viewModel: AlHujurViewModel,
    onOpenFullRamadanSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLocation by viewModel.currentLocation.collectAsState()
    val todayAmal by viewModel.todayAmalRecord.collectAsState()
    val isFasting = todayAmal.fastingDone

    var showDuaDialog by remember { mutableStateOf(false) }
    var selectedDua by remember { mutableStateOf<RamadanSpecialDua?>(null) }
    var showTarabiDialog by remember { mutableStateOf(false) }
    var showFitrahDialog by remember { mutableStateOf(false) }

    // Accurate calculation for current location
    val calendar = Calendar.getInstance()
    val schedule = remember(currentLocation.latitude, currentLocation.longitude) {
        PrayerTimeCalculatorService.calculatePrayerTimes(
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            calendar = calendar,
            locationName = currentLocation.cityName
        )
    }

    val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
    val currentSeconds = currentMinutes * 60 + calendar.get(Calendar.SECOND)

    // Sehri end countdown
    val sehriDiffSeconds = (schedule.sehriEndMinutes * 60 - currentSeconds).let {
        if (it < 0) it + 86400 else it
    }
    val sehriHours = sehriDiffSeconds / 3600
    val sehriMins = (sehriDiffSeconds % 3600) / 60

    // Iftar countdown
    val iftarDiffSeconds = (schedule.iftarMinutes * 60 - currentSeconds).let {
        if (it < 0) it + 86400 else it
    }
    val iftarHours = iftarDiffSeconds / 3600
    val iftarMins = (iftarDiffSeconds % 3600) / 60

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .testTag("ramadan_special_hub_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = DeepNavy
        ),
        border = BorderStroke(1.5.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TOP HEADER: Ramadan Banner & District Selector Pill
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
                        color = Color(0x33D4AF37),
                        border = BorderStroke(1.dp, IslamicGold),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = "মাহে রমজান",
                                tint = BrightGold,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "সামনে মাহে রমজান ১৪৪৭",
                                color = BrightGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x2210B981)
                            ) {
                                Text(
                                    text = "বিশেষ সুবিধা",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${currentLocation.cityName} • ইসলামিক ফাউন্ডেশন মানদণ্ড",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // 30 Days schedule button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightGoldTint,
                    border = BorderStroke(1.dp, IslamicGold),
                    modifier = Modifier
                        .clickable { onOpenFullRamadanSchedule() }
                        .testTag("button_open_ramadan_30days_calendar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "৩০ দিনের সূচী",
                            color = BrightGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. HIGHLIGHTED SEHRI & IFTAR LIVE TIMINGS (Two Column Display)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Sehri Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = BorderStroke(1.2.dp, Color(0x33D4AF37))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "সাহরি শেষ সময়",
                                color = TextLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Outlined.WbTwilight,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = schedule.formatTimeBengali(schedule.sehriEndMinutes),
                            color = BrightGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x22D4AF37)
                        ) {
                            Text(
                                text = "বাকি ${HijriCalendarService.toBengaliDigits(sehriHours)} ঘ. ${HijriCalendarService.toBengaliDigits(sehriMins)} মি.",
                                color = TextLight,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "সতর্কতা: ৩ মি. পূর্বে",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Iftar Card
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF132247),
                    border = BorderStroke(1.2.dp, IslamicGold)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ইফতারের সময়",
                                color = BrightGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Outlined.DinnerDining,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = schedule.formatTimeBengali(schedule.iftarMinutes),
                            color = TextWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldContainer
                        ) {
                            Text(
                                text = "বাকি ${HijriCalendarService.toBengaliDigits(iftarHours)} ঘ. ${HijriCalendarService.toBengaliDigits(iftarMins)} মি.",
                                color = EmeraldSuccess,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "সূর্যাস্তের ২ মি. পর",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // 3. FASTING STATUS & INTERACTIVE FAST TRACKER (Room DB)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleFasting() }
                    .testTag("fasting_toggle_card"),
                shape = RoundedCornerShape(14.dp),
                color = if (isFasting) Color(0x2210B981) else NavySurface,
                border = BorderStroke(1.dp, if (isFasting) EmeraldSuccess else Color(0x33FFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isFasting) EmeraldSuccess else Color(0x22FFFFFF),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isFasting) Icons.Default.Check else Icons.Outlined.Circle,
                                    contentDescription = null,
                                    tint = if (isFasting) MidnightBlue else TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isFasting) "আজকের রোজা: আলহামদুলিল্লাহ সম্পন্ন / নিয়তকৃত" else "আজকের রোজা: রোজা সম্পন্ন হিসেবে চিহ্নিত করুন",
                                color = if (isFasting) EmeraldSuccess else TextLight,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ট্যাপ করে প্রতিদিনের রোজা ট্র্যাকিং আপডেট করুন",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    Text(
                        text = if (isFasting) "সম্পন্ন ✓" else "চিহ্নিত করুন",
                        color = if (isFasting) EmeraldSuccess else IslamicGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 4. SEHRI & IFTAR DUAS CHIPS (Instant Access)
            Text(
                text = "রমজানের প্রয়োজনীয় মাসনূন দোয়া ও আমল:",
                color = IslamicGold,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dua: Niyyah
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedDua = IslamicRepository.ramadanSpecialDuas.find { it.id == "dua_sehri_niyyah" }
                            showDuaDialog = true
                        }
                        .testTag("button_dua_sehri_niyyah"),
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurface,
                    border = BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "রোজার নিয়ত",
                            color = BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "সাহরির পর",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Dua: Iftar Time
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedDua = IslamicRepository.ramadanSpecialDuas.find { it.id == "dua_iftar_time" }
                            showDuaDialog = true
                        }
                        .testTag("button_dua_iftar_time"),
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurface,
                    border = BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "ইফতারের দোয়া",
                            color = BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ইফতারের শুরুতে",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Dua: After Iftar
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedDua = IslamicRepository.ramadanSpecialDuas.find { it.id == "dua_iftar_after" }
                            showDuaDialog = true
                        }
                        .testTag("button_dua_iftar_after"),
                    shape = RoundedCornerShape(10.dp),
                    color = NavySurface,
                    border = BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "ইফতারের পর",
                            color = BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "জাহাবায জামা'উ",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // 5. RAMADAN EXPANDED TOOLS: Tarabi Prayer Guide & Fitrah Calculator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tarabi Guide
                Button(
                    onClick = { showTarabiDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, Color(0x44D4AF37)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_open_tarabi_guide"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "তারাবীহ গাইড",
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Fitrah Calculator
                Button(
                    onClick = { showFitrahDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, Color(0x44D4AF37)),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_open_fitrah_calculator"),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolunteerActivism,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "সাদাকাতুল ফিতর",
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // --- POPUP DIALOGS ---

    // 1. Ramadan Dua Detail Dialog
    if (showDuaDialog && selectedDua != null) {
        val dua = selectedDua!!
        Dialog(onDismissRequest = { showDuaDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.5.dp, IslamicGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
                            text = dua.title,
                            color = BrightGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showDuaDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ", tint = TextLight)
                        }
                    }

                    Text(
                        text = "সময়: ${dua.occasion}",
                        color = EmeraldSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Arabic
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MidnightBlue,
                        border = BorderStroke(1.dp, Color(0x33D4AF37)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = dua.arabicText,
                            color = TextWhite,
                            fontSize = 18.sp,
                            lineHeight = 30.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    // Pronunciation
                    Text(
                        text = "উচ্চারণ: ${dua.pronunciationBn}",
                        color = TextLight,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    // Meaning
                    Text(
                        text = "অর্থ: \"${dua.meaningBn}\"",
                        color = BrightGold,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "রেফারেন্স: ${dua.reference}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                dua.title,
                                "${dua.title}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.pronunciationBn}\n\nঅর্থ: ${dua.meaningBn}\n[${dua.reference}]"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "দোয়া কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            showDuaDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LightGoldTint),
                        border = BorderStroke(1.dp, IslamicGold),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = BrightGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("দোয়া কপি করুন", color = BrightGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 2. Tarabi Guide Dialog
    if (showTarabiDialog) {
        Dialog(onDismissRequest = { showTarabiDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.5.dp, IslamicGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
                            text = "তারাবীহ নামাজের নিয়ম ও দোয়া",
                            color = BrightGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showTarabiDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ", tint = TextLight)
                        }
                    }

                    Text(
                        text = "• তারাবীহ নামাজ ২০ রাকাত সুন্নাতে মুয়াক্কাদা। ২ রাকাত করে ১০ সালামে আদায় করতে হয়।\n• প্রতি চার রাকাত পর কিছু সময় বিশ্রাম নেওয়া ও তাসবীহ পাঠ করা মুস্তাহাব।",
                        color = TextLight,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    // Tarabi Tasbeeh
                    val tarabiTasbeeh = IslamicRepository.ramadanSpecialDuas.find { it.id == "dua_tarabi_tasbeeh" }
                    if (tarabiTasbeeh != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NavySurface,
                            border = BorderStroke(1.dp, Color(0x33D4AF37)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "প্রতি চার রাকাত পর তাসবীহ:",
                                    color = BrightGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tarabiTasbeeh.arabicText,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.End,
                                    lineHeight = 24.sp
                                )
                                Text(
                                    text = tarabiTasbeeh.pronunciationBn,
                                    color = TextLight,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    // Tarabi Munajat
                    val tarabiMunajat = IslamicRepository.ramadanSpecialDuas.find { it.id == "dua_tarabi_munajat" }
                    if (tarabiMunajat != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NavySurface,
                            border = BorderStroke(1.dp, Color(0x33D4AF37)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "তারাবীহর মুনাজাত:",
                                    color = EmeraldSuccess,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tarabiMunajat.arabicText,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.End,
                                    lineHeight = 24.sp
                                )
                                Text(
                                    text = tarabiMunajat.meaningBn,
                                    color = TextLight,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // 3. Fitrah Guide Dialog
    if (showFitrahDialog) {
        Dialog(onDismissRequest = { showFitrahDialog = false }) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                border = BorderStroke(1.5.dp, IslamicGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
                            text = "সাদাকাতুল ফিতর গাইড ও হিসাব",
                            color = BrightGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showFitrahDialog = false }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ", tint = TextLight)
                        }
                    }

                    Text(
                        text = "সাদাকাতুল ফিতর প্রত্যেক সামর্থ্যবান মুসলমানের উপর ওয়াজিব। ঈদের নামাজের পূর্বে তা দরিদ্রদের মাঝে প্রদান করতে হয়।",
                        color = TextLight,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurface,
                        border = BorderStroke(1.dp, Color(0x33D4AF37)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "ইসলামিক ফাউন্ডেশন নির্ধারিত ফিতরার হার:",
                                color = BrightGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "১. আটা (১ কেজি ৬৫০ গ্রাম) = ১১৫ - ১৩০ টাকা (সর্বনিম্ন)\n২. যব (৩ কেজি ৩০০ গ্রাম) = ৪০০ - ৪৫০ টাকা\n৩. কিসমিস (৩ কেজি ৩০০ গ্রাম) = ১,৮০০ - ২,২০০ টাকা\n৪. খেজুর (৩ কেজি ৩০০ গ্রাম) = ২,০০০ - ২,৫০০ টাকা\n৫. পনির (৩ কেজি ৩০০ গ্রাম) = ২,৬০০ - ৩,০০০ টাকা (সর্বোচ্চ)",
                                color = TextWhite,
                                fontSize = 12.sp,
                                lineHeight = 19.sp
                            )
                            Text(
                                text = "সামর্থ্যবানদের উচিত সর্বোচ্চ মানের খাদ্যদ্রব্য দিয়ে ফিতরা আদায় করে গরিবের মুখে হাসি ফোটানো।",
                                color = EmeraldSuccess,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
