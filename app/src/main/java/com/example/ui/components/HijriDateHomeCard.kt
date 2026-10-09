package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HijriDateInfo
import com.example.data.model.IslamicEvent
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*

@Composable
fun HijriDateHomeCard(
    hijriDate: HijriDateInfo,
    nextEvent: IslamicEvent?,
    onOpenCalendar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .clickable { onOpenCalendar() }
            .testTag("hijri_date_home_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Icon, Section Title & "ক্যালেন্ডার খুলুন" button
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
                            .size(32.dp)
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
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Text(
                        text = "হিজরি ক্যালেন্ডার",
                        color = IslamicGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(0.5.dp, GoldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "মাসিক ভিউ",
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

            // Central Display: Hijri Date alongside Gregorian Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    // Current Islamic Date
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = hijriDate.formattedHijriBn,
                            color = BrightGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${hijriDate.monthNameAr})",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }

                    // Current Gregorian Date alongside
                    Text(
                        text = "খ্রিষ্টীয়: ${hijriDate.formattedGregorianBn}",
                        color = TextLight.copy(alpha = 0.85f),
                        fontSize = 12.5.sp
                    )
                }

                // Ayyam al-Beed or Friday Pill
                if (hijriDate.isAyyamAlBeed) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = LightCyan.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, LightCyan)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
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
                                text = "আইয়ামে বিজ",
                                color = LightCyan,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (hijriDate.isFriday) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldSuccess.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, EmeraldSuccess)
                    ) {
                        Text(
                            text = "মুবারক জুমা",
                            color = EmeraldSuccess,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Upcoming Significant Event Highlight Banner
            if (nextEvent != null) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = BorderStroke(0.6.dp, Color(0x33D4AF37)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (nextEvent.isToday) Icons.Default.Star else Icons.Outlined.Event,
                                contentDescription = null,
                                tint = if (nextEvent.isToday) EmeraldSuccess else BrightGold,
                                modifier = Modifier.size(18.dp)
                            )

                            Column {
                                Text(
                                    text = nextEvent.titleBn,
                                    color = TextLight,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${HijriCalendarService.toBengaliDigits(nextEvent.hijriDay)} ${nextEvent.hijriMonthNameBn} • ${nextEvent.estimatedGregorianDateBn}",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Countdown tag
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (nextEvent.isToday) EmeraldSuccess else IslamicGold.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, if (nextEvent.isToday) EmeraldSuccess else IslamicGold)
                        ) {
                            Text(
                                text = when {
                                    nextEvent.isToday -> "আজকে!"
                                    nextEvent.daysRemaining == 1L -> "কালকে"
                                    else -> "আর ${HijriCalendarService.toBengaliDigits(nextEvent.daysRemaining.toInt())} দিন"
                                },
                                color = if (nextEvent.isToday) DeepNavy else BrightGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
