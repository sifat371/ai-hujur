package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.data.repository.BadHabitRepository
import com.example.ui.theme.*

/**
 * Emergency SOS Modal Dialog for Satanic Waswasah & Lustful Temptations (ওয়াসওয়াসা প্রতিরোধের জরুরি পদক্ষেপ).
 */
@Composable
fun WaswasahSosDialog(
    onDismiss: () -> Unit,
    onConsultAi: (String) -> Unit
) {
    val context = LocalContext.current
    val emergencySteps = remember { BadHabitRepository.getEmergencySteps() }

    // Mild haptic feedback to ground the user
    LaunchedEffect(Unit) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .testTag("dialog_waswasah_sos"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(2.dp, Color(0xFFEF4444))
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
                                .background(Color(0xFFDC2626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "ওয়াসওয়াসা ইমার্জেন্সি SOS",
                                color = Color(0xFFFCA5A5),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "কুপ্রবৃত্তি ও শয়তানের প্ররোচনা প্রতিরোধ",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Calligraphy Banner: A'udhu Billah
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x33DC2626),
                    border = BorderStroke(1.2.dp, Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "\"আমি বিতাড়িত শয়তানের কুমন্ত্রণা থেকে আল্লাহর নিকট আশ্রয় প্রার্থনা করছি।\"",
                            color = BrightGold,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Scrollable 4 Instant Sunnah Steps
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "তাত্ক্ষণিক ৪টি সুন্নাহ আমল (এখনই কার্যকর করুন):",
                        color = BrightGold,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    emergencySteps.forEach { step ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = NavySurface,
                            border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFDC2626).copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, Color(0xFFDC2626)),
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${step.stepNumber}",
                                            color = Color(0xFFFCA5A5),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = step.titleBn,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = step.actionBn,
                                        color = TextLight,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0x18D4AF37)
                                    ) {
                                        Text(
                                            text = "দলিল: ${step.hadithProofBn}",
                                            color = IslamicGold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // AI Scholar Support Button
                Button(
                    onClick = {
                        onDismiss()
                        onConsultAi("মুহতারাম, আমার মনে এই মুহূর্তে তীব্র কুপ্রবৃত্তি ও গুনাহের ওয়াসওয়াসা এসেছে। আমাকে নফস দমন ও তাৎক্ষণিক আত্মশুদ্ধির পথ বাতলে দিন।")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrightGold)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = DeepNavy,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "এআই স্কলারের সাহায্য নিন (তাৎক্ষণিক সান্ত্বনা)",
                        color = DeepNavy,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
