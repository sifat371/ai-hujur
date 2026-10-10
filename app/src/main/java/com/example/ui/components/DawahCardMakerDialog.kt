package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.ViralFeaturesRepository
import com.example.ui.theme.*

/**
 * 1-Tap Viral Dawah & Social Story Card Generator.
 */
@Composable
fun DawahCardMakerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val templates = remember { ViralFeaturesRepository.dawahTemplates }

    var selectedTemplateIndex by remember { mutableIntStateOf(0) }
    var presenterName by remember { mutableStateOf("") }
    var selectedThemeIndex by remember { mutableIntStateOf(0) }

    val activeTemplate = templates[selectedTemplateIndex]

    // Themes
    val themes = listOf(
        Pair("গোল্ডেন মিডনাইট", listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0B1329))),
        Pair("রয়্যাল এমারেল্ড", listOf(Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF022C22))),
        Pair("কাবা রোজ", listOf(Color(0xFF4C0519), Color(0xFF831843), Color(0xFF2E020C))),
        Pair("ডিপ ওশান", listOf(Color(0xFF0C4A6E), Color(0xFF075985), Color(0xFF082F49)))
    )

    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Dawah Card", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "টেক্সট কপি হয়েছে! এখন সোশ্যাল মিডিয়ায় পেস্ট করুন।", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .testTag("dialog_dawah_card_maker"),
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
                                        listOf(Color(0xFFF43F5E), Color(0xFFBE123C))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "সোশ্যাল স্টোরি ও দাওয়াহ কার্ড",
                                color = BrightGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "১-ক্লিকে সদকায়ে জারিয়া স্ট্যাটাস তৈরি",
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

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // LIVE PREVIEW CARD (WYSIWYG)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.5.dp, BrightGold.copy(alpha = 0.8f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Brush.verticalGradient(themes[selectedThemeIndex].second))
                                .padding(18.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Top Badge & Category
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x33D4AF37),
                                        border = BorderStroke(0.6.dp, BrightGold.copy(alpha = 0.6f))
                                    ) {
                                        Text(
                                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                            color = BrightGold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0x22FFFFFF)
                                    ) {
                                        Text(
                                            text = activeTemplate.category,
                                            color = TextWhite,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Arabic Calligraphy
                                Text(
                                    text = activeTemplate.arabicCalligraphy,
                                    color = BrightGold,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 28.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                // Bengali Meaning
                                Text(
                                    text = activeTemplate.banglaTranslation,
                                    color = TextWhite,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp
                                )

                                // Reference & Reflection
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "— ${activeTemplate.referenceBn}",
                                        color = IslamicGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "\"${activeTemplate.reflectionBn}\"",
                                        color = TextLight.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 15.sp
                                    )
                                }

                                HorizontalDivider(
                                    color = BrightGold.copy(alpha = 0.3f),
                                    thickness = 0.8.dp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )

                                // Watermark & Presenter Name
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (presenterName.isNotBlank()) "দাওয়াহ প্রদানে: ${presenterName.trim()}" else "সদকায়ে জারিয়া হিসেবে শেয়ারকৃত",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "📱 ইসলামিক মাইন্ড অ্যাপ",
                                        color = BrightGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Theme Selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "কার্ডের কালার থিম পছন্দ করুন:",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            themes.forEachIndexed { idx, theme ->
                                val isSelected = selectedThemeIndex == idx
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = theme.second.first(),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) BrightGold else GoldBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clickable { selectedThemeIndex = idx }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = theme.first.split(" ").last(),
                                            color = if (isSelected) BrightGold else TextLight,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Choose Quote / Template
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "বাণী বা আয়াত নির্বাচন করুন:",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        templates.forEachIndexed { idx, tmpl ->
                            val isSelected = selectedTemplateIndex == idx
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0x33D4AF37) else NavySurface,
                                border = BorderStroke(
                                    if (isSelected) 1.2.dp else 0.6.dp,
                                    if (isSelected) BrightGold else GoldBorder.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTemplateIndex = idx }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) BrightGold else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tmpl.titleBn,
                                            color = if (isSelected) BrightGold else TextWhite,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = tmpl.banglaTranslation,
                                            color = TextLight,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Presenter Name Input
                    OutlinedTextField(
                        value = presenterName,
                        onValueChange = { presenterName = it },
                        label = { Text("আপনার নাম (ঐচ্ছিক)", color = TextMuted, fontSize = 11.sp) },
                        placeholder = { Text("দাওয়াহ প্রদানে আপনার নাম লিখুন", color = TextMuted, fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepNavy,
                            unfocusedContainerColor = DeepNavy,
                            focusedBorderColor = BrightGold,
                            unfocusedBorderColor = GoldBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextLight
                        )
                    )
                }

                // Action Buttons: 1-Tap Share to WhatsApp / Facebook & Copy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val cardFormattedText = """
${activeTemplate.arabicCalligraphy}
${activeTemplate.banglaTranslation}
— ${activeTemplate.referenceBn}

"${activeTemplate.reflectionBn}"
${if (presenterName.isNotBlank()) "দাওয়াহ প্রদানে: ${presenterName.trim()}\n" else ""}
📱 ইসলামিক মাইন্ড অ্যাপ | আপনিও দ্বীনি আলো ছড়ান
                            """.trimIndent()
                            copyToClipboard(cardFormattedText)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, BrightGold)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "কপি করুন",
                            color = BrightGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val shareMessage = """
✨ *পবিত্র কুরআন ও সুন্নাহর বাণী* ✨

${activeTemplate.arabicCalligraphy}
"${activeTemplate.banglaTranslation}"
— *${activeTemplate.referenceBn}*

💡 *তাদাব্বুর/উপলব্ধি:*
${activeTemplate.reflectionBn}
${if (presenterName.isNotBlank()) "\n🤲 *দাওয়াহ প্রদানে:* ${presenterName.trim()}" else ""}
___________________________
📲 *ইসলামিক মাইন্ড অ্যাপ* থেকে সংগৃহীত।
সদকায়ে জারিয়া হিসেবে আপনিও এই বাণী শেয়ার করুন! 🌸
                            """.trimIndent()

                            ViralFeaturesRepository.shareTextToSocial(
                                context = context,
                                text = shareMessage,
                                title = "হোয়াটসঅ্যাপ ও ফেসবুকে স্ট্যাটাস দিন"
                            )
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .testTag("btn_share_dawah_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "স্ট্যাটাসে শেয়ার 📲",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
