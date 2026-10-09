package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChatMessage
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun AiChatScreen(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.chatMessages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val isRecordingVoice by viewModel.isVoiceRecording.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("রোজা ও রমজান") }
    val listState = rememberLazyListState()

    // Categorized prompt suggestion collections for the Scholar AI
    val categorizedPrompts = remember {
        mapOf(
            "রোজা ও রমজান" to listOf(
                "অসুস্থতার কারণে রোজা কাজা করার সঠিক বিধান কী?",
                "সেহরির সময় শেষ হওয়ার কতক্ষণ পূর্বে খাওয়া বন্ধ করা উত্তম?",
                "ভুলবশত কিছু খেয়ে ফেললে কি রোজা ভেঙে যাবে?",
                "রোজা অবস্থায় ইনজেকশন বা আই ড্রপ ব্যবহারের শারঈ হুকুম কী?"
            ),
            "নামাজ ও খুশু" to listOf(
                "নামাজে একাগ্রতা ও খুশু-খুজু বাড়ানোর উপায় কী?",
                "তাহাজ্জুদ নামাজের সঠিক নিয়ম ও মোস্তাহাব ওয়াক্ত কোনটি?",
                "ছুটে যাওয়া কাজা নামাজ আদায়ের তরতীব কীভাবে সাজাব?",
                "বিতর নামাজে দোয়া কুনুত ভুলে গেলে করণীয় কী?"
            ),
            "যাকাত ও সদকা" to listOf(
                "জমানো টাকার যাকাত হিসাব করব কীভাবে?",
                "সোনা ও রূপার নেসাব এবং বর্তমান হিসাব পদ্ধতি কী?",
                "সদকাতুল ফিতর কখন এবং কাদের প্রদান করা ওয়াজিব?",
                "নিকটাত্মীয়দের কি যাকাতের অর্থ দেওয়া যায়?"
            ),
            "দোয়া ও জিকির" to listOf(
                "দুশ্চিন্তা ও পেরেশানি দূর করার শ্রেষ্ঠ সুন্নতি দোয়া কোনটি?",
                "শবে কদরে বেশি বেশি পঠিতব্য প্রিয় দোয়া কোনটি?",
                "ঘুম থেকে ওঠার পর ও শোয়ার সুন্নতি আমলসমূহ কী?",
                "ঋণ মুক্তির জন্য মহানবী ﷺ কোন দোয়া শিখিয়েছেন?"
            ),
            "পারিবারিক জীবন" to listOf(
                "পিতা-মাতার সাথে সদ্ব্যবহার নিয়ে কুরআনের নির্দেশ কী?",
                "সন্তানদের দ্বীনি শিক্ষাদানে পিতা-মাতার দায়িত্ব কী?",
                "প্রতিবেশীর হক রক্ষায় ইসলামের তাকীদ কী?"
            )
        )
    }

    val categories = remember {
        listOf("রোজা ও রমজান", "নামাজ ও খুশু", "যাকাত ও সদকা", "দোয়া ও জিকির", "পারিবারিক জীবন")
    }

    // Scroll to bottom when new messages arrive or AI responds
    LaunchedEffect(messages.size, isAiThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

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
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Header: Scholar AI Identity Banner with New Chat Action
            ChatHeader(
                onClearChat = {
                    viewModel.clearChat()
                    Toast.makeText(context, "নতুন কথোপকথন শুরু হয়েছে", Toast.LENGTH_SHORT).show()
                }
            )

            // Category Selection Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) IslamicGold else NavySurface,
                        border = BorderStroke(1.dp, if (isSelected) BrightGold else Color(0x33D4AF37)),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) MidnightBlue else TextLight,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Quick Question Prompt Chips for the active category
            val promptsForCategory = categorizedPrompts[selectedCategory] ?: emptyList()
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(promptsForCategory) { prompt ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NavyCard,
                        border = BorderStroke(0.8.dp, GoldBorder.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable {
                            viewModel.sendChatMessage(prompt)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = prompt,
                                color = BrightGold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                // If only welcome message exists, show the Dignified Scholar Intro Card
                if (messages.size <= 1) {
                    item {
                        ScholarIntroGuideCard()
                    }
                }

                items(messages) { message ->
                    ChatBubbleItem(
                        message = message,
                        onCopyText = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Scholar AI Advice", text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "উপদেশটি ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        onShareText = { text ->
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Scholar AI Islamic Guidance")
                                putExtra(Intent.EXTRA_TEXT, text)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "দ্বীনি উপদেশ শেয়ার করুন"))
                        }
                    )
                }

                if (isAiThinking) {
                    item {
                        AiThinkingBubble()
                    }
                }
            }

            // Bottom Input Bar with Voice Recording & Send button
            ChatInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendChatMessage(inputText)
                        inputText = ""
                    }
                },
                isRecordingVoice = isRecordingVoice,
                onToggleVoice = { viewModel.toggleVoiceRecording() }
            )
        }
    }
}

@Composable
private fun ChatHeader(onClearChat: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DeepNavy,
        border = BorderStroke(0.5.dp, GoldBorder),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(2.dp, IslamicGold, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_scholar_avatar),
                    contentDescription = "Scholar AI Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Scholar AI (স্কলার এআই)",
                        color = BrightGold,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EmeraldContainer
                    ) {
                        Text(
                            text = "✓ শরঈ পরামর্শ",
                            color = EmeraldSuccess,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "কুরআন ও সুন্নাহর আলোকে সম্মানিত মুফতি ও উপদেষ্টা",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            // Action button: Refresh / Clear conversation
            IconButton(
                onClick = onClearChat,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "নতুন কথোপকথন শুরু করুন",
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ScholarIntroGuideCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = NavyCard,
        border = BorderStroke(1.dp, GoldBorder.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                color = IslamicGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "সম্মানিত দ্বীনি ভাই/বোন, Scholar AI-তে আপনাকে আন্তরিক মোবারকবাদ।",
                color = TextWhite,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "এখানে আপনি মাহে রমজানের রোজা, পাঁচ ওয়াক্ত নামাজ, যাকাত-ফিতরা, সহীহ হাদিস ও কুরআনের আয়াতের তাফসির সম্পর্কিত যেকোনো প্রশ্ন শুদ্ধ বাংলায় শ্রদ্ধার সাথে জিজ্ঞাসা করতে পারেন।",
                color = TextLight,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            HorizontalDivider(
                color = GoldBorder.copy(alpha = 0.3f),
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("কুরআন ও সুন্নাহ", color = BrightGold, fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("সহীহ হাদিস রেফারেন্স", color = TextLight, fontSize = 10.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = BrightGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("জমহুর ফিকহ", color = TextLight, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    onCopyText: (String) -> Unit,
    onShareText: (String) -> Unit
) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, IslamicGold, CircleShape)
                    .align(Alignment.Top)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_scholar_avatar),
                    contentDescription = "Scholar AI",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = if (isUser) 18.dp else 4.dp,
                topEnd = if (isUser) 4.dp else 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = if (isUser) NavyCardElevated else NavyCard,
            border = BorderStroke(1.dp, if (isUser) GoldBorder else Color(0x33D4AF37)),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = IslamicGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Scholar AI (স্কলার এআই)",
                                color = IslamicGold,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (message.isVoiceMessage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "ভয়েস প্রশ্ন",
                            color = BrightGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Message Body Text
                Text(
                    text = message.text,
                    color = TextWhite,
                    fontSize = 13.5.sp,
                    lineHeight = 21.sp
                )

                // Timestamp & Action Buttons (Copy / Share for scholar replies)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!isUser) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { onCopyText(message.text) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "কপি করুন",
                                    tint = BrightGold.copy(alpha = 0.8f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            IconButton(
                                onClick = { onShareText(message.text) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "শেয়ার করুন",
                                    tint = BrightGold.copy(alpha = 0.8f),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Text(
                        text = message.timestamp,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AiThinkingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .border(1.5.dp, IslamicGold, CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_scholar_avatar),
                contentDescription = "Scholar AI Thinking",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = NavyCard,
            border = BorderStroke(1.dp, GoldBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = IslamicGold,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "স্কলার কুরআন ও হাদিস থেকে সমাধান প্রস্তুত করছেন...",
                    color = BrightGold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isRecordingVoice: Boolean,
    onToggleVoice: () -> Unit
) {
    // Pulse animation for recording microphone button
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecordingVoice) 1.22f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DeepNavy,
        border = BorderStroke(0.5.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isRecordingVoice) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x33EF4444),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Text(
                            text = "আপনার প্রশ্ন শুনছি... শেষ করতে মাইক্রোফোনে পুনরায় চাপুন।",
                            color = Color(0xFFFCA5A5),
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Text input field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = "হুজুরকে যে কোনো ইসলামিক প্রশ্ন জিজ্ঞাসা করুন...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    ),
                    maxLines = 3
                )

                // Send text button (if text present)
                if (inputText.isNotBlank()) {
                    IconButton(
                        onClick = onSend,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(IslamicGold)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "পাঠান",
                            tint = MidnightBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // PROMINENT Voice-recording (microphone) button
                IconButton(
                    onClick = onToggleVoice,
                    modifier = Modifier
                        .size(48.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            if (isRecordingVoice) Color(0xFFDC2626) else IslamicGold
                        )
                        .border(
                            2.dp,
                            if (isRecordingVoice) Color.White else BrightGold,
                            CircleShape
                        )
                        .testTag("voice_recording_mic_button")
                ) {
                    Icon(
                        imageVector = if (isRecordingVoice) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "ভয়েস রেকর্ড করে জিজ্ঞাসা করুন",
                        tint = if (isRecordingVoice) Color.White else MidnightBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Respectful advisory footer
            Text(
                text = "জ্ঞাতব্য: Scholar AI কুরআন ও নির্ভরযোগ্য হাদিসের ভিত্তিতে দিকনির্দেশনা দেয়। ব্যক্তিগত ফতোয়ায় উলামাদের সাথে পরামর্শ কাম্য।",
                color = TextMuted.copy(alpha = 0.7f),
                fontSize = 9.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }
    }
}
