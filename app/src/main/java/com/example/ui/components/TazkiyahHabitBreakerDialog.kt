package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.BadHabit
import com.example.data.repository.BadHabitRepository
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*

/**
 * Full-featured Tazkiyah & Bad Habit Breaker Dialog (খারাপ অভ্যাস বর্জন ও আত্মশুদ্ধি ড্যাশবোর্ড).
 */
@Composable
fun TazkiyahHabitBreakerDialog(
    habits: List<BadHabit>,
    onDismiss: () -> Unit,
    onMarkClean: (String) -> Unit,
    onResetRelapse: (String) -> Unit,
    onAddCustomHabit: (String, String, String) -> Unit,
    onDeleteHabit: (String) -> Unit,
    onOpenEmergencySos: () -> Unit,
    onConsultAi: (String) -> Unit
) {
    val context = LocalContext.current
    val today = remember { BadHabitRepository.getTodayDateString() }

    var showAddHabitDialog by remember { mutableStateOf(false) }
    var habitToRelapse by remember { mutableStateOf<BadHabit?>(null) }
    var istighfarCount by remember { mutableIntStateOf(0) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("dialog_tazkiyah_breaker"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, GoldBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header Row
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
                                        listOf(Color(0xFFE11D48), Color(0xFF9F1239))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "খারাপ অভ্যাস বর্জন ও আত্মশুদ্ধি",
                                color = BrightGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "তাজকিয়াতুন নফস ও তাওবার ট্র্যাকার",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Emergency SOS Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFDC2626),
                    border = BorderStroke(1.dp, Color(0xFFFECACA)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenEmergencySos() }
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "কুপ্রবৃত্তি বা ওয়াসওয়াসা অনুভব করছেন?",
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "তাত্ক্ষণিক ৪টি সুন্নাহ প্রতিকার দেখতে ট্যাপ করুন",
                                    color = Color(0xFFFEE2E2),
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "SOS 🚨",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Quick Statistics Banner
                val maxStreak = habits.maxOfOrNull { it.streakDays } ?: 0
                val totalClean = habits.sumOf { it.totalCleanDays }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NavySurface,
                    border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "ট্র্যাককৃত বদভ্যাস",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = "${HijriCalendarService.toBengaliDigits(habits.size)}টি",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(28.dp),
                            color = GoldBorder.copy(alpha = 0.4f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "সর্বোচ্চ স্ট্রিক",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = "${HijriCalendarService.toBengaliDigits(maxStreak)} দিন",
                                color = BrightGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        VerticalDivider(
                            modifier = Modifier.height(28.dp),
                            color = GoldBorder.copy(alpha = 0.4f)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "মোট মুক্ত দিন",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = "${HijriCalendarService.toBengaliDigits(totalClean)} দিন",
                                color = EmeraldSuccess,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Add Custom Habit Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "আপনার বদভ্যাসসমূহ:",
                        color = IslamicGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddHabitDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x22D4AF37)),
                        border = BorderStroke(1.dp, IslamicGold),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "নতুন যোগ করুন",
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Habit List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(habits, key = { it.id }) { habit ->
                        BadHabitItemCard(
                            habit = habit,
                            todayDate = today,
                            onMarkClean = {
                                onMarkClean(habit.id)
                                Toast.makeText(context, "আলহামদুলিল্লাহ! আজকের সফল দিনটি যোগ করা হয়েছে।", Toast.LENGTH_SHORT).show()
                            },
                            onRelapseClick = {
                                habitToRelapse = habit
                            },
                            onDelete = {
                                onDeleteHabit(habit.id)
                                Toast.makeText(context, "অভ্যাসটি তালিকা থেকে সরানো হয়েছে।", Toast.LENGTH_SHORT).show()
                            },
                            onConsultAi = { prompt ->
                                onDismiss()
                                onConsultAi(prompt)
                            }
                        )
                    }

                    // Daily Istighfar Counter Box
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DeepNavy,
                            border = BorderStroke(1.dp, GoldBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📿 সাইয়্যিদুল ইস্তিগফার ও তওবা",
                                        color = BrightGold,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "গণনা: ${HijriCalendarService.toBengaliDigits(istighfarCount)} বার",
                                        color = EmeraldSuccess,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "أَسْتَغْفِرُ اللَّهَ الَّذِي لاَ إِلَهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ وَأَتُوبُ إِلَيْهِ",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "\"আমি আল্লাহর কাছে ক্ষমা চাই, যিনি ছাড়া কোনো উপাস্য নেই, যিনি চিরঞ্জীব, চিরস্থায়ী এবং আমি তাঁর দিকেই তওবা করছি।\"",
                                    color = TextLight,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )

                                Button(
                                    onClick = { istighfarCount++ },
                                    modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "আস্তাগফিরুল্লাহ পাঠ (+১)",
                                        color = Color.White,
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
    }

    // Modal: Add Custom Habit Dialog
    if (showAddHabitDialog) {
        AddCustomHabitModal(
            onDismiss = { showAddHabitDialog = false },
            onAdd = { title, reason, cat ->
                onAddCustomHabit(title, reason, cat)
                showAddHabitDialog = false
                Toast.makeText(context, "অভ্যাসটি সফলভাবে যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Modal: Compassionate Relapse & Repentance Dialog
    habitToRelapse?.let { habit ->
        CompassionateRelapseModal(
            habit = habit,
            onDismiss = { habitToRelapse = null },
            onConfirmReset = {
                onResetRelapse(habit.id)
                habitToRelapse = null
                Toast.makeText(context, "আল্লাহ আপনাকে ক্ষমা করুন। নতুন দৃঢ়তায় শুরু করুন!", Toast.LENGTH_SHORT).show()
            },
            onConsultAi = {
                val q = "মুহতারাম, আমি '${habit.titleBn}' বর্জনের চেষ্টায় ভুলবশত আবারও গুনাহে লিপ্ত হয়েছি। আমার মন ভেঙে গেছে। কীভাবে আন্তরিকভাবে তওবা করে আবার নতুন উদ্যমে শুরু করব?"
                habitToRelapse = null
                onDismiss()
                onConsultAi(q)
            }
        )
    }
}

/**
 * Individual Bad Habit Card with details, streak, and actions.
 */
@Composable
private fun BadHabitItemCard(
    habit: BadHabit,
    todayDate: String,
    onMarkClean: () -> Unit,
    onRelapseClick: () -> Unit,
    onDelete: () -> Unit,
    onConsultAi: (String) -> Unit
) {
    val isMarkedToday = habit.lastCleanDate == todayDate
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NavySurface,
        border = BorderStroke(
            1.2.dp,
            if (isMarkedToday) EmeraldSuccess.copy(alpha = 0.8f) else GoldBorder.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category & Streak Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = IslamicGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = habit.category,
                        color = IslamicGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (habit.streakDays > 0) EmeraldSuccess.copy(alpha = 0.15f) else Color(0x22FFFFFF)
                    ) {
                        Text(
                            text = "🔥 ${HijriCalendarService.toBengaliDigits(habit.streakDays)} দিন মুক্ত",
                            color = if (habit.streakDays > 0) EmeraldSuccess else TextLight,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (habit.id.startsWith("custom_")) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "মুছুন",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Title & Reason
            Text(
                text = habit.titleBn,
                color = TextWhite,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold
            )

            if (habit.reasonToQuit.isNotBlank()) {
                Text(
                    text = "কেন ত্যাগ করবেন: ${habit.reasonToQuit}",
                    color = TextLight,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            }

            // Action Buttons Row: Check-in, Relapse, Expand Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Check-in Button
                Button(
                    onClick = onMarkClean,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMarkedToday) EmeraldSuccess else Color(0xFF1E3A8A)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = if (isMarkedToday) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMarkedToday) "আজ মুক্ত ছিলাম ✅" else "আজও মুক্ত আছি 🎯",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Relapse Button
                OutlinedButton(
                    onClick = onRelapseClick,
                    modifier = Modifier
                        .weight(0.9f)
                        .height(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFF87171)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF87171)),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "স্খলন / রিল্যাপ্স",
                        color = Color(0xFFF87171),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Expand/Collapse Details
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "বিস্তারিত",
                        tint = BrightGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Expanded Spiritual Advice & AI Consultation
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (habit.spiritualRemedy.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DeepNavy,
                            border = BorderStroke(0.6.dp, GoldBorder.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "💡 ইসলামিক ও আত্মশুদ্ধিমূলক প্রতিকার:",
                                    color = BrightGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = habit.spiritualRemedy,
                                    color = TextLight,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    if (habit.quranAyahOrHadith.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0x18D4AF37)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "\"${habit.quranAyahOrHadith}\"",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                                if (habit.reference.isNotBlank()) {
                                    Text(
                                        text = "— ${habit.reference}",
                                        color = BrightGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Consult AI Scholar regarding this specific habit
                    TextButton(
                        onClick = {
                            val prompt = "মুহতারাম, আমি '${habit.titleBn}' এই খারাপ অভ্যাসটি পুরোপুরি বর্জন করতে চাই। কুরআন, সহীহ সুন্নাহ ও নফস নিয়ন্ত্রণের আলোকে আমাকে দৈনন্দিন কার্যকরী রুটিন ও দোয়া বাতলে দিন।"
                            onConsultAi(prompt)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "এই অভ্যাস ছাড়তে এআই হুজুরের পরামর্শ নিন ➜",
                            color = BrightGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal to Add a Custom Bad Habit.
 */
@Composable
private fun AddCustomHabitModal(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("দৃষ্টির সংযম") }

    val categories = listOf(
        "দৃষ্টির সংযম",
        "জিহ্বার হেফাজত",
        "আমল ও ইবাদত",
        "দেহের পবিত্রতা",
        "সময়ের মূল্য",
        "ব্যক্তিগত অভ্যাস"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, GoldBorder),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "নতুন খারাপ অভ্যাস যোগ করুন",
                    color = BrightGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "যে বদভ্যাসটি আপনি আল্লাহর সন্তুষ্টির উদ্দেশ্যে বর্জন করতে চান:",
                    color = TextLight,
                    fontSize = 11.sp
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("অভ্যাসের নাম (যেমন: গালি দেওয়া)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = BrightGold,
                        unfocusedBorderColor = GoldBorder
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("কেন এটি ত্যাগ করতে চান?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = BrightGold,
                        unfocusedBorderColor = GoldBorder
                    ),
                    maxLines = 2
                )

                Text(
                    text = "ক্যাটাগরি নির্বাচন করুন:",
                    color = BrightGold,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )

                // Category chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGold,
                                selectedLabelColor = DeepNavy,
                                containerColor = NavySurface,
                                labelColor = TextLight
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGold,
                                selectedLabelColor = DeepNavy,
                                containerColor = NavySurface,
                                labelColor = TextLight
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("বাতিল", color = TextLight)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAdd(title, reason, selectedCategory)
                            }
                        },
                        enabled = title.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrightGold)
                    ) {
                        Text("সংরক্ষণ করুন", color = DeepNavy, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Compassionate Modal when a user relapses (স্খলন বা অনিচ্ছাকৃত ভুলের পর তওবা ও উৎসাহ).
 */
@Composable
private fun CompassionateRelapseModal(
    habit: BadHabit,
    onDismiss: () -> Unit,
    onConfirmReset: () -> Unit,
    onConsultAi: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, Color(0xFFF87171)),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0x33F87171),
                    modifier = Modifier.size(46.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            tint = Color(0xFFFCA5A5),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Text(
                    text = "আল্লাহর রহমত থেকে নিরাশ হবেন না!",
                    color = Color(0xFFFCA5A5),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "\"বলো: হে আমার বান্দাগণ যারা নিজেদের ওপর বাড়াবাড়ি করেছ, তোমরা আল্লাহর রহমত থেকে নিরাশ হয়ো না। নিশ্চয়ই আল্লাহ সমস্ত গুনাহ ক্ষমা করে দেন।\" (সূরা আয-যুমার: ৫৩)",
                    color = BrightGold,
                    fontSize = 11.5.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(0.6.dp, GoldBorder.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "তাত্ক্ষণিক ৩টি আমল এখনই করুন:",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "১. অজু করে ২ রাকাত সালাতুত তাওবা পড়ুন।\n২. আন্তরিক অনুশোচনায় 'আস্তাগফিরুল্লাহ' পাঠ করুন।\n৩. সামান্য কিছু সদকা করুন (সদকা গুনাহের আগুন নিভিয়ে দেয়)।",
                            color = TextLight,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // AI Counselor Option
                OutlinedButton(
                    onClick = onConsultAi,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BrightGold),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "এআই হুজুরের কাছে আন্তরিক সান্ত্বনা নিন",
                        color = BrightGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Buttons: Cancel vs Start Fresh
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("ফিরে যান", color = TextLight)
                    }

                    Button(
                        onClick = onConfirmReset,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                    ) {
                        Text("নতুন প্রত্যয়ে শুরু করুন 🎯", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
