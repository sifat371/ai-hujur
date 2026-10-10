package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AllahName
import com.example.data.model.QuranVerseOfTheDay
import com.example.data.model.ZakatCalculationResult
import com.example.data.repository.IslamicUniqueRepository
import com.example.ui.theme.*
import java.text.DecimalFormat

/**
 * 1. Daily Ayah Reflection Card
 */
@Composable
fun DailyAyahCard(
    verse: QuranVerseOfTheDay,
    modifier: Modifier = Modifier,
    onOpenQuranReader: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("daily_ayah_reflection_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0x22D4AF37),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "আজকের কুরআনিক আয়াত ও তাৎপর্য",
                            color = BrightGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${verse.surahNameBn} (আয়াত ${verse.ayahNumber})",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(
                            "Quran Ayah",
                            "${verse.arabicText}\n\n${verse.meaningBn}\n[${verse.surahNameBn}: ${verse.ayahNumber}]"
                        )
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "আয়াত ও অর্থ কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "কপি করুন",
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Arabic Ayah
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MidnightBlue.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, Color(0x22D4AF37))
            ) {
                Text(
                    text = verse.arabicText,
                    color = TextWhite,
                    fontSize = 19.sp,
                    lineHeight = 32.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(14.dp)
                )
            }

            // Bengali Pronunciation
            Text(
                text = "উচ্চারণ: ${verse.pronunciationBn}",
                color = TextLight.copy(alpha = 0.85f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            // Meaning
            Text(
                text = "অর্থ: \"${verse.meaningBn}\"",
                color = BrightGold,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 21.sp
            )

            // Expandable Spiritual Reflection
            AnimatedVisibility(visible = isExpanded) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x2210B981))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "জীবনঘনিষ্ঠ শিক্ষা ও আমল",
                                color = EmeraldSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = verse.reflectionBn,
                                color = TextLight,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // Bottom Actions Row (Expand prompt & Quran reader button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "সংক্ষেপ করুন ▲" else "আমল ও ব্যাখ্যা দেখতে ট্যাপ করুন ▼",
                    color = IslamicGold.copy(alpha = 0.8f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (onOpenQuranReader != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0x22D4AF37),
                        border = BorderStroke(0.8.dp, IslamicGold),
                        modifier = Modifier
                            .clickable { onOpenQuranReader() }
                            .testTag("button_open_quran_from_daily_ayah")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "কুরআন রিডার খুলুন",
                                color = BrightGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 2. Asmaul Husna (99 Names of Allah) Dialog
 */
@Composable
fun AsmaulHusnaDialog(onDismiss: () -> Unit) {
    val names = remember { IslamicUniqueRepository.asmaulHusnaList }
    var searchQuery by remember { mutableStateOf("") }

    val filteredNames = remember(searchQuery) {
        if (searchQuery.isBlank()) names
        else names.filter {
            it.arabic.contains(searchQuery.trim(), ignoreCase = true) ||
            it.transliteration.contains(searchQuery.trim(), ignoreCase = true) ||
            it.meaningBn.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("dialog_asmaul_husna"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "আসমাউল হুসনা (৯৯ নাম)",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "মহান আল্লাহর সুন্দরতম নামসমূহ ও ফজিলত",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("নাম বা অর্থ খুঁজুন...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = IslamicGold) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = GoldBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight
                    ),
                    singleLine = true
                )

                // List of Names
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredNames) { item ->
                        AllahNameItemCard(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun AllahNameItemCard(item: AllahName) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, Color(0x33D4AF37))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x22D4AF37),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${item.number}",
                                color = BrightGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Column {
                        Text(
                            text = item.transliteration,
                            color = TextWhite,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "অর্থ: ${item.meaningBn}",
                            color = BrightGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = item.arabic,
                    color = BrightGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = Color(0x22D4AF37), thickness = 0.8.dp)
                    Text(
                        text = "ব্যাখ্যা: ${item.explanationBn}",
                        color = TextLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NavySurface,
                        border = BorderStroke(0.8.dp, Color(0x2210B981))
                    ) {
                        Text(
                            text = "ফজিলত: ${item.spiritualBenefitBn}",
                            color = EmeraldSuccess,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3. Accurate Shariah Zakat Calculator Dialog
 */
@Composable
fun ZakatCalculatorDialog(onDismiss: () -> Unit) {
    var cashText by remember { mutableStateOf("") }
    var goldText by remember { mutableStateOf("") }
    var silverText by remember { mutableStateOf("") }
    var businessText by remember { mutableStateOf("") }
    var debtText by remember { mutableStateOf("") }

    val formatter = remember { DecimalFormat("#,##,###") }

    val cash = cashText.toDoubleOrNull() ?: 0.0
    val gold = goldText.toDoubleOrNull() ?: 0.0
    val silver = silverText.toDoubleOrNull() ?: 0.0
    val business = businessText.toDoubleOrNull() ?: 0.0
    val debts = debtText.toDoubleOrNull() ?: 0.0

    val result = remember(cash, gold, silver, business, debts) {
        ZakatCalculationResult(
            cashInHandAndBank = cash,
            goldValue = gold,
            silverValue = silver,
            businessGoodsValue = business,
            immediateDebtsOwed = debts
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .testTag("dialog_zakat_calculator"),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "শারঈ যাকাত ক্যালকুলেটর",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "হিসাব ও নিসাব পরিমাপ (২.৫% হারে)",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                // Summary Display Card
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MidnightBlue,
                    border = BorderStroke(1.2.dp, if (result.isZakatObligatory) IslamicGold else GoldBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (result.isZakatObligatory) "আপনার যাকাত প্রযোজ্য হয়েছে" else "যাকাতের নিসাব পৌঁছেনি",
                            color = if (result.isZakatObligatory) EmeraldSuccess else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "৳ ${formatter.format(result.zakatPayableBdt)}",
                            color = BrightGold,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "মোট যাকাতযোগ্য সম্পদ: ৳ ${formatter.format(result.netZakatEligibleWealth)}",
                            color = TextLight,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "রৌপ্য নিসাব সীমা: ৳ ${formatter.format(result.nisabThresholdSilverBdt)} (প্রায় ৫২.৫ তোলা)",
                            color = TextMuted,
                            fontSize = 10.5.sp
                        )
                    }
                }

                Text(
                    text = "আপনার সম্পদ ও ঋণের তথ্য প্রদান করুন:",
                    color = BrightGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                ZakatInputField(
                    label = "নগদ টাকা ও ব্যাংক ব্যালেন্স (৳)",
                    value = cashText,
                    onValueChange = { cashText = it }
                )

                ZakatInputField(
                    label = "স্বর্ণের বাজারমূল্য (৳)",
                    value = goldText,
                    onValueChange = { goldText = it }
                )

                ZakatInputField(
                    label = "রৌপ্যের বাজারমূল্য (৳)",
                    value = silverText,
                    onValueChange = { silverText = it }
                )

                ZakatInputField(
                    label = "ব্যবসার পণ্য ও বিক্রয়যোগ্য মজুদ (৳)",
                    value = businessText,
                    onValueChange = { businessText = it }
                )

                ZakatInputField(
                    label = "তাৎক্ষণিক পরিশোধযোগ্য ঋণ (৳)",
                    value = debtText,
                    onValueChange = { debtText = it },
                    isDeduction = true
                )

                // Shariah note
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, Color(0x22D4AF37))
                ) {
                    Text(
                        text = "শারঈ জ্ঞাতব্য: নিসাব পরিমাণ সম্পদের মালিক হয়ে এক চান্দ্রবছর অতিবাহিত হলে মূল সম্পদের ২.৫% (৪০ ভাগের ১ ভাগ) হকদারদের প্রদান করা ফরজ। ব্যক্তিগত ব্যবহারের আসবাব, বাড়ি ও পরিবহনে যাকাত নেই।",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Reset button
                OutlinedButton(
                    onClick = {
                        cashText = ""
                        goldText = ""
                        silverText = ""
                        businessText = ""
                        debtText = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GoldBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextLight)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("সব রিসেট করুন", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ZakatInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isDeduction: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            if (input.all { it.isDigit() || it == '.' }) {
                onValueChange(input)
            }
        },
        label = { Text(label, fontSize = 12.sp, color = if (isDeduction) CoralAccent else IslamicGold) },
        placeholder = { Text("০", color = TextMuted) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = NavySurface,
            unfocusedContainerColor = NavySurface,
            focusedBorderColor = if (isDeduction) CoralAccent else IslamicGold,
            unfocusedBorderColor = GoldBorder,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextLight
        ),
        singleLine = true
    )
}
