package com.example.ui.components

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DailyNasihot
import com.example.data.model.QuranVerseOfTheDay
import com.example.data.service.HijriCalendarService
import com.example.ui.theme.*

/**
 * Al-Hujur AI Scholar & Continuously Updating Single Daily Wisdom (Ayah or Hadith).
 * Displays exactly ONE focused sacred wisdom at a time (Ayat or Hadith), updating continuously,
 * with an interactive AI Scholar questioning interface ready to answer any Islamic inquiry.
 */
@Composable
fun AlHujurUnifiedWisdomCard(
    nasihot: DailyNasihot,
    verse: QuranVerseOfTheDay,
    currentIndex: Int,
    totalCount: Int,
    onNextWisdom: () -> Unit,
    onPreviousWisdom: () -> Unit,
    onOpenQuranReader: () -> Unit,
    onAskAiClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAskQuestionWithPrompt: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    // 0: Quranic Ayah, 1: Hadith / Sunnah Wisdom
    var displayMode by remember { mutableStateOf(0) }
    var questionInput by remember { mutableStateOf("") }
    var isTadabburExpanded by remember { mutableStateOf(false) }
    var showViralStatusDialog by remember { mutableStateOf(false) }
    var sunnahCompleted by remember { mutableStateOf(false) }

    // Speech-to-Text Recognition Launcher for Voice Questions
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                questionInput = spokenText
                if (onAskQuestionWithPrompt != null) {
                    onAskQuestionWithPrompt(spokenText)
                } else {
                    onAskAiClick()
                }
            }
        }
    }

    val launchVoiceRecognition = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "bn-BD")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "আল-হুজুর শুনছেন... আপনার দ্বীনি প্রশ্নটি মুখে বলুন")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "ভয়েস সার্ভিস সক্রিয় নয়, নিচে লিখে প্রশ্ন করুন", Toast.LENGTH_SHORT).show()
        }
    }

    // High-interest viral Islamic questions frequently asked by Muslims
    val quickQuestions = remember {
        listOf(
            "রোজা রেখে পেস্ট ব্যবহার করার বিধান?",
            "চোখে বা কানে ড্রপ দিলে কি রোজা ভাঙে?",
            "তাহাজ্জুদের শ্রেষ্ঠ ফজিলত ও নিয়ম",
            "লাইলাতুল কদরের দোয়া ও বিশেষ আমল",
            "ঋণগ্রস্ত ব্যক্তির ওপর কি জাকাত ফরজ?",
            "মনের সকল পেরেশানি ও ঋণমুক্তির দোয়া",
            "সদকাতুল ফিতর কার ওপর ওয়াজিব?"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(26.dp))
            .testTag("unified_al_hujur_wisdom_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = DeepNavy),
        border = BorderStroke(1.5.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // --- 1. SCHOLAR HEADER & ACTIVE STATUS ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(modifier = Modifier.size(46.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = NavySurface,
                            border = BorderStroke(1.5.dp, IslamicGold),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "আল-হুজুর এআই স্কলার",
                                    tint = BrightGold,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        // Live green pulse badge
                        Surface(
                            shape = CircleShape,
                            color = EmeraldSuccess,
                            border = BorderStroke(1.5.dp, DeepNavy),
                            modifier = Modifier
                                .size(13.dp)
                                .align(Alignment.BottomEnd)
                        ) {}
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "আল-হুজুর এআই স্কলার",
                                color = TextWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldContainer
                            ) {
                                Text(
                                    text = "অনলাইন মুফতি",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "কুরআন ও সহীহ সুন্নাহ ভিত্তিক সকল প্রশ্নের উত্তর প্রস্তুত",
                            color = TextMuted,
                            fontSize = 11.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // --- 2. INTEGRATED AI SCHOLAR QUESTIONING INTERFACE (এআই স্কলারকে সরাসরি প্রশ্ন করুন - ভয়েস ও চ্যাট) ---
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NavySurface,
                border = BorderStroke(1.2.dp, IslamicGold.copy(alpha = 0.7f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            Icon(
                                imageVector = Icons.Outlined.QuestionAnswer,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "এআই স্কলারকে সরাসরি প্রশ্ন করুন",
                                color = BrightGold,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0x22D4AF37)
                        ) {
                            Text(
                                text = "ভয়েস ও চ্যাট সক্রিয়",
                                color = IslamicGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Question Input Field with Mic & Send Buttons
                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        placeholder = {
                            Text(
                                text = "মুখে বলুন বা প্রশ্ন লিখুন...",
                                color = TextMuted,
                                fontSize = 12.5.sp
                            )
                        },
                        trailingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                // Microphone / Voice Input Button
                                IconButton(
                                    onClick = launchVoiceRecognition,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("btn_voice_input_inline")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Mic,
                                        contentDescription = "ভয়েসে বলুন",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Send Button
                                IconButton(
                                    onClick = {
                                        if (questionInput.isNotBlank()) {
                                            val q = questionInput.trim()
                                            questionInput = ""
                                            if (onAskQuestionWithPrompt != null) {
                                                onAskQuestionWithPrompt(q)
                                            } else {
                                                onAskAiClick()
                                            }
                                        } else {
                                            onAskAiClick()
                                        }
                                    },
                                    modifier = Modifier
                                        .size(34.dp)
                                        .testTag("btn_send_scholar_question")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "জিজ্ঞাসা করুন",
                                        tint = BrightGold,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_scholar_question"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DeepNavy,
                            unfocusedContainerColor = DeepNavy,
                            focusedBorderColor = BrightGold,
                            unfocusedBorderColor = GoldBorder,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextLight
                        ),
                        singleLine = true
                    )

                    // Dual Direct Action Buttons: 1. Voice Question, 2. Chat Discussion
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Button A: Direct Voice Question
                        Button(
                            onClick = launchVoiceRecognition,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_direct_voice_scholar"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(17.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "ভয়েসে প্রশ্ন 🎙️",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Button B: Direct Full Chat Discussion
                        Button(
                            onClick = onAskAiClick,
                            modifier = Modifier
                                .weight(1.2f)
                                .testTag("btn_open_full_ai_chat"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrightGold),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = DeepNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "চ্যাটে আলোচনা 💬",
                                color = DeepNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Quick Suggested Prompts Scrollable Row
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "ট্রেন্ডিং প্রশ্নসমূহ (ট্যাপ করে তাৎক্ষণিক জানুন):",
                            color = TextMuted,
                            fontSize = 11.sp
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickQuestions.forEach { prompt ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0x18D4AF37),
                                    border = BorderStroke(0.6.dp, Color(0x44D4AF37)),
                                    modifier = Modifier
                                        .clickable {
                                            if (onAskQuestionWithPrompt != null) {
                                                onAskQuestionWithPrompt(prompt)
                                            } else {
                                                onAskAiClick()
                                            }
                                        }
                                        .testTag("chip_prompt_$prompt")
                                ) {
                                    Text(
                                        text = prompt,
                                        color = TextLight,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 3. SWITCHABLE SINGLE WISDOM TOGGLE CHIPS (পবিত্র বাণী ও নাসীহত) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selector Tabs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (displayMode == 0) IslamicGold else NavySurface,
                        border = BorderStroke(1.dp, if (displayMode == 0) BrightGold else Color(0x33D4AF37)),
                        modifier = Modifier
                            .clickable { displayMode = 0 }
                            .testTag("tab_quran_verse")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = if (displayMode == 0) DeepNavy else IslamicGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "পবিত্র কুরআন",
                                color = if (displayMode == 0) DeepNavy else TextLight,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (displayMode == 1) IslamicGold else NavySurface,
                        border = BorderStroke(1.dp, if (displayMode == 1) BrightGold else Color(0x33D4AF37)),
                        modifier = Modifier
                            .clickable { displayMode = 1 }
                            .testTag("tab_hadith_wisdom")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = if (displayMode == 1) DeepNavy else IslamicGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "সহীহ হাদিস ও নাসীহত",
                                color = if (displayMode == 1) DeepNavy else TextLight,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Previous & Next Arrows to manually rotate wisdom
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPreviousWisdom,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("button_prev_unified_wisdom")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "পূর্ববর্তী",
                            tint = BrightGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0x22D4AF37)
                    ) {
                        Text(
                            text = "${HijriCalendarService.toBengaliDigits(currentIndex + 1)}/${HijriCalendarService.toBengaliDigits(totalCount)}",
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = onNextWisdom,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("button_next_unified_wisdom")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "পরবর্তী",
                            tint = BrightGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // --- 3. SINGLE FOCUSED SACRED CONTENT (Either Quranic Verse OR Hadith, Never Both!) ---
            AnimatedContent(
                targetState = displayMode to (if (displayMode == 0) verse else nasihot),
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "SingleWisdomAnimation"
            ) { (mode, content) ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MidnightBlue.copy(alpha = 0.85f),
                    border = BorderStroke(1.2.dp, Color(0x44D4AF37)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (mode == 0) {
                            // --- OPTION A: QURANIC AYAH ---
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = BrightGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "আজকের পবিত্র কুরআনিক আয়াত",
                                        color = BrightGold,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x22D4AF37),
                                    border = BorderStroke(0.8.dp, IslamicGold.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "${verse.surahNameBn} : আয়াত ${verse.ayahNumber}",
                                        color = BrightGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Sacred Arabic Verse
                            Text(
                                text = verse.arabicText,
                                color = TextWhite,
                                fontSize = 19.sp,
                                lineHeight = 32.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            )

                            // Bengali Meaning
                            Text(
                                text = "অর্থ: \"${verse.meaningBn}\"",
                                color = TextLight,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 20.sp
                            )

                            // Bengali Pronunciation
                            Text(
                                text = "উচ্চারণ: ${verse.pronunciationBn}",
                                color = TextMuted,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )

                            // Expandable Tadabbur Reflection
                            if (verse.reflectionBn.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0x18D4AF37),
                                    border = BorderStroke(0.6.dp, Color(0x33D4AF37)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { isTadabburExpanded = !isTadabburExpanded }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "💡 কুরআনের আত্মিক শিক্ষা ও তাদাব্বুর",
                                                color = EmeraldSuccess,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (isTadabburExpanded) "সংক্ষেপ করুন ▲" else "দেখুন ▼",
                                                color = IslamicGold,
                                                fontSize = 11.sp
                                            )
                                        }
                                        AnimatedVisibility(visible = isTadabburExpanded) {
                                            Text(
                                                text = verse.reflectionBn,
                                                color = TextLight,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp,
                                                modifier = Modifier.padding(top = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // --- OPTION B: HADITH / SUNNAH WISDOM ---
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = BrightGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = nasihot.title,
                                        color = BrightGold,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldContainer
                                ) {
                                    Text(
                                        text = nasihot.category,
                                        color = EmeraldSuccess,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                    )
                                }
                            }

                            Text(
                                text = nasihot.advice,
                                color = TextWhite,
                                fontSize = 14.sp,
                                lineHeight = 21.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📜 রেফারেন্স: ${nasihot.reference}",
                                    color = IslamicGold,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "লাইভ আপডেট",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Bottom Actions: Quran Reader (if Ayah) or Copy & Share
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (mode == 0) {
                                TextButton(
                                    onClick = onOpenQuranReader,
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = BrightGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "কুরআন রিডার খুলুন",
                                        color = BrightGold,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Viral Status Card Generator Button
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0x22D4AF37),
                                    border = BorderStroke(0.8.dp, BrightGold),
                                    modifier = Modifier.clickable { showViralStatusDialog = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = null,
                                            tint = BrightGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "স্ট্যাটাস কার্ড 📲",
                                            color = BrightGold,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        val text = if (mode == 0) {
                                            "📖 ${verse.surahNameBn} (আয়াত ${verse.ayahNumber})\n\n${verse.arabicText}\n\nঅর্থ: \"${verse.meaningBn}\"\n\n— Al-Hujur AI"
                                        } else {
                                            "📜 ${nasihot.title}\n\n${nasihot.advice}\n\nরেফারেন্স: ${nasihot.reference}\n— Al-Hujur AI"
                                        }
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        clipboard?.setPrimaryClip(ClipData.newPlainText("Islamic Wisdom", text))
                                        Toast.makeText(context, "বাণী কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.ContentCopy,
                                        contentDescription = "কপি",
                                        tint = TextLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val text = if (mode == 0) {
                                            "📖 ${verse.surahNameBn} (আয়াত ${verse.ayahNumber})\n\n${verse.arabicText}\n\nঅর্থ: \"${verse.meaningBn}\"\n\n— Al-Hujur AI"
                                        } else {
                                            "📜 ${nasihot.title}\n\n${nasihot.advice}\n\nরেফারেন্স: ${nasihot.reference}\n— Al-Hujur AI"
                                        }
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, text)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "শেয়ার করুন"))
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = "শেয়ার",
                                        tint = TextLight,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- 5. VIRAL FEATURE: DAILY SUNNAH CHALLENGE & 3-DAY AMAL STREAK (অ্যাপ ভাইরাল করার বিশেষ ফিচার) ---
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x16D4AF37),
                border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
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
                                text = "🔥 ৩ দিনের ইবাদত স্ট্রিক",
                                color = BrightGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldContainer
                        ) {
                            Text(
                                text = "আজকের সুন্নত আমল",
                                color = EmeraldSuccess,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "✨ আজকের আমল: \"আজ অন্তত ৩ জন মুসলমানকে আন্তরিক হাসিমুখে সালাম দিন এবং ৫ বার 'আস্তাগফিরুল্লাহ' পাঠ করুন।\"",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 17.5.sp
                    )

                    Button(
                        onClick = {
                            sunnahCompleted = true
                            val shareText = "🕌 আলহামদুলিল্লাহ! আমি আল-হুজুর অ্যাপের 'আজকের সুন্নত আমল চ্যালেঞ্জ' সম্পন্ন করেছি।\n\n\"আমল: আজ অন্তত ৩ জন মুসলমানকে হাসিমুখে সালাম দিন এবং ৫ বার 'আস্তাগফিরুল্লাহ' পাঠ করুন।\"\n\nআপনিও অংশ নিন এবং ৩ দিনের ইবাদত স্ট্রিক শুরু করুন:\n✨ আল-হুজুর এআই • ইসলামিক জ্ঞান ও আমল সঙ্গী"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "আমল চ্যালেঞ্জ বন্ধুদের সাথে শেয়ার করুন"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (sunnahCompleted) EmeraldSuccess else NavySurface
                        ),
                        border = BorderStroke(1.dp, if (sunnahCompleted) EmeraldSuccess else IslamicGold),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (sunnahCompleted) Icons.Default.CheckCircle else Icons.Default.Stars,
                            contentDescription = null,
                            tint = if (sunnahCompleted) Color.White else BrightGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (sunnahCompleted) "✅ আলহামদুলিল্লাহ সম্পন্ন! • বন্ধুদের চ্যালেঞ্জ জানান 📲" else "🏆 আমল সম্পন্ন করেছি • বন্ধুদের চ্যালেঞ্জ জানান 📲",
                            color = if (sunnahCompleted) Color.White else BrightGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- 6. SADAQAH JARIYAH VIRAL REFERRAL BANNER (সাদাকায়ে জারিয়া শেয়ার) ---
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0x1410B981),
                border = BorderStroke(0.8.dp, EmeraldSuccess.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val shareMsg = "রাসূলুল্লাহ (সা.) বলেছেন: 'যে ব্যক্তি কোনো ভালো কাজের পথ দেখায়, সে ওই কাজ সম্পাদনকারীর সমান সওয়াব পাবে।' (সহীহ মুসলিম)\n\nদ্বীনি জীবন সহজ করতে আল-হুজুর এআই অ্যাপ ব্যবহার করুন। ৩-ইন-১ হিজরি ক্যালেন্ডার, লাইভ সেহরি-ইফতার, এআই মুফতি পরামর্শ ও আমল ট্র্যাকার সব একসাথে!\n\n📲 আল-হুজুর এআই"
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareMsg)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "সাদাকায়ে জারিয়া হিসেবে অ্যাপটি শেয়ার করুন"))
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "সাদাকায়ে জারিয়ার সওয়াব অর্জন করুন",
                                color = EmeraldSuccess,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "দ্বীনি বন্ধুদের সাথে অ্যাপটি শেয়ার করে সমপরিমাণ নেকি অর্জন করুন",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldSuccess,
                        modifier = Modifier.padding(start = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "শেয়ার 🤲",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // --- VIRAL ISLAMIC STATUS POSTER MODAL DIALOG ---
    if (showViralStatusDialog) {
        ViralIslamicStatusDialog(
            mode = displayMode,
            verse = verse,
            nasihot = nasihot,
            onDismiss = { showViralStatusDialog = false }
        )
    }
}

/**
 * Viral Islamic Status Poster Generator Dialog.
 * Generates an ornate, elegant Islamic card ready to post on WhatsApp Status, Facebook, Instagram & X.
 */
@Composable
private fun ViralIslamicStatusDialog(
    mode: Int,
    verse: QuranVerseOfTheDay,
    nasihot: DailyNasihot,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val title = if (mode == 0) "📖 ${verse.surahNameBn} (আয়াত ${verse.ayahNumber})" else "📜 ${nasihot.title}"
    val primaryText = if (mode == 0) verse.arabicText else nasihot.advice
    val secondaryText = if (mode == 0) "অর্থ: \"${verse.meaningBn}\"" else "রেফারেন্স: ${nasihot.reference}"

    val formattedShareText = """
        ✨ আজকের পবিত্র বাণী ✨
        $title
        
        $primaryText
        
        $secondaryText
        
        ══════════════════
        📲 আল-হুজুর এআই (Al-Hujur AI)
        দ্বীনি জীবনকে সহজ ও সমৃদ্ধ করুন।
        #AlHujurAI #DailyIslamicWisdom
    """.trimIndent()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(2.dp, BrightGold)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Status Poster Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📲 ভাইরাল স্ট্যাটাস কার্ড",
                        color = BrightGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
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

                // Ornate Preview Box
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = NavySurface,
                    border = BorderStroke(1.5.dp, IslamicGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            color = BrightGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = title,
                            color = TextWhite,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = primaryText,
                            color = BrightGold,
                            fontSize = if (mode == 0) 18.sp else 13.sp,
                            lineHeight = if (mode == 0) 30.sp else 20.sp,
                            textAlign = if (mode == 0) TextAlign.End else TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = secondaryText,
                            color = TextLight,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        HorizontalDivider(color = Color(0x33D4AF37), thickness = 0.8.dp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "আল-হুজুর এআই • দ্বীনি জীবন সহজ করুন",
                                color = EmeraldSuccess,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Share Buttons Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp & Social Media Button
                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, formattedShareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "WhatsApp বা স্ট্যাটাসে শেয়ার করুন"))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "WhatsApp ও ফেসবুকে শেয়ার করুন 📲",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Copy Text Button
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clipboard?.setPrimaryClip(ClipData.newPlainText("Islamic Status", formattedShareText))
                            Toast.makeText(context, "স্ট্যাটাস টেক্সট কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BrightGold)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "সম্পূর্ণ কার্ড কপি করুন 📋",
                            color = BrightGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
