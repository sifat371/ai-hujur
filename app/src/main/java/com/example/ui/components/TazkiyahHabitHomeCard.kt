package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadHabit
import com.example.data.repository.BadHabitRepository
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*

/**
 * Home Overview Card for Tazkiyah & Bad Habit Breaker (খারাপ অভ্যাস বর্জন ও আত্মশুদ্ধি ট্র্যাকার).
 */
@Composable
fun TazkiyahHabitHomeCard(
    habits: List<BadHabit>,
    onOpenFullTazkiyah: () -> Unit,
    onOpenEmergencySos: () -> Unit,
    onMarkClean: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val today = remember { BadHabitRepository.getTodayDateString() }
    val maxStreak = habits.maxOfOrNull { it.streakDays } ?: 0
    val totalCleanCount = habits.sumOf { it.totalCleanDays }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp))
            .testTag("card_tazkiyah_home"),
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
            // Header Row: Title, Badge, and Emergency SOS Trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFE11D48), Color(0xFF9F1239))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "খারাপ অভ্যাস বর্জন",
                                color = TextWhite,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldSuccess.copy(alpha = 0.2f),
                                border = BorderStroke(0.5.dp, EmeraldSuccess)
                            ) {
                                Text(
                                    text = "আত্মশুদ্ধি",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "তাজকিয়াহ ও নফস নিয়ন্ত্রণ ট্র্যাকার",
                            color = IslamicGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Emergency Panic Button (ওয়াসওয়াসা প্রতিরোধের SOS বাটন)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFDC2626),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier
                        .clickable { onOpenEmergencySos() }
                        .testTag("btn_emergency_waswasah_sos")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "ওয়াসওয়াসা SOS 🚨",
                            color = Color.White,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Streak & Stats Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DeepNavy,
                border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.6f)),
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "🔥 সর্বোচ্চ স্ট্রিক:",
                            color = BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${HijriCalendarService.toBengaliDigits(maxStreak)} দিন মুক্ত",
                            color = EmeraldSuccess,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "মোট সাফল্য: ${HijriCalendarService.toBengaliDigits(totalCleanCount)} দিন",
                        color = TextLight,
                        fontSize = 11.sp
                    )
                }
            }

            // Horizontal Scroll of Habit Cards with Quick "আজও মুক্ত ছিলাম" Action
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "ট্র্যাককৃত বদভ্যাসসমূহ (আজকের অগ্রগতি মার্ক করুন):",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    habits.take(5).forEach { habit ->
                        val isMarkedToday = habit.lastCleanDate == today

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NavySurface,
                            border = BorderStroke(
                                1.dp,
                                if (isMarkedToday) EmeraldSuccess.copy(alpha = 0.8f) else GoldBorder.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { onOpenFullTazkiyah() }
                                .testTag("card_habit_preview_${habit.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(11.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(5.dp),
                                        color = IslamicGold.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = habit.category,
                                            color = IslamicGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }

                                    Text(
                                        text = "${HijriCalendarService.toBengaliDigits(habit.streakDays)} দিন মুক্ত",
                                        color = if (habit.streakDays > 0) EmeraldSuccess else TextMuted,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = habit.titleBn,
                                    color = TextWhite,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Quick Check-in Button
                                Button(
                                    onClick = {
                                        if (!isMarkedToday) {
                                            onMarkClean(habit.id)
                                            Toast.makeText(context, "আলহামদুলিল্লাহ! আজকের দিনটি সফলভাবে রেকর্ড হয়েছে।", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "আজকের দিনটি ইতিমধ্যে সংরক্ষিত হয়েছে।", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(30.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isMarkedToday) EmeraldSuccess else Color(0x22D4AF37)
                                    ),
                                    border = BorderStroke(
                                        0.8.dp,
                                        if (isMarkedToday) EmeraldSuccess else IslamicGold
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isMarkedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isMarkedToday) Color.White else BrightGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isMarkedToday) "আজ মুক্ত ছিলাম ✅" else "আজও মুক্ত আছি 🎯",
                                        color = if (isMarkedToday) Color.White else BrightGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Full Sheet Button
            OutlinedButton(
                onClick = onOpenFullTazkiyah,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_full_tazkiyah_sheet"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BrightGold.copy(alpha = 0.8f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = BrightGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "সম্পূর্ণ আত্মশুদ্ধি ড্যাশবোর্ড ও নতুন অভ্যাস যোগ করুন",
                    color = BrightGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = BrightGold,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
