package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.PrivacyPolicySummaryCard
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun ProfileScreen(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val spiritualGoal by viewModel.spiritualGoal.collectAsState()
    val prayerNotifications by viewModel.prayerNotificationsEnabled.collectAsState()
    val calculationMethod by viewModel.calculationMethod.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(MidnightBlue, DeepNavy, Color(0xFF070E22))
                )
            )
    ) {
        IslamicGeometricBackground(alpha = 0.035f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "প্রোফাইল ও সেটিংস",
                color = IslamicGold,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            // User Profile Card
            UserProfileCard(
                name = userName,
                goal = spiritualGoal,
                onEditClick = { showEditProfileDialog = true }
            )

            // Spiritual Stats / Streak Card
            SpiritualStatsCard()

            // Notification & Reminder Toggles Section
            TogglesSettingsCard(
                prayerNotifications = prayerNotifications,
                onTogglePrayerNotifications = { viewModel.togglePrayerNotifications() },
                onSendTestAlert = { viewModel.sendTestPrayerAlert() },

            )

            // Privacy Policy & Security Card
            PrivacyPolicySummaryCard(
                onViewPrivacyPolicy = { showPrivacyPolicyDialog = true }
            )

            // App & Scholar AI Info Card
            AboutAppCard(
                onViewPrivacyPolicy = { showPrivacyPolicyDialog = true }
            )
        }
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userName,
            currentGoal = spiritualGoal,
            currentMethod = calculationMethod,
            onDismiss = { showEditProfileDialog = false },
            onSave = { name, goal, method ->
                viewModel.updateProfile(name, goal, method)
                showEditProfileDialog = false
            }
        )
    }

    if (showPrivacyPolicyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }
}

@Composable
private fun UserProfileCard(
    name: String,
    goal: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_profile_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(NavySurface)
                        .border(2.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = name,
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "রমজানের লক্ষ্য: $goal",
                        color = IslamicGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "আল-হুজুর সদস্য • ১৪৪৭ হিজরি",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.testTag("edit_profile_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "প্রোফাইল সম্পাদনা",
                    tint = BrightGold
                )
            }
        }
    }
}

@Composable
private fun SpiritualStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(value = "১৪ দিন", label = "রোজার ধারাবাহিকতা", icon = Icons.Default.LocalFireDepartment, tint = BrightGold)
            StatItem(value = "৭০ / ৭০", label = "ওয়াক্ত নামাজ", icon = Icons.Default.CheckCircle, tint = EmeraldSuccess)
            StatItem(value = "১৫ পারা", label = "কুরআন পাঠ", icon = Icons.AutoMirrored.Filled.MenuBook, tint = CoralAccent)
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        Text(text = value, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = TextMuted, fontSize = 10.5.sp)
    }
}

@Composable
private fun TogglesSettingsCard(
    prayerNotifications: Boolean,
    onTogglePrayerNotifications: () -> Unit,
    onSendTestAlert: () -> Unit
) {
    var testAlertSent by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("settings_toggles_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "নোটিফিকেশন ও পছন্দসমূহ",
                color = BrightGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // 10-Minute Prayer Notifications Toggle
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTogglePrayerNotifications() },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (prayerNotifications) BrightGold else IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "১০ মিনিট পূর্বে নামাজের সতর্কতা",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "প্রতিটি ওয়াক্ত শুরু হওয়ার ১০ মিনিট আগে স্থানীয় নোটিফিকেশন",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = prayerNotifications,
                        onCheckedChange = { onTogglePrayerNotifications() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MidnightBlue,
                            checkedTrackColor = IslamicGold,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = NavySurface
                        ),
                        modifier = Modifier.testTag("toggle_prayer_notifications")
                    )
                }

                // Test Notification Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            onSendTestAlert()
                            testAlertSent = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, IslamicGold.copy(alpha = 0.7f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BrightGold
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("button_profile_test_notification")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "টেস্ট নোটিফিকেশন পাঠান",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (testAlertSent) {
                        Text(
                            text = "✓ টেস্ট নোটিফিকেশন পাঠানো হয়েছে",
                            color = EmeraldSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0x22D4AF37))

            // The application currently only implements 18° twilight angles
            // with Hanafi Asr. Do not offer fake selection of unsupported methods.
            Text(
                text = "বর্তমান গণনা: ফজর ও এশা ১৮°; আসর হানাফি পদ্ধতি",
                color = TextLight,
                fontSize = 13.sp
            )
            Text(
                text = "সময়গুলো আনুমানিক। স্থানীয় কর্তৃপক্ষের প্রকাশিত সময় যাচাই করুন।",
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun AboutAppCard(
    onViewPrivacyPolicy: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.dp, Color(0x22D4AF37))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "আল-হুজুর এআই সম্পর্কে",
                    color = BrightGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "আল-হুজুর এআই হলো বাংলাদেশের মুসলিমদের জন্য আধুনিক ইসলামিক সহকারী অ্যাপ। ইসলামিক প্রশ্নে এআই-সহায়ক তথ্য, আনুমানিক নামাজের সময়সূচি, ডিজিটাল তাসবিহ, কিবলা কম্পাস ও আমল ট্র্যাকারসহ পরিপূর্ণ অভিজ্ঞতা প্রদান করে।",
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ভার্সন ২.০ • গুগল এআই স্টুডিও",
                    color = IslamicGold.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
                TextButton(
                    onClick = onViewPrivacyPolicy,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "প্রাইভেসি পলিসি",
                        color = BrightGold,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentGoal: String,
    currentMethod: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var goal by remember { mutableStateOf(currentGoal) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "প্রোফাইল সম্পাদনা",
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("আপনার নাম", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("রমজানের আমলের লক্ষ্য", color = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(name, goal, currentMethod)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
