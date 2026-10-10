package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import com.example.data.model.QuizQuestion
import com.example.data.repository.ViralFeaturesRepository
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*

/**
 * High-engagement Daily Islamic Quiz Dialog designed for viral peer sharing.
 */
@Composable
fun IslamicQuizDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val questions = remember { ViralFeaturesRepository.quizQuestions.shuffled().take(5) }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isQuizFinished by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    fun triggerHaptic(success: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(if (success) 80 else 180, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (success) 80 else 180)
            }
        } catch (_: Exception) {}
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .testTag("dialog_islamic_quiz"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, GoldBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(IslamicGold, WarmGold)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "দৈনিক ইসলামিক কুইজ",
                                color = BrightGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "দ্বীনি জ্ঞান ও মেধা প্রতিযোগিতা",
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

                if (!isQuizFinished && currentQuestion != null) {
                    // Progress Bar & Question Counter
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "প্রশ্ন: ${HijriCalendarService.toBengaliDigits(currentQuestionIndex + 1)} / ${HijriCalendarService.toBengaliDigits(questions.size)}",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = IslamicGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = currentQuestion.category,
                                    color = BrightGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { (currentQuestionIndex + 1) / questions.size.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = BrightGold,
                            trackColor = Color(0x33D4AF37)
                        )
                    }

                    // Question Box & Options
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NavySurface,
                            border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = currentQuestion.questionBn,
                                color = TextWhite,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 21.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Options
                        currentQuestion.options.forEachIndexed { index, option ->
                            val isCorrect = index == currentQuestion.correctIndex
                            val isSelected = selectedOptionIndex == index

                            val backgroundColor = when {
                                !isSubmitted && isSelected -> Color(0x33D4AF37)
                                isSubmitted && isCorrect -> EmeraldSuccess.copy(alpha = 0.25f)
                                isSubmitted && isSelected && !isCorrect -> Color(0x33EF4444)
                                else -> NavySurface
                            }

                            val borderColor = when {
                                !isSubmitted && isSelected -> BrightGold
                                isSubmitted && isCorrect -> EmeraldSuccess
                                isSubmitted && isSelected && !isCorrect -> Color(0xFFEF4444)
                                else -> GoldBorder.copy(alpha = 0.3f)
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = backgroundColor,
                                border = BorderStroke(1.2.dp, borderColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isSubmitted) {
                                        selectedOptionIndex = index
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) BrightGold else Color(0x22FFFFFF),
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = when (index) {
                                                    0 -> "ক"
                                                    1 -> "খ"
                                                    2 -> "গ"
                                                    else -> "ঘ"
                                                },
                                                color = if (isSelected) DeepNavy else TextWhite,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Text(
                                        text = option,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isSubmitted) {
                                        if (isCorrect) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EmeraldSuccess,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        } else if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Explanation after submission
                        AnimatedVisibility(visible = isSubmitted) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = DeepNavy,
                                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = BrightGold,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Text(
                                            text = "দলিল ও ব্যাখ্যা:",
                                            color = BrightGold,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = currentQuestion.explanationBn,
                                        color = TextLight,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                    Text(
                                        text = "রেফারেন্স: ${currentQuestion.referenceBn}",
                                        color = IslamicGold,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // Bottom Action Button: Check Answer or Next Question
                    Button(
                        onClick = {
                            if (!isSubmitted) {
                                if (selectedOptionIndex != null) {
                                    val correct = selectedOptionIndex == currentQuestion.correctIndex
                                    if (correct) score++
                                    triggerHaptic(correct)
                                    isSubmitted = true
                                }
                            } else {
                                if (currentQuestionIndex + 1 < questions.size) {
                                    currentQuestionIndex++
                                    selectedOptionIndex = null
                                    isSubmitted = false
                                } else {
                                    ViralFeaturesRepository.saveQuizScore(context, score)
                                    isQuizFinished = true
                                }
                            }
                        },
                        enabled = selectedOptionIndex != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("btn_quiz_action"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrightGold)
                    ) {
                        Text(
                            text = if (!isSubmitted) "উত্তর যাচাই করুন" else if (currentQuestionIndex + 1 < questions.size) "পরবর্তী প্রশ্ন ➜" else "ফলাফল দেখুন 🏆",
                            color = DeepNavy,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // QUIZ FINISHED / CERTIFICATE & VIRAL SOCIAL CHALLENGE SHARE
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0x33D4AF37),
                            border = BorderStroke(2.dp, BrightGold),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = BrightGold,
                                    modifier = Modifier.size(42.dp)
                                )
                            }
                        }

                        Text(
                            text = "মা শা আল্লাহ! কুইজ সম্পন্ন হয়েছে",
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Score Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NavySurface,
                            border = BorderStroke(1.2.dp, GoldBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "আপনার অর্জিত স্কোর",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${HijriCalendarService.toBengaliDigits(score)} / ${HijriCalendarService.toBengaliDigits(questions.size)}",
                                    color = BrightGold,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                val badgeTitle = when (score) {
                                    5 -> "🏆 মুত্তাকী আলিম / পারফেক্ট জ্ঞান"
                                    4 -> "🌟 ইলমের সন্ধানী পথিক"
                                    3 -> "📖 দ্বীনি সচেতন ভাই/বোন"
                                    else -> "🌱 দ্বীন শিক্ষার্থী"
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldSuccess.copy(alpha = 0.2f),
                                    border = BorderStroke(0.8.dp, EmeraldSuccess)
                                ) {
                                    Text(
                                        text = badgeTitle,
                                        color = EmeraldSuccess,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        // VIRAL SHARE CHALLENGE BUTTON
                        Button(
                            onClick = {
                                val shareText = "আলহামদুলিল্লাহ! আজকের ইসলামিক কুইজে আমি ${HijriCalendarService.toBengaliDigits(score)}/${HijriCalendarService.toBengaliDigits(questions.size)} পেয়েছি! 🏆✨\n\nআপনি কি পারবেন আমার স্কোরকে ভাঙতে? এখনই 'ইসলামিক মাইন্ড' অ্যাপে পরীক্ষা দিন এবং আপনার দ্বীনি জ্ঞান যাচাই করুন! 📲"
                                ViralFeaturesRepository.shareTextToSocial(
                                    context = context,
                                    text = shareText,
                                    title = "বন্ধুদের সাথে ইসলামিক কুইজ স্কোর চ্যালেঞ্জ করুন"
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_share_quiz_challenge"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "WhatsApp ও ফেসবুকে বন্ধুদের চ্যালেঞ্জ জানান 🚀",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                currentQuestionIndex = 0
                                selectedOptionIndex = null
                                isSubmitted = false
                                score = 0
                                isQuizFinished = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, GoldBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "আবার খেলুন (নতুন প্রশ্নমালা)",
                                color = BrightGold,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
