package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.ViralFeaturesRepository
import com.example.ui.theme.*

/**
 * Fajr Wakeup Buddy & Collective Ummah Salawat Milestone Dialog.
 */
@Composable
fun FajrBuddyDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val reminders = remember { ViralFeaturesRepository.fajrReminders }
    var selectedIndex by remember { mutableIntStateOf(0) }
    val activeReminder = reminders[selectedIndex]

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .testTag("dialog_fajr_buddy"),
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
                // Header
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
                                        listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WbTwilight,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "ফজর ও তাহাজ্জুদ ওয়েক-আপ ফ্রেন্ড",
                                color = BrightGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "প্রিয়জনদের ফজরের সালাতে জাগিয়ে তুলুন",
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

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Purpose Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NavySurface,
                        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "সদকায়ে জারিয়া ও অপরিসীম নেকি 🌟",
                                    color = BrightGold,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "আপনার একটি মেসেজের কারণে যদি কোনো ভাই/বোন ফজরের নামাজে জাগ্রত হয়, তবে তাঁর সমপরিমাণ সওয়াব আপনিও পাবেন ইনশাআল্লাহ!",
                                    color = TextLight,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "দাওয়াহ বার্তা নির্বাচন করুন:",
                        color = TextWhite,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    reminders.forEachIndexed { idx, item ->
                        val isSelected = selectedIndex == idx
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0x33D4AF37) else NavySurface,
                            border = BorderStroke(
                                if (isSelected) 1.2.dp else 0.6.dp,
                                if (isSelected) BrightGold else GoldBorder.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedIndex = idx }
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.titleBn,
                                        color = if (isSelected) BrightGold else TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x22FFFFFF)
                                    ) {
                                        Text(
                                            text = item.hadithProof,
                                            color = IslamicGold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = item.messageText,
                                    color = TextLight,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Action Button: Send to WhatsApp
                Button(
                    onClick = {
                        val fullMessage = """
${activeReminder.messageText}

📖 দলিল: ${activeReminder.hadithProof}
___________________________
📲 'ইসলামিক মাইন্ড' অ্যাপ থেকে প্রেরিত।
আপনিও আপনার বন্ধুদের ফজরের নামাজে আহ্বান করুন! 🕌
                        """.trimIndent()

                        ViralFeaturesRepository.shareTextToSocial(
                            context = context,
                            text = fullMessage,
                            title = "ফজরের দাওয়াহ মেসেজ পাঠান"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_send_fajr_whatsapp"),
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
                        text = "WhatsApp এ বন্ধুদের জাগিয়ে তুলুন 🌅",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
