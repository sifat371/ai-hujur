package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HijriCalendarDay
import com.example.data.model.IslamicEvent
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun HijriCalendarDialog(
    viewModel: AlHujurViewModel,
    onDismiss: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("মাসিক ক্যালেন্ডার", "গুরুত্বপূর্ণ দিবসসমূহ", "তারিখ কনভার্টার")

    val currentHijriDate by viewModel.currentHijriDate.collectAsState()
    val monthData by viewModel.hijriMonthData.collectAsState()
    val upcomingEvents by viewModel.upcomingIslamicEvents.collectAsState()
    val offsetDays by viewModel.hijriOffsetDays.collectAsState()

    var selectedDay by remember { mutableStateOf<HijriCalendarDay?>(null) }

    // Auto-select today on launch
    LaunchedEffect(monthData) {
        if (selectedDay == null || selectedDay?.hijriMonth != monthData.month) {
            selectedDay = monthData.days.find { it.isToday } ?: monthData.days.firstOrNull()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("hijri_calendar_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Top Header Row
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
                                .size(40.dp)
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
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "হিজরি ক্যালেন্ডার ও দিবস",
                                color = BrightGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentHijriDate.formattedHijriBn,
                                color = TextLight.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_hijri_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Switcher
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = NavyCard,
                    contentColor = BrightGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = IslamicGold
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTabIndex == index) BrightGold else TextMuted
                                )
                            },
                            modifier = Modifier.testTag("hijri_tab_$index")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                when (selectedTabIndex) {
                    0 -> MonthlyHijriCalendarView(
                        monthData = monthData,
                        offsetDays = offsetDays,
                        selectedDay = selectedDay,
                        onDaySelected = { selectedDay = it },
                        onPreviousMonth = { viewModel.previousHijriMonth() },
                        onNextMonth = { viewModel.nextHijriMonth() },
                        onResetToday = { viewModel.resetToCurrentHijriMonth() },
                        onSetOffset = { viewModel.setHijriOffset(it) }
                    )
                    1 -> SignificantIslamicEventsView(
                        events = upcomingEvents
                    )
                    2 -> HijriDateConverterView(
                        offsetDays = offsetDays
                    )
                }
            }
        }
    }
}

// =========================================================================
// TAB 1: MONTHLY HIJRI CALENDAR VIEW
// =========================================================================
@Composable
private fun MonthlyHijriCalendarView(
    monthData: com.example.data.model.HijriMonthData,
    offsetDays: Int,
    selectedDay: HijriCalendarDay?,
    onDaySelected: (HijriCalendarDay) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToday: () -> Unit,
    onSetOffset: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Month Navigation Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NavyCard,
                border = BorderStroke(1.dp, GoldBorder),
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
                        IconButton(
                            onClick = onPreviousMonth,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NavySurface)
                                .testTag("prev_hijri_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "পূর্ববর্তী মাস",
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "${monthData.monthNameBn} ${HijriCalendarService.toBengaliDigits(monthData.year)} হিজরি",
                                    color = BrightGold,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "(${monthData.monthNameAr})",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "খ্রিষ্টীয়: ${monthData.gregorianSpanBn}",
                                color = TextLight.copy(alpha = 0.75f),
                                fontSize = 11.5.sp
                            )
                        }

                        IconButton(
                            onClick = onNextMonth,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NavySurface)
                                .testTag("next_hijri_month_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "পরবর্তী মাস",
                                tint = IslamicGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Moon Sighting Offset Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NightsStay,
                                contentDescription = null,
                                tint = LightCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "চাঁদ দেখার সমন্বয়:",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(-1, 0, 1).forEach { offset ->
                                val isSelected = (offsetDays == offset)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) IslamicGold else NavySurface,
                                    border = BorderStroke(0.5.dp, if (isSelected) BrightGold else Color(0x33D4AF37)),
                                    modifier = Modifier
                                        .clickable { onSetOffset(offset) }
                                        .testTag("offset_btn_$offset")
                                ) {
                                    Text(
                                        text = when (offset) {
                                            -1 -> "-১ দিন"
                                            0 -> "স্বাভাবিক (০)"
                                            else -> "+১ দিন"
                                        },
                                        color = if (isSelected) DeepNavy else TextLight,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Return to Today button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavySurface,
                                border = BorderStroke(0.5.dp, IslamicGold),
                                modifier = Modifier.clickable { onResetToday() }
                            ) {
                                Text(
                                    text = "আজ",
                                    color = IslamicGold,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Calendar Weekday Headers (Saturday to Friday)
        item {
            val weekDays = listOf(
                Pair("শনি", false),
                Pair("রবি", false),
                Pair("সোম", false),
                Pair("মঙ্গল", false),
                Pair("বুধ", false),
                Pair("বৃহঃ", false),
                Pair("শুক্র", true) // Highlight Jummah
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavySurface, RoundedCornerShape(10.dp))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                weekDays.forEach { (name, isJummah) ->
                    Text(
                        text = name,
                        color = if (isJummah) EmeraldSuccess else IslamicGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Calendar Days Grid
        item {
            CalendarDaysGrid(
                days = monthData.days,
                selectedDay = selectedDay,
                onDaySelected = onDaySelected
            )
        }

        // Selected Day Details Card
        item {
            if (selectedDay != null) {
                SelectedDayDetailsCard(day = selectedDay)
            }
        }
    }
}

/**
 * 7-column grid showing days of the Hijri Month aligned by weekday.
 */
@Composable
private fun CalendarDaysGrid(
    days: List<HijriCalendarDay>,
    selectedDay: HijriCalendarDay?,
    onDaySelected: (HijriCalendarDay) -> Unit
) {
    if (days.isEmpty()) return

    // Calculate leading blank slots based on the day of week of the 1st day
    // In our week headers: 0 = Saturday, 1 = Sunday, ..., 6 = Friday
    val firstDayDow = days.first().dayOfWeek // 1 = Sunday, 7 = Saturday
    // Map Java Calendar DOW (1..7) to our grid index where Saturday = 0, Sunday = 1, ..., Friday = 6
    val leadingEmpty = when (firstDayDow) {
        java.util.Calendar.SATURDAY -> 0
        java.util.Calendar.SUNDAY -> 1
        java.util.Calendar.MONDAY -> 2
        java.util.Calendar.TUESDAY -> 3
        java.util.Calendar.WEDNESDAY -> 4
        java.util.Calendar.THURSDAY -> 5
        java.util.Calendar.FRIDAY -> 6
        else -> 0
    }

    val totalCells = leadingEmpty + days.size
    val totalRows = (totalCells + 6) / 7

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (row in 0 until totalRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    val dayIndex = cellIndex - leadingEmpty

                    if (dayIndex in days.indices) {
                        val day = days[dayIndex]
                        val isSelected = (selectedDay?.hijriDay == day.hijriDay)

                        DayCell(
                            day = day,
                            isSelected = isSelected,
                            onSelect = { onDaySelected(day) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        // Empty slot
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

/**
 * Single Day Cell inside the Monthly Grid.
 */
@Composable
private fun DayCell(
    day: HijriCalendarDay,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isToday = day.isToday
    val isAyyam = day.isAyyamAlBeed
    val hasEvent = day.event != null

    val backgroundColor = when {
        isSelected -> IslamicGold.copy(alpha = 0.28f)
        isToday -> NavyCardElevated
        isAyyam -> Color(0x1A26D0CE)
        day.isFriday -> Color(0x152ECC71)
        else -> NavySurface
    }

    val borderColor = when {
        isSelected -> BrightGold
        isToday -> BrightGold.copy(alpha = 0.8f)
        hasEvent -> IslamicGold.copy(alpha = 0.6f)
        isAyyam -> LightCyan.copy(alpha = 0.5f)
        else -> Color(0x22D4AF37)
    }

    Surface(
        modifier = modifier
            .aspectRatio(0.9f)
            .clickable { onSelect() }
            .testTag("hijri_day_cell_${day.hijriDay}"),
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = BorderStroke(if (isSelected || isToday) 1.5.dp else 0.8.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top badges (Event or Ayyam al-Beed icon)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasEvent) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "বিশেষ দিবস",
                        tint = BrightGold,
                        modifier = Modifier.size(9.dp)
                    )
                } else if (isAyyam) {
                    Icon(
                        imageVector = Icons.Outlined.NightsStay,
                        contentDescription = "আইয়ামে বিজ",
                        tint = LightCyan,
                        modifier = Modifier.size(9.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(9.dp))
                }

                if (isToday) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(EmeraldSuccess)
                    )
                } else {
                    Spacer(modifier = Modifier.size(6.dp))
                }
            }

            // Center: Hijri Day in bold Bengali digits
            Text(
                text = HijriCalendarService.toBengaliDigits(day.hijriDay),
                color = when {
                    isToday -> BrightGold
                    isSelected -> BrightGold
                    day.isFriday -> EmeraldSuccess
                    else -> TextLight
                },
                fontSize = 14.sp,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Medium
            )

            // Bottom: Gregorian Day
            Text(
                text = "${day.gregorianDay}",
                color = TextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

/**
 * Detail Card for the currently selected day.
 */
@Composable
private fun SelectedDayDetailsCard(day: HijriCalendarDay) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("selected_day_details_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        Text(
                            text = "${HijriCalendarService.toBengaliDigits(day.hijriDay)} ${HijriCalendarService.toBengaliDigits(day.hijriYear)} হিজরি",
                            color = BrightGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (day.isToday) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                border = BorderStroke(0.5.dp, EmeraldSuccess)
                            ) {
                                Text(
                                    text = "আজকের দিন",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "ইংরেজি: ${HijriCalendarService.toBengaliDigits(day.gregorianDay)}/${HijriCalendarService.toBengaliDigits(day.gregorianMonth)}/${HijriCalendarService.toBengaliDigits(day.gregorianYear)}, ${day.dayOfWeekNameBn}",
                        color = TextLight.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }

                if (day.isAyyamAlBeed) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = LightCyan.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, LightCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.NightsStay,
                                contentDescription = null,
                                tint = LightCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "আইয়ামে বিজ (সাদা রোজা)",
                                color = LightCyan,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // If this day has an Islamic Event, showcase it
            if (day.event != null) {
                HorizontalDivider(color = Color(0x33D4AF37), thickness = 0.5.dp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = day.event.titleBn,
                            color = BrightGold,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = day.event.significance,
                            color = TextLight,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Recommended Sunnah Practice on this day
            val sunnahTip = when {
                day.isAyyamAlBeed -> "সুন্নাত আমল: রাসূলুল্লাহ ﷺ প্রতি আরবি মাসের ১৩, ১৪ ও ১৫ তারিখ আইয়ামে বিজের রোজা রাখতেন, যা সারা বছর রোজা রাখার সমতুল্য।"
                day.dayOfWeek == java.util.Calendar.MONDAY || day.dayOfWeek == java.util.Calendar.THURSDAY ->
                    "সুন্নাত আমল: সোমবার ও বৃহস্পতিবার আল্লাহর দরবারে আমল পেশ করা হয়; এ দুই দিনে রোজা রাখা বিশেষ সুন্নাত।"
                day.isFriday ->
                    "জুমার সুন্নাত: গোসল, সুগন্ধি ব্যবহার, আগেভাগে মসজিদে যাওয়া, সূরা আল-কাহাফ তিলাওয়াত এবং নবীজি ﷺ-এর ওপর অধিক দরুদ পাঠ।"
                else -> null
            }

            if (sunnahTip != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NavySurface,
                    border = BorderStroke(0.5.dp, Color(0x22FFFFFF))
                ) {
                    Text(
                        text = sunnahTip,
                        color = TextLight.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: SIGNIFICANT ISLAMIC EVENTS VIEW
// =========================================================================
@Composable
private fun SignificantIslamicEventsView(
    events: List<IslamicEvent>
) {
    var selectedCategory by remember { mutableStateOf("সকল দিবস") }
    val categories = listOf("সকল দিবস", "আসন্ন (কাছাকাছি)", "রমজান ও ঈদ", "পবিত্র মাস", "ঐতিহাসিক দিবস", "ফজিলতপূর্ণ রজনী")

    val filteredEvents = remember(selectedCategory, events) {
        when (selectedCategory) {
            "সকল দিবস" -> events
            "আসন্ন (কাছাকাছি)" -> events.filter { it.daysRemaining <= 60 }
            else -> events.filter { it.category == selectedCategory }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Category Filter Chips
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(36.dp),
            content = {}
        )
        // Horizontal scrollable chip row
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
            containerColor = Color.Transparent,
            contentColor = BrightGold,
            edgePadding = 0.dp,
            divider = {},
            indicator = {}
        ) {
            categories.forEach { cat ->
                val isSelected = (selectedCategory == cat)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) IslamicGold else NavyCard,
                    border = BorderStroke(0.5.dp, if (isSelected) BrightGold else Color(0x33D4AF37)),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable { selectedCategory = cat }
                        .testTag("event_filter_$cat")
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) DeepNavy else TextLight,
                        fontSize = 11.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // List of Events
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredEvents) { event ->
                IslamicEventCard(event = event)
            }
        }
    }
}

/**
 * Card representing a single Significant Islamic Event.
 */
@Composable
private fun IslamicEventCard(event: IslamicEvent) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, if (event.isToday) EmeraldSuccess else GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Title & Countdown Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (event.isToday) EmeraldSuccess.copy(alpha = 0.25f)
                                else IslamicGold.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            tint = if (event.isToday) EmeraldSuccess else BrightGold,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Column {
                        Text(
                            text = event.titleBn,
                            color = if (event.isToday) EmeraldSuccess else BrightGold,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${HijriCalendarService.toBengaliDigits(event.hijriDay)} ${event.hijriMonthNameBn} (${event.titleAr})",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // Countdown Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        event.isToday -> EmeraldSuccess
                        event.daysRemaining <= 10 -> BrightGold
                        else -> NavySurface
                    },
                    border = BorderStroke(
                        0.5.dp,
                        if (event.isToday || event.daysRemaining <= 10) Color.Transparent else IslamicGold.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = when {
                            event.isToday -> "আজ পবিত্র দিন!"
                            event.daysRemaining == 1L -> "আগামীকাল"
                            else -> "আর ${HijriCalendarService.toBengaliDigits(event.daysRemaining.toInt())} দিন বাকি"
                        },
                        color = when {
                            event.isToday -> DeepNavy
                            event.daysRemaining <= 10 -> DeepNavy
                            else -> IslamicGold
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            // Estimated Gregorian Date Banner
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NavySurface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সম্ভাব্য খ্রিষ্টীয় তারিখ:",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = event.estimatedGregorianDateBn,
                        color = TextLight,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Significance Text
            Text(
                text = event.significance,
                color = TextLight.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            // Quran / Hadith Reference
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0x18D4AF37),
                border = BorderStroke(0.5.dp, Color(0x33D4AF37)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = event.quranHadithReference,
                        color = IslamicGold,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            // Expandable Recommended Deeds (আমল)
            AnimatedVisibility(visible = isExpanded, enter = fadeIn(), exit = fadeOut()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = "এই দিনের বিশেষ ফজিলতপূর্ণ আমলসমূহ:",
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    event.recommendedDeeds.forEach { deed ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(BrightGold)
                            )
                            Text(
                                text = deed,
                                color = TextLight,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Expand / Collapse indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "আমল সংক্ষিপ্ত করুন ▲" else "আমল ও ফজিলত বিস্তারিত দেখুন ▼",
                    color = TextMuted,
                    fontSize = 10.5.sp
                )
            }
        }
    }
}

// =========================================================================
// TAB 3: HIJRI DATE CONVERTER VIEW
// =========================================================================
@Composable
private fun HijriDateConverterView(offsetDays: Int) {
    var gYear by remember { mutableIntStateOf(2026) }
    var gMonth by remember { mutableIntStateOf(9) }
    var gDay by remember { mutableIntStateOf(22) }

    val convertedHijri = remember(gYear, gMonth, gDay, offsetDays) {
        HijriCalendarService.getHijriDateInfo(gYear, gMonth, gDay, offsetDays)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                border = BorderStroke(1.dp, GoldBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.SwapHoriz,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "খ্রিষ্টীয় তারিখ থেকে হিজরি তারিখ নির্ণয়",
                            color = BrightGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "যেকোনো ইংরেজি তারিখ নির্বাচন করে সমতুল্য ইসলামিক (হিজরি) তারিখ ও বার জানুন:",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )

                    // Pickers for Day, Month, Year
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Day Picker
                        NumberSelector(
                            label = "দিন",
                            value = gDay,
                            range = 1..31,
                            onValueChange = { gDay = it },
                            modifier = Modifier.weight(1f)
                        )

                        // Month Picker
                        NumberSelector(
                            label = "মাস",
                            value = gMonth,
                            range = 1..12,
                            onValueChange = { gMonth = it },
                            modifier = Modifier.weight(1f)
                        )

                        // Year Picker
                        NumberSelector(
                            label = "সাল",
                            value = gYear,
                            range = 1950..2050,
                            onValueChange = { gYear = it },
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Quick Jump to Today
                    Button(
                        onClick = {
                            val cal = java.util.Calendar.getInstance()
                            gYear = cal.get(java.util.Calendar.YEAR)
                            gMonth = cal.get(java.util.Calendar.MONTH) + 1
                            gDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                        border = BorderStroke(0.5.dp, IslamicGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "আজকের তারিখে সেট করুন",
                            color = IslamicGold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Result Showcase Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCardElevated),
                border = BorderStroke(1.5.dp, BrightGold)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "গণনাকৃত হিজরি (ইসলামিক) তারিখ:",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Text(
                        text = convertedHijri.formattedHijriBn,
                        color = BrightGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "${convertedHijri.day} ${convertedHijri.monthNameAr} ${convertedHijri.year} هـ",
                        color = IslamicGold,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )

                    HorizontalDivider(color = Color(0x33D4AF37), thickness = 0.5.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "বার:",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = convertedHijri.dayOfWeekBn,
                            color = TextLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আইয়ামে বিজ রোজা:",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (convertedHijri.isAyyamAlBeed) "হ্যাঁ (১৩, ১৪ বা ১৫ তারিখ)" else "না",
                            color = if (convertedHijri.isAyyamAlBeed) EmeraldSuccess else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Reusable stepper for selecting numbers.
 */
@Composable
private fun NumberSelector(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NavySurface,
        border = BorderStroke(0.5.dp, Color(0x33D4AF37)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 10.5.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (value > range.first) onValueChange(value - 1) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "কমান",
                        tint = IslamicGold,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = HijriCalendarService.toBengaliDigits(value),
                    color = TextLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { if (value < range.last) onValueChange(value + 1) },
                    modifier = Modifier.size(26.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "বাড়ান",
                        tint = IslamicGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
