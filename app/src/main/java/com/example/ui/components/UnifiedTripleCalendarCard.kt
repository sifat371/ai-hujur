package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.data.model.HijriDateInfo
import com.example.data.model.IslamicEvent
import com.example.data.service.BanglaCalendarService
import com.example.data.service.BanglaDateInfo
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Unified Triple Calendar Card displaying Arabic (Hijri), Bengali (Bongabdo),
 * and English (Gregorian) dates, day of the week, and month in one clean,
 * auto-updating, master calendar section.
 */
@Composable
fun UnifiedTripleCalendarCard(
    hijriDate: HijriDateInfo,
    banglaDate: BanglaDateInfo,
    nextEvent: IslamicEvent?,
    onOpenCalendar: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenEvents: (() -> Unit)? = null
) {
    // Current Gregorian Date
    val now = remember { Date() }
    val dayOfWeekBn = hijriDate.dayOfWeekBn
    val gregorianDay = SimpleDateFormat("d", Locale.US).format(now).toIntOrNull() ?: 23
    val gregorianDayBn = HijriCalendarService.toBengaliDigits(gregorianDay)
    val gregorianMonthEn = SimpleDateFormat("MMMM", Locale.US).format(now)
    val gregorianMonthBn = when (SimpleDateFormat("M", Locale.US).format(now)) {
        "1" -> "জানুয়ারি"
        "2" -> "ফেব্রুয়ারি"
        "3" -> "মার্চ"
        "4" -> "এপ্রিল"
        "5" -> "মে"
        "6" -> "জুন"
        "7" -> "জুলাই"
        "8" -> "আগস্ট"
        "9" -> "সেপ্টেম্বর"
        "10" -> "অক্টোবর"
        "11" -> "নভেম্বর"
        "12" -> "ডিসেম্বর"
        else -> "সেপ্টেম্বর"
    }
    val gregorianYear = SimpleDateFormat("yyyy", Locale.US).format(now).toIntOrNull() ?: 2026
    val gregorianYearBn = HijriCalendarService.toBengaliDigits(gregorianYear)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp))
            .clickable { onOpenCalendar() }
            .testTag("unified_triple_calendar_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.2.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- TOP TITLE ROW: Live Indicator, Day of Week & Action Button ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(IslamicGold.copy(alpha = 0.25f), BrightGold.copy(alpha = 0.1f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = "আজ $dayOfWeekBn",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            // Live auto-updating pulsing indicator
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                border = BorderStroke(0.6.dp, EmeraldSuccess)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FiberManualRecord,
                                        contentDescription = null,
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(7.dp)
                                    )
                                    Text(
                                        text = "অটো-আপডেট",
                                        color = EmeraldSuccess,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "সম্মিলিত দিন-পঞ্জিকা • ৩টি ক্যালেন্ডার একনজরে",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(0.8.dp, GoldBorder),
                    modifier = Modifier.clickable { onOpenCalendar() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "ক্যালেন্ডার",
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            // --- THREE UNIFIED CALENDAR PILLARS: ARABIC, BANGLA, GREGORIAN ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. ARABIC (HIJRI) PILLAR
                DatePillarCard(
                    modifier = Modifier.weight(1f),
                    badgeTitle = "আরবি (হিজরি)",
                    badgeColor = IslamicGold,
                    icon = Icons.Outlined.NightsStay,
                    datePrimary = "${HijriCalendarService.toBengaliDigits(hijriDate.day)} ${hijriDate.monthNameBn}",
                    yearSecondary = "${HijriCalendarService.toBengaliDigits(hijriDate.year)} হিজরি",
                    extraTag = hijriDate.monthNameAr,
                    testTag = "pillar_hijri_date"
                )

                // 2. BANGLA (BONGABDO) PILLAR
                DatePillarCard(
                    modifier = Modifier.weight(1f),
                    badgeTitle = "বাংলা (সন)",
                    badgeColor = BrightGold,
                    icon = Icons.Outlined.WbSunny,
                    datePrimary = "${BanglaCalendarService.toBengaliDigits(banglaDate.day)} ${banglaDate.monthName}",
                    yearSecondary = "${BanglaCalendarService.toBengaliDigits(banglaDate.year)} বঙ্গাব্দ",
                    extraTag = banglaDate.season,
                    testTag = "pillar_bangla_date"
                )

                // 3. ENGLISH (GREGORIAN) PILLAR
                DatePillarCard(
                    modifier = Modifier.weight(1f),
                    badgeTitle = "ইংরেজি (ঈসায়ী)",
                    badgeColor = Color(0xFF60A5FA),
                    icon = Icons.Outlined.CalendarMonth,
                    datePrimary = "$gregorianDayBn $gregorianMonthBn",
                    yearSecondary = "$gregorianYearBn খ্রিস্টাব্দ",
                    extraTag = "$gregorianMonthEn $gregorianYear",
                    testTag = "pillar_gregorian_date"
                )
            }

            // --- UPCOMING ISLAMIC EVENT BANNER (If present) ---
            if (nextEvent != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x18D4AF37),
                    border = BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { (onOpenEvents ?: onOpenCalendar).invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "আসন্ন: ${nextEvent.titleBn}",
                                color = BrightGold,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${HijriCalendarService.toBengaliDigits(nextEvent.daysRemaining.toInt())} দিন বাকি",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual column card for one calendar system (Arabic, Bangla, or English).
 */
@Composable
private fun DatePillarCard(
    modifier: Modifier = Modifier,
    badgeTitle: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    datePrimary: String,
    yearSecondary: String,
    extraTag: String,
    testTag: String
) {
    Surface(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        color = NavySurface,
        border = BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Pillar Badge Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = badgeTitle,
                    color = badgeColor,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Primary Day & Month
            Text(
                text = datePrimary,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            // Secondary Year
            Text(
                text = yearSecondary,
                color = IslamicGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            // Extra Tag (Arabic Script / Season / En Month)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0x18FFFFFF),
                modifier = Modifier.padding(top = 2.dp)
            ) {
                Text(
                    text = extraTag,
                    color = TextMuted,
                    fontSize = 9.5.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }
    }
}

/**
 * Space-Saving Unified Card:
 * 1. Top Section: 3-in-1 Triple Calendar (Hijri, Bangla, Gregorian) stays FIXED & visible.
 * 2. Bottom Section: Upcoming Islamic Special Days slide through one after another
 *    (নিচ দিয়ে বিশেষ দিনগুলো স্লাইড আকারে একটার পর একটা আসবে যাবে) with live countdown.
 */
@Composable
fun UnifiedCalendarEventsSliderCard(
    hijriDate: HijriDateInfo,
    banglaDate: BanglaDateInfo,
    upcomingEvents: List<IslamicEvent>,
    onOpenCalendar: () -> Unit,
    onOpenEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = remember { Date() }
    val dayOfWeekBn = hijriDate.dayOfWeekBn
    val gregorianDay = SimpleDateFormat("d", Locale.US).format(now).toIntOrNull() ?: 23
    val gregorianDayBn = HijriCalendarService.toBengaliDigits(gregorianDay)
    val gregorianMonthBn = when (SimpleDateFormat("M", Locale.US).format(now)) {
        "1" -> "জানুয়ারি"
        "2" -> "ফেব্রুয়ারি"
        "3" -> "মার্চ"
        "4" -> "এপ্রিল"
        "5" -> "মে"
        "6" -> "জুন"
        "7" -> "জুলাই"
        "8" -> "আগস্ট"
        "9" -> "সেপ্টেম্বর"
        "10" -> "অক্টোবর"
        "11" -> "নভেম্বর"
        "12" -> "ডিসেম্বর"
        else -> "সেপ্টেম্বর"
    }
    val gregorianYear = SimpleDateFormat("yyyy", Locale.US).format(now).toIntOrNull() ?: 2026
    val gregorianYearBn = HijriCalendarService.toBengaliDigits(gregorianYear)

    // Upcoming Islamic events in sequential slides (up to 8 events)
    val eventSlides = remember(upcomingEvents) {
        if (upcomingEvents.isNotEmpty()) upcomingEvents.take(8) else emptyList()
    }
    val pagerState = rememberPagerState(pageCount = { if (eventSlides.isNotEmpty()) eventSlides.size else 1 })
    val coroutineScope = rememberCoroutineScope()

    // Smooth auto-sliding for special days ticker every 4.5 seconds (pauses when user interacts)
    LaunchedEffect(eventSlides.size) {
        if (eventSlides.size > 1) {
            while (true) {
                delay(4500L)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % eventSlides.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(22.dp))
            .testTag("unified_calendar_events_slider_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.2.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // =========================================================================
            // 1. TOP FIXED SECTION: 3-IN-1 TRIPLE CALENDAR (স্থির ও সর্বদা দৃশ্যমান)
            // =========================================================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCalendar() },
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row: Today's Day + Live Auto-Update Badge + Full Calendar Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(IslamicGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = "আজ $dayOfWeekBn",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(5.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            border = BorderStroke(0.6.dp, EmeraldSuccess)
                        ) {
                            Text(
                                text = "লাইভ ক্যালেন্ডার",
                                color = EmeraldSuccess,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NavySurface,
                        border = BorderStroke(0.7.dp, GoldBorder),
                        modifier = Modifier.clickable { onOpenCalendar() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "সম্পূর্ণ পঞ্জিকা",
                                color = BrightGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                // 3 Fixed Calendar Badges Side-by-Side: Arabic, Bangla, English
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // 1. ARABIC (HIJRI)
                    CompactDateBadge(
                        modifier = Modifier.weight(1f),
                        badge = "হিজরি",
                        badgeColor = IslamicGold,
                        icon = Icons.Outlined.NightsStay,
                        primaryDate = "${HijriCalendarService.toBengaliDigits(hijriDate.day)} ${hijriDate.monthNameBn}",
                        secondaryInfo = "${HijriCalendarService.toBengaliDigits(hijriDate.year)} হিজরি"
                    )

                    // 2. BANGLA (BONGABDO)
                    CompactDateBadge(
                        modifier = Modifier.weight(1f),
                        badge = "বাংলা",
                        badgeColor = BrightGold,
                        icon = Icons.Outlined.WbSunny,
                        primaryDate = "${BanglaCalendarService.toBengaliDigits(banglaDate.day)} ${banglaDate.monthName}",
                        secondaryInfo = "${BanglaCalendarService.toBengaliDigits(banglaDate.year)} সন (${banglaDate.season})"
                    )

                    // 3. ENGLISH (GREGORIAN)
                    CompactDateBadge(
                        modifier = Modifier.weight(1f),
                        badge = "ইংরেজি",
                        badgeColor = Color(0xFF60A5FA),
                        icon = Icons.Outlined.CalendarMonth,
                        primaryDate = "$gregorianDayBn $gregorianMonthBn",
                        secondaryInfo = "$gregorianYearBn খ্রিস্টাব্দ"
                    )
                }
            }

            // Divider separating Fixed Calendar and Sliding Special Days
            HorizontalDivider(
                color = Color(0x33D4AF37),
                thickness = 0.8.dp,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // =========================================================================
            // 2. BOTTOM SLIDING SECTION: SPECIAL DAYS CAROUSEL (স্লাইড আকারে পর পর প্রদর্শন)
            // =========================================================================
            if (eventSlides.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->
                        val event = eventSlides.getOrNull(page)
                        if (event != null) {
                            EventSlideContent(
                                event = event,
                                eventIndex = page + 1,
                                totalEvents = eventSlides.size,
                                onOpenEvents = onOpenEvents
                            )
                        }
                    }

                    // Bottom Navigation Row: Indicator Dots + Current Event Indicator + Left/Right Chevrons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, start = 2.dp, end = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated Dots Indicator
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(eventSlides.size) { index ->
                                val isSelected = pagerState.currentPage == index
                                Box(
                                    modifier = Modifier
                                        .height(4.dp)
                                        .width(if (isSelected) 16.dp else 4.5.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (isSelected) BrightGold else IslamicGold.copy(alpha = 0.3f)
                                        )
                                        .clickable {
                                            coroutineScope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        }
                                )
                            }
                        }

                        // Current Event Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x18D4AF37),
                            border = BorderStroke(0.6.dp, GoldBorder.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "আসন্ন বিশেষ দিবস (${HijriCalendarService.toBengaliDigits(pagerState.currentPage + 1)}/${HijriCalendarService.toBengaliDigits(eventSlides.size)})",
                                color = BrightGold,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.5.dp)
                            )
                        }

                        // Left & Right Arrow Buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val prevPage = if (pagerState.currentPage > 0) pagerState.currentPage - 1 else eventSlides.size - 1
                                        pagerState.animateScrollToPage(prevPage)
                                    }
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "আগের দিবস",
                                    tint = IslamicGold,
                                    modifier = Modifier.size(11.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        val nextPage = (pagerState.currentPage + 1) % eventSlides.size
                                        pagerState.animateScrollToPage(nextPage)
                                    }
                                },
                                modifier = Modifier.size(22.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "পরের দিবস",
                                    tint = IslamicGold,
                                    modifier = Modifier.size(11.dp)
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
private fun CalendarSlideContent(
    dayOfWeekBn: String,
    hijriDate: HijriDateInfo,
    banglaDate: BanglaDateInfo,
    gregorianDayBn: String,
    gregorianMonthBn: String,
    gregorianYearBn: String,
    onOpenCalendar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenCalendar() }
            .padding(bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // Slide Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(IslamicGold.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "আজ $dayOfWeekBn",
                    color = TextWhite,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = EmeraldSuccess.copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, EmeraldSuccess)
                ) {
                    Text(
                        text = "লাইভ পঞ্জিকা",
                        color = EmeraldSuccess,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavySurface,
                border = BorderStroke(0.6.dp, GoldBorder),
                modifier = Modifier.clickable { onOpenCalendar() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "পঞ্জিকা",
                        color = BrightGold,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        // 3 Compact Date Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CompactDateBadge(
                modifier = Modifier.weight(1f),
                badge = "হিজরি",
                badgeColor = IslamicGold,
                icon = Icons.Outlined.NightsStay,
                primaryDate = "${HijriCalendarService.toBengaliDigits(hijriDate.day)} ${hijriDate.monthNameBn}",
                secondaryInfo = "${HijriCalendarService.toBengaliDigits(hijriDate.year)} হিজরি"
            )

            CompactDateBadge(
                modifier = Modifier.weight(1f),
                badge = "বাংলা",
                badgeColor = BrightGold,
                icon = Icons.Outlined.WbSunny,
                primaryDate = "${BanglaCalendarService.toBengaliDigits(banglaDate.day)} ${banglaDate.monthName}",
                secondaryInfo = "${BanglaCalendarService.toBengaliDigits(banglaDate.year)} সন (${banglaDate.season})"
            )

            CompactDateBadge(
                modifier = Modifier.weight(1f),
                badge = "ইংরেজি",
                badgeColor = Color(0xFF60A5FA),
                icon = Icons.Outlined.CalendarMonth,
                primaryDate = "$gregorianDayBn $gregorianMonthBn",
                secondaryInfo = "$gregorianYearBn খ্রিস্টাব্দ"
            )
        }
    }
}

@Composable
private fun EventSlideContent(
    event: IslamicEvent,
    eventIndex: Int,
    totalEvents: Int,
    onOpenEvents: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenEvents() }
            .padding(bottom = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Event Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            if (event.isToday) EmeraldSuccess.copy(alpha = 0.25f) else IslamicGold.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (event.isToday) Icons.Default.Stars else Icons.Outlined.EventAvailable,
                        contentDescription = null,
                        tint = if (event.isToday) EmeraldSuccess else BrightGold,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (event.isToday) EmeraldSuccess.copy(alpha = 0.2f) else Color(0x22D4AF37)
                ) {
                    Text(
                        text = if (event.isToday) "আজকের পবিত্র দিন" else "ইসলামের বিশেষ দিন (${HijriCalendarService.toBengaliDigits(eventIndex)}/${HijriCalendarService.toBengaliDigits(totalEvents)})",
                        color = if (event.isToday) EmeraldSuccess else BrightGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavySurface,
                border = BorderStroke(0.6.dp, GoldBorder),
                modifier = Modifier.clickable { onOpenEvents() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "সকল দিবস",
                        color = IslamicGold,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }

        // Center Row: Title & Dates on Left, Countdown Badge on Right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = event.titleBn,
                    color = TextWhite,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${HijriCalendarService.toBengaliDigits(event.hijriDay)} ${event.hijriMonthNameBn} • ${event.estimatedGregorianDateBn}",
                    color = IslamicGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                if (event.significance.isNotBlank()) {
                    Text(
                        text = event.significance,
                        color = TextLight,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Prominent Countdown Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when {
                    event.isToday -> EmeraldSuccess
                    event.daysRemaining <= 7 -> BrightGold
                    else -> Color(0x33D4AF37)
                },
                border = BorderStroke(
                    0.8.dp,
                    if (event.isToday || event.daysRemaining <= 7) Color.Transparent else IslamicGold.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    val isLightBg = event.isToday || event.daysRemaining <= 7
                    Text(
                        text = when {
                            event.isToday -> "আজ পবিত্র দিন!"
                            event.daysRemaining == 1L -> "আগামীকাল"
                            else -> "আর ${HijriCalendarService.toBengaliDigits(event.daysRemaining.toInt())} দিন"
                        },
                        color = if (isLightBg) DeepNavy else BrightGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (!event.isToday && event.daysRemaining != 1L) {
                        Text(
                            text = "বাকি আছে",
                            color = if (isLightBg) DeepNavy.copy(alpha = 0.8f) else TextMuted,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactDateBadge(
    modifier: Modifier = Modifier,
    badge: String,
    badgeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryDate: String,
    secondaryInfo: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = NavySurface,
        border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(10.dp)
                )
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = primaryDate,
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = secondaryInfo,
                color = IslamicGold,
                fontSize = 8.5.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

