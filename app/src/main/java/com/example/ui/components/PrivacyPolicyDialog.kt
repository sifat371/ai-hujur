package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*

@Composable
fun PrivacyPolicySummaryCard(
    onViewPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("privacy_policy_card")
            .clickable { onViewPrivacyPolicy() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NavySurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "প্রাইভেসি আইকন",
                            tint = BrightGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "গোপনীয়তা নীতি ও নিরাপত্তা",
                            color = BrightGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Privacy Policy • Google Play Compliant",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "বিস্তারিত দেখুন",
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "আপনার আমল ও ব্যক্তিগত তথ্য সম্পূর্ণ নিরাপদ। নামাজের সময়সূচির জন্য ব্যবহৃত অবস্থানের তথ্য কোনো সার্ভারে সংরক্ষিত বা বিক্রি করা হয় না।",
                color = TextLight,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            OutlinedButton(
                onClick = onViewPrivacyPolicy,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.7f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_view_privacy_policy")
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "সম্পূর্ণ প্রাইভেসি পলিসি পড়ুন",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val contactEmail = "mdhasibulhasanofficial@gmail.com"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.88f)
                .testTag("privacy_policy_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dialog Header
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NavySurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "গোপনীয়তা নীতি",
                                color = BrightGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Privacy Policy • আল-হুজুর এআই",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_privacy_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = TextLight
                        )
                    }
                }

                HorizontalDivider(
                    color = Color(0x33D4AF37),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Badge info
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurface,
                        border = BorderStroke(1.dp, Color(0x3310B981))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "ব্যবহারকারীর তথ্যের সর্বোচ্চ নিরাপত্তা এবং গুগল প্লে পলিসি অনুসরণ করে তৈরি।",
                                color = TextLight,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    PolicySectionItem(
                        icon = Icons.Default.LocationOn,
                        title = "১. অবস্থানের তথ্যের ব্যবহার (Location Data)",
                        description = "নামাজের ওয়াক্তের সঠিক সময়সূচি ও কিবলা কম্পাসের দিক নির্ণয়ে আপনার বর্তমান অবস্থান ব্যবহৃত হয়। এই তথ্য ক্ষণস্থায়ীভাবে ডিভাইসে প্রসেস করা হয়; কোনো বাহ্যিক সার্ভারে আপলোড বা সংরক্ষণ করা হয় না।"
                    )

                    PolicySectionItem(
                        icon = Icons.Default.NotificationsActive,
                        title = "২. অ্যালার্ম ও নোটিফিকেশন (Notifications)",
                        description = "নামাজের ওয়াক্তের ১০ মিনিট আগে সতর্কতা ও সেহরি-ইফতারের স্মারক পাঠাতে লোকাল নোটিফিকেশন ব্যবহৃত হয়। অ্যাপ বা মোবাইল সেটিংস থেকে এই সুবিধা নিয়ন্ত্রণযোগ্য।"
                    )

                    PolicySectionItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "৩. এআই স্কলার ও গুগল জেমিনাই (Scholar AI)",
                        description = "ধর্মীয় প্রশ্নের প্রামাণ্য উত্তর প্রদানে গুগল জেমিনাই এআই মডেল ব্যবহৃত হয়। আপনার কোনো ব্যক্তিগত তথ্য বা পরিচয় এতে সংযুক্ত করা হয় না।"
                    )

                    PolicySectionItem(
                        icon = Icons.Default.Storage,
                        title = "৪. লোকাল ডাটাবেজ সংরক্ষণ (Local Storage)",
                        description = "আপনার আমল ট্র্যাকার, ডিজিটাল তাসবিহ ও ব্যক্তিগত লক্ষ্যসমূহ শুধুমাত্র আপনার ডিভাইসের সুরক্ষিত লোকাল ডাটাবেজে (Room SQLite) সংরক্ষিত থাকে।"
                    )

                    PolicySectionItem(
                        icon = Icons.Default.GppGood,
                        title = "৫. তথ্য সুরক্ষা ও নো-শেয়ারিং (No Data Selling)",
                        description = "আমরা কোনো ব্যবহারকারীর ব্যক্তিগত তথ্য তৃতীয় কোনো বিজ্ঞাপনদাতা বা ডাটা ব্রোকারের কাছে বিক্রি, ভাড়া বা শেয়ার করি না।"
                    )

                    PolicySectionItem(
                        icon = Icons.Default.DeleteOutline,
                        title = "৬. তথ্য মুছে ফেলা (Data Rights & Deletion)",
                        description = "প্রোফাইল থেকে লক্ষ্য পরিবর্তন অথবা মোবাইলের অ্যাপ সেটিংস থেকে 'Clear Storage' বা আনইনস্টল করে সমস্ত লোকাল তথ্য মুছে ফেলা সম্ভব।"
                    )

                    // Contact Section
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, Color(0x33D4AF37)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "যোগাযোগ ও প্রশ্ন (Contact Us)",
                                color = BrightGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "প্রাইভেসি পলিসি সংক্রান্ত যেকোনো বিষয়ে সরাসরি যোগাযোগ করুন:",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                            Text(
                                text = contactEmail,
                                color = IslamicGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            OutlinedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(contactEmail))
                                    Toast.makeText(context, "ইমেইল কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = BrightGold),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ইমেইল কপি করুন", fontSize = 11.sp)
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = Color(0x33D4AF37),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Dialog Footer Action
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_dismiss_privacy_dialog")
                ) {
                    Text(
                        text = "বুঝেছি / বন্ধ করুন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicySectionItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NavySurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrightGold,
                modifier = Modifier.size(16.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = BrightGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = description,
                color = TextLight,
                fontSize = 11.5.sp,
                lineHeight = 16.5.sp
            )
        }
    }
}
