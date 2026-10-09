package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Reply
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
import com.example.data.model.ForumPost
import com.example.data.model.ForumReply
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun CommunityForumScreen(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posts by viewModel.forumPosts.collectAsState()
    val selectedCategory by viewModel.selectedForumCategory.collectAsState()

    var showNewQuestionDialog by remember { mutableStateOf(false) }
    var replyingToPostId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val categories = remember {
        listOf("সবগুলো", "রমজান ও রোজা", "নামাজ ও ফিকহ", "দৈনিক জিকির ও আমল", "যাকাত ও সদকা")
    }

    val filteredPosts = remember(posts, selectedCategory, searchQuery) {
        posts.filter { post ->
            val matchesCategory = (selectedCategory == "সবগুলো" || selectedCategory == "All" || post.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    post.questionText.contains(searchQuery, ignoreCase = true) ||
                    post.authorName.contains(searchQuery, ignoreCase = true) ||
                    post.replies.any { it.replyText.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
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
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
        ) {
            // Header: Title & "প্রশ্ন করুন" (Post Discussion) Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "কমিউনিটি ফোরাম",
                            color = IslamicGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldContainer
                        ) {
                            Text(
                                text = "দ্বীনি আলোচনা",
                                color = EmeraldSuccess,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = "মুফতি ও স্কলার দ্বারা পর্যবেক্ষিত ইসলামিক প্রশ্নোত্তর ও আলোচনা",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                }

                Button(
                    onClick = { showNewQuestionDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("ask_question_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "প্রশ্ন করুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar for Islamic Threads
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "ফোরামে মাসয়ালা বা আলোচনা খুঁজুন...",
                        color = TextMuted,
                        fontSize = 12.5.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "অনুসন্ধান",
                        tint = BrightGold,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "পরিষ্কার করুন",
                                tint = TextLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("forum_search_input"),
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextLight,
                    focusedContainerColor = NavySurface,
                    unfocusedContainerColor = NavySurface,
                    focusedBorderColor = IslamicGold,
                    unfocusedBorderColor = Color(0x33D4AF37)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setForumCategory(cat) },
                        label = { Text(cat, fontSize = 11.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IslamicGold,
                            selectedLabelColor = MidnightBlue,
                            containerColor = NavySurface,
                            labelColor = TextLight
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0x33D4AF37),
                            selectedBorderColor = IslamicGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Feed List with Islamic Threads
            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = IslamicGold.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "কোনো আলোচনা পাওয়া যায়নি",
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "প্রথম ব্যক্তি হিসেবে এই বিষয়ে একটি প্রশ্ন বা আলোচনা পোস্ট করুন।",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = { showNewQuestionDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = IslamicGold,
                                contentColor = MidnightBlue
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("নতুন প্রশ্ন পোস্ট করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredPosts, key = { it.id }) { post ->
                        ForumPostCard(
                            post = post,
                            onLike = { viewModel.togglePostLike(post.id) },
                            onReplyClick = { replyingToPostId = post.id },
                            onShare = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "ইসলামিক আলোচনা: ${post.category}")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "【ইসলামিক ফোরাম প্রশ্নোত্তর】\nপ্রশ্ন: ${post.questionText}\nলেখক: ${post.authorName}\n\nআল-হুজুর ইসলামিক অ্যাপ"
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "প্রশ্নটি শেয়ার করুন"))
                            },
                            onCopy = { text ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Islamic Discussion", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // New Question / Discussion Thread Dialog
    if (showNewQuestionDialog) {
        NewQuestionDialog(
            onDismiss = { showNewQuestionDialog = false },
            onSubmit = { question, category ->
                viewModel.addForumPost(question, category)
                showNewQuestionDialog = false
                Toast.makeText(context, "আপনার প্রশ্নটি সফলভাবে ফোরামে পোস্ট করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Reply Dialog for Threads
    if (replyingToPostId != null) {
        ReplyDialog(
            onDismiss = { replyingToPostId = null },
            onSubmit = { replyText ->
                viewModel.addReplyToPost(replyingToPostId!!, replyText)
                replyingToPostId = null
                Toast.makeText(context, "আপনার উত্তরটি যুক্ত করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun ForumPostCard(
    post: ForumPost,
    onLike: () -> Unit,
    onReplyClick: () -> Unit,
    onShare: () -> Unit,
    onCopy: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("forum_post_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Post Author Header
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NavySurface)
                            .border(1.dp, IslamicGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1),
                            color = BrightGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Column {
                        Text(
                            text = post.authorName,
                            color = TextWhite,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = post.timeAgo,
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = LightGoldTint
                ) {
                    Text(
                        text = post.category,
                        color = IslamicGold,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Question Text
            Text(
                text = post.questionText,
                color = TextLight,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            // Post Actions (Like, Replies Count, Share, Copy & Reply Button)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like button
                    Row(
                        modifier = Modifier
                            .clickable { onLike() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                            contentDescription = "লাইক",
                            tint = if (post.isLiked) BrightGold else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.likes}",
                            color = if (post.isLiked) BrightGold else TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    // Reply count
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "উত্তর",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.replies.size}টি উত্তর",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার",
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    // Copy question
                    IconButton(
                        onClick = { onCopy(post.questionText) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "কপি",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Reply button
                Button(
                    onClick = onReplyClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavySurface,
                        contentColor = BrightGold
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0x33D4AF37)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("reply_button_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "উত্তর দিন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Replies Thread (including AI Moderator replies)
            if (post.replies.isNotEmpty()) {
                HorizontalDivider(color = Color(0x22D4AF37))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    post.replies.forEach { reply ->
                        ForumReplyItem(
                            reply = reply,
                            onCopyReply = { onCopy(reply.replyText) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForumReplyItem(
    reply: ForumReply,
    onCopyReply: () -> Unit
) {
    val isAi = reply.isAiModerator

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isAi) Color(0xFF102344) else NavySurface,
        border = BorderStroke(
            1.dp,
            if (isAi) IslamicGold else Color(0x22D4AF37)
        ),
        modifier = Modifier.fillMaxWidth()
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isAi) {
                        // SPECIAL VISUAL TAG for automated replies made by the "AI Moderator"
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IslamicGold,
                            modifier = Modifier.testTag("ai_moderator_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = MidnightBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "এআই মডারেটর • শরয়ী সিদ্ধান্ত",
                                    color = MidnightBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = reply.authorName,
                            color = BrightGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = reply.timeAgo,
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                    IconButton(
                        onClick = onCopyReply,
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "উত্তর কপি করুন",
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Text(
                text = reply.replyText,
                color = TextLight,
                fontSize = 12.5.sp,
                lineHeight = 18.sp
            )

            if (reply.verifiedReference != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "দলিল: ${reply.verifiedReference}",
                        color = IslamicGold.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun NewQuestionDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var questionText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("রমজান ও রোজা") }
    val categories = remember {
        listOf("রমজান ও রোজা", "নামাজ ও ফিকহ", "দৈনিক জিকির ও আমল", "যাকাত ও সদকা")
    }

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "কমিউনিটিতে প্রশ্ন করুন",
                            color = BrightGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "মুফতি ও সাধারণ দ্বীনি ভাইবোনদের মতামত জানুন",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                Text(
                    text = "বিভাগ নির্বাচন করুন",
                    color = TextLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = (selectedCategory == cat),
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 10.5.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGold,
                                selectedLabelColor = MidnightBlue,
                                containerColor = NavySurface,
                                labelColor = TextLight
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    placeholder = {
                        Text(
                            "আপনার ইসলামিক প্রশ্ন বা আলোচনার বিষয় বিস্তারিত লিখুন... আমাদের এআই মডারেটর কুরআন ও সুন্নাহর ভিত্তিতে যাচাইকৃত সমাধান ও দলিল প্রদান করবে।",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("question_text_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                Button(
                    onClick = {
                        if (questionText.isNotBlank()) {
                            onSubmit(questionText, selectedCategory)
                        }
                    },
                    enabled = questionText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_question_button")
                ) {
                    Text(
                        text = "প্রশ্ন পোস্ট করুন ও এআই উত্তর নিন",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ReplyDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }

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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "উত্তর লিখুন",
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন", tint = TextLight)
                    }
                }

                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = {
                        Text(
                            "আপনার মতামত, হাদিস বা কোরআনের সঠিক রেফারেন্স শেয়ার করুন...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("reply_text_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44D4AF37)
                    )
                )

                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onSubmit(replyText)
                        }
                    },
                    enabled = replyText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = MidnightBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_reply_button")
                ) {
                    Text("উত্তর জমা দিন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
