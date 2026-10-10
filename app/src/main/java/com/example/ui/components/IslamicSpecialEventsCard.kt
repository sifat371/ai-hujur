package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.IslamicEvent
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

/**
 * Prominent Home Screen Card displaying upcoming Islamic Special Days with Live Countdown.
 */
@Composable
fun IslamicSpecialEventsHomeCard(
    upcomingEvents: List<IslamicEvent>,
    nextEvent: IslamicEvent?,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topEvents = remember(upcomingEvents) {
        upcomingEvents.take(4)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp))
            .testTag("islamic_special_events_home_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.2.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- HEADER ROW ---
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
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(IslamicGold.copy(alpha = 0.3f), BrightGold.copy(alpha = 0.1f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.EventAvailable,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "ইসলামের বিশেষ দিনসমূহ",
                            color = BrightGold,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "আসন্ন দিবস ও বাকি দিনের লাইভ হিসাব",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                TextButton(
                    onClick = onViewAllClick,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_view_all_islamic_events")
                ) {
                    Text(
                        text = "সকল দিবস",
                        color = IslamicGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // --- PRIMARY SPOTLIGHT CARD (The Immediate Next Event) ---
            if (nextEvent != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onViewAllClick() }
                        .testTag("next_event_spotlight_card"),
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = BorderStroke(1.2.dp, if (nextEvent.isToday) EmeraldSuccess else IslamicGold.copy(alpha = 0.7f))
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
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (nextEvent.isToday) EmeraldSuccess.copy(alpha = 0.2f) else IslamicGold.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Stars,
                                        contentDescription = null,
                                        tint = if (nextEvent.isToday) EmeraldSuccess else BrightGold,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (nextEvent.isToday) "আজকের পবিত্র দিন" else "সবচেয়ে নিকটে",
                                        color = if (nextEvent.isToday) EmeraldSuccess else BrightGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // COUNTDOWN BADGE
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    nextEvent.isToday -> EmeraldSuccess
                                    nextEvent.daysRemaining <= 10 -> BrightGold
                                    else -> Color(0x33D4AF37)
                                },
                                border = BorderStroke(
                                    0.8.dp,
                                    if (nextEvent.isToday || nextEvent.daysRemaining <= 10) Color.Transparent else IslamicGold.copy(alpha = 0.6f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.HourglassTop,
                                        contentDescription = null,
                                        tint = if (nextEvent.isToday || nextEvent.daysRemaining <= 10) DeepNavy else BrightGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = when {
                                            nextEvent.isToday -> "আজ পবিত্র দিন!"
                                            nextEvent.daysRemaining == 1L -> "আগামীকাল"
                                            else -> "আর ${HijriCalendarService.toBengaliDigits(nextEvent.daysRemaining.toInt())} দিন বাকি"
                                        },
                                        color = if (nextEvent.isToday || nextEvent.daysRemaining <= 10) DeepNavy else TextWhite,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Title & Arabic Name
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = nextEvent.titleBn,
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${HijriCalendarService.toBengaliDigits(nextEvent.hijriDay)} ${nextEvent.hijriMonthNameBn} • ${nextEvent.titleAr}",
                                color = IslamicGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Estimated Gregorian Date & Category
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅 সম্ভাব্য: ${nextEvent.estimatedGregorianDateBn}",
                                color = TextLight,
                                fontSize = 11.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0x18FFFFFF)
                            ) {
                                Text(
                                    text = nextEvent.category,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // --- HORIZONTAL SCROLL OF SUBSEQUENT UPCOMING EVENTS ---
            if (topEvents.size > 1) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "পরবর্তী বিশেষ দিবসসমূহ:",
                        color = TextLight,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        topEvents.drop(1).forEach { event ->
                            SubsequentEventMiniCard(
                                event = event,
                                onClick = onViewAllClick
                            )
                        }
                    }
                }
            }

            // --- BOTTOM CTA BUTTON ---
            OutlinedButton(
                onClick = onViewAllClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_all_islamic_events_dialog"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0x0DD4AF37),
                    contentColor = BrightGold
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = BrightGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ইসলামের সকল বিশেষ দিন ও দিন গণনা দেখুন",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Compact preview card for horizontal list of upcoming special days.
 */
@Composable
private fun SubsequentEventMiniCard(
    event: IslamicEvent,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("mini_event_card_${event.id}"),
        shape = RoundedCornerShape(14.dp),
        color = NavySurface,
        border = BorderStroke(0.8.dp, Color(0x33D4AF37))
    ) {
        Column(
            modifier = Modifier.padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Countdown badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when {
                    event.daysRemaining <= 30 -> BrightGold.copy(alpha = 0.25f)
                    else -> Color(0x18D4AF37)
                },
                border = BorderStroke(0.5.dp, IslamicGold.copy(alpha = 0.4f))
            ) {
                Text(
                    text = "আর ${HijriCalendarService.toBengaliDigits(event.daysRemaining.toInt())} দিন বাকি",
                    color = BrightGold,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                )
            }

            Text(
                text = event.titleBn,
                color = TextWhite,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${HijriCalendarService.toBengaliDigits(event.hijriDay)} ${event.hijriMonthNameBn}",
                color = IslamicGold,
                fontSize = 11.sp
            )

            Text(
                text = event.estimatedGregorianDateBn,
                color = TextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Full-featured interactive Dialog showing the complete list of Islamic Special Days,
 * accurate countdowns (days remaining), filters, search, and comprehensive Sunnah deeds.
 */
@Composable
fun IslamicSpecialEventsDialog(
    viewModel: AlHujurViewModel,
    onDismiss: () -> Unit
) {
    val upcomingEvents by viewModel.upcomingIslamicEvents.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("সকল দিবস") }

    val categories = listOf(
        "সকল দিবস",
        "কাছাকাছি (৬০ দিন)",
        "রমজান ও ঈদ",
        "পবিত্র মাস",
        "ঐতিহাসিক দিবস",
        "ফজিলতপূর্ণ রজনী"
    )

    val filteredEvents = remember(upcomingEvents, searchQuery, selectedCategory) {
        upcomingEvents.filter { event ->
            val matchesCategory = when (selectedCategory) {
                "সকল দিবস" -> true
                "কাছাকাছি (৬০ দিন)" -> event.daysRemaining <= 60
                else -> event.category == selectedCategory
            }

            val query = searchQuery.trim().lowercase()
            val matchesSearch = query.isEmpty() ||
                    event.titleBn.lowercase().contains(query) ||
                    event.titleAr.lowercase().contains(query) ||
                    event.hijriMonthNameBn.lowercase().contains(query) ||
                    event.significance.lowercase().contains(query)

            matchesCategory && matchesSearch
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
                .testTag("islamic_special_events_dialog"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(IslamicGold.copy(alpha = 0.35f), BrightGold.copy(alpha = 0.15f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EventNote,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "ইসলামের বিশেষ দিনসমূহ",
                                color = BrightGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "কোন দিন কত দিন বাকি ও ফজিলত",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_islamic_events_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextLight
                        )
                    }
                }

                // Search Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "দিবস বা মাস খুঁজুন (রমজান, কদর, ঈদ, আরাফাত)...",
                            color = TextMuted,
                            fontSize = 12.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = IslamicGold,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "মুছুন",
                                    tint = TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_islamic_events"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = BrightGold,
                        unfocusedBorderColor = GoldBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight
                    ),
                    singleLine = true
                )

                // Category Filter Scrollable Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = (selectedCategory == cat)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) IslamicGold else NavyCard,
                            border = BorderStroke(0.8.dp, if (isSelected) BrightGold else Color(0x33D4AF37)),
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .testTag("filter_chip_$cat")
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

                // Header Count info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "মোট দিবস: ${HijriCalendarService.toBengaliDigits(filteredEvents.size)}টি",
                        color = TextLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "কার্ডে চাপ দিয়ে ফজিলত ও আমল দেখুন",
                        color = IslamicGold.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }

                // Event Cards List
                if (filteredEvents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.EventBusy,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(42.dp)
                            )
                            Text(
                                text = "কোনো দিবস পাওয়া যায়নি",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredEvents, key = { it.id }) { event ->
                            DetailedIslamicEventCard(event = event)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Detailed Expandable Card for a single Islamic Holy Day.
 */
@Composable
private fun DetailedIslamicEventCard(event: IslamicEvent) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { isExpanded = !isExpanded }
            .testTag("detailed_event_card_${event.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (event.isToday) Color(0xFF0F2E23) else NavyCard
        ),
        border = BorderStroke(
            1.2.dp,
            when {
                event.isToday -> EmeraldSuccess
                event.daysRemaining <= 10 -> BrightGold
                else -> GoldBorder
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Title + Countdown Badge
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (event.isToday) EmeraldSuccess.copy(alpha = 0.25f)
                                else IslamicGold.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (event.category) {
                                "রমজান ও ঈদ" -> Icons.Outlined.NightsStay
                                "ফজিলতপূর্ণ রজনী" -> Icons.Outlined.WbTwilight
                                "পবিত্র মাস" -> Icons.Outlined.CalendarMonth
                                else -> Icons.Outlined.Star
                            },
                            contentDescription = null,
                            tint = if (event.isToday) EmeraldSuccess else BrightGold,
                            modifier = Modifier.size(18.dp)
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
                            text = "${HijriCalendarService.toBengaliDigits(event.hijriDay)} ${event.hijriMonthNameBn} • ${event.titleAr}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // COUNTDOWN BADGE (Prominent & Color Coded)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        event.isToday -> EmeraldSuccess
                        event.daysRemaining == 1L -> CoralAccent
                        event.daysRemaining <= 15 -> BrightGold
                        event.daysRemaining <= 60 -> Color(0x33D4AF37)
                        else -> NavySurface
                    },
                    border = BorderStroke(
                        0.8.dp,
                        if (event.isToday || event.daysRemaining <= 15) Color.Transparent else IslamicGold.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Timer,
                            contentDescription = null,
                            tint = if (event.isToday || event.daysRemaining <= 15) DeepNavy else BrightGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = when {
                                event.isToday -> "আজ পবিত্র দিন!"
                                event.daysRemaining == 1L -> "আগামীকাল"
                                else -> "আর ${HijriCalendarService.toBengaliDigits(event.daysRemaining.toInt())} দিন বাকি"
                            },
                            color = if (event.isToday || event.daysRemaining <= 15) DeepNavy else TextWhite,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Date and Category Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 সম্ভাব্য: ${event.estimatedGregorianDateBn}",
                    color = TextLight,
                    fontSize = 11.5.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x22D4AF37)
                    ) {
                        Text(
                            text = event.category,
                            color = IslamicGold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // EXPANDABLE CONTENT: Significance, Deeds, Quran/Hadith reference, Share Button
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HorizontalDivider(color = Color(0x33D4AF37), thickness = 0.8.dp)

                    // Significance
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = "তাৎপর্য ও গুরুত্ব:",
                            color = IslamicGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = event.significance,
                            color = TextLight,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }

                    // Recommended Deeds
                    if (event.recommendedDeeds.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "এই দিবসে করণীয় সুন্নাত আমল:",
                                color = BrightGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            event.recommendedDeeds.forEach { deed ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        color = BrightGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
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

                    // Quran / Hadith Reference
                    if (event.quranHadithReference.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NavySurface,
                            border = BorderStroke(0.6.dp, Color(0x33D4AF37)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                    lineHeight = 15.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }

                    // Share / Copy Details Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                val shareText = buildString {
                                    append("🌙 ${event.titleBn} (${event.titleAr})\n")
                                    append("📅 হিজরি তারিখ: ${HijriCalendarService.toBengaliDigits(event.hijriDay)} ${event.hijriMonthNameBn}\n")
                                    append("📅 সম্ভাব্য ইংরেজি: ${event.estimatedGregorianDateBn}\n")
                                    append("⏳ বাকি আছে: ${if (event.isToday) "আজ পবিত্র দিন!" else "আর ${HijriCalendarService.toBengaliDigits(event.daysRemaining.toInt())} দিন বাকি"}\n\n")
                                    append("📖 তাৎপর্য: ${event.significance}\n\n")
                                    if (event.recommendedDeeds.isNotEmpty()) {
                                        append("✨ করণীয় আমল:\n")
                                        event.recommendedDeeds.forEach { d -> append("• $d\n") }
                                        append("\n")
                                    }
                                    if (event.quranHadithReference.isNotEmpty()) {
                                        append("📜 রেফারেন্স: ${event.quranHadithReference}\n\n")
                                    }
                                    append("— Al-Hujur AI (ইসলামিক মাইন্ড)")
                                }

                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                val clip = ClipData.newPlainText("Islamic Event Details", shareText)
                                clipboard?.setPrimaryClip(clip)
                                Toast.makeText(context, "দিবসের তথ্য ও দিন গণনা কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "শেয়ার",
                                tint = BrightGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "শেয়ার / কপি করুন",
                                color = BrightGold,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
