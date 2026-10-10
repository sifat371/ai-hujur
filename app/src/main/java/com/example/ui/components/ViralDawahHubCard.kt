package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ViralFeaturesRepository
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*

/**
 * Viral Growth & Continuous Dawah Hub Card for Home Screen.
 * Contains: Daily Quiz, Dawah Card Maker, Fajr Buddy, and Ummah Salawat Milestone.
 */
@Composable
fun ViralDawahHubCard(
    onOpenQuiz: () -> Unit,
    onOpenDawahCardMaker: () -> Unit,
    onOpenFajrBuddy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var myDuroodCount by remember { mutableIntStateOf(ViralFeaturesRepository.getMyDuroodCount(context)) }
    val (communityDurood, duroodTarget) = remember(myDuroodCount) {
        ViralFeaturesRepository.getUmmahDuroodProgress(context)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(22.dp))
            .testTag("viral_dawah_hub_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.2.dp, BrightGold.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(BrightGold, WarmGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = DeepNavy,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "সদকায়ে জারিয়া ও দাওয়াহ হাব",
                            color = BrightGold,
                            fontSize = 15.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "দ্বীনি আলো ছড়ান ও সওয়াবের অংশীদার হোন",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldSuccess.copy(alpha = 0.2f),
                    border = BorderStroke(0.6.dp, EmeraldSuccess)
                ) {
                    Text(
                        text = "ভাইরাল দাওয়াহ 🚀",
                        color = EmeraldSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // Dual Viral Main Action Banners: 1. Quiz Challenge, 2. Status Card Generator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item 1: Islamic Quiz
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenQuiz() }
                        .testTag("btn_hub_daily_quiz")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33D4AF37)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "দৈনিক ইসলামিক কুইজ",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "জ্ঞান পরীক্ষা ও বন্ধুদের চ্যালেঞ্জ জানান 🏆",
                            color = TextLight.copy(alpha = 0.85f),
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Item 2: Dawah Story Maker
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x44F43F5E)),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenDawahCardMaker() }
                        .testTag("btn_hub_dawah_card_maker")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x33F43F5E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = null,
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "স্টোরি ও কার্ড মেকার",
                            color = TextWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "১-ক্লিকে স্ট্যাটাস ও সোশ্যাল পোস্ট বানান 📲",
                            color = TextLight.copy(alpha = 0.85f),
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            // Item 3: Fajr Wake-up Call Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = DeepNavy,
                border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onOpenFajrBuddy() }
                    .testTag("btn_hub_fajr_buddy")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0x2238BDF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.WbTwilight,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ফজর ও তাহাজ্জুদ ওয়েক-আপ ফ্রেন্ড 🌅",
                            color = BrightGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "হোয়াটসঅ্যাপে প্রিয়জনকে ফজরের নামাজে আহ্বান করুন",
                            color = TextLight,
                            fontSize = 10.5.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Item 4: Global Ummah Salawat Milestone Bar
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x18D4AF37),
                border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "📿 উম্মাহ দরূদ চেইন:",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "আজকের লক্ষ্য ১,০০,০০০ বার",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                        }

                        Text(
                            text = "${HijriCalendarService.toBengaliDigits(communityDurood)} বার পঠিত",
                            color = BrightGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { (communityDurood.toFloat() / duroodTarget).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = BrightGold,
                        trackColor = Color(0x22D4AF37)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "আপনার অবদান: ${HijriCalendarService.toBengaliDigits(myDuroodCount)} বার",
                            color = TextLight,
                            fontSize = 11.sp
                        )

                        // 1-Tap Add Recitation
                        Button(
                            onClick = {
                                myDuroodCount = ViralFeaturesRepository.addMyDuroodCount(context, 10)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrightGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = "+১০ দরূদ যুক্ত করুন",
                                color = DeepNavy,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
