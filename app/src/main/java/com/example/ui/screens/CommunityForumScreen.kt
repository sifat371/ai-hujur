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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ForumPost
import com.example.data.model.ForumReply
import com.example.ui.components.IslamicGeometricBackground
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

/**
 * Islamic Social Media Feed — "দ্বীনি উম্মাহ সোশ্যাল"
 * A dedicated Islamic Social Network where users share Quranic reminders, Masnoon hadiths,
 * heartfelt Dua requests with interactive "আমিন বলুন" counters, and Shariah questions
 * moderated and reviewed by the Al-Hujur AI Scholar.
 */
@Composable
fun CommunityForumScreen(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val posts by viewModel.forumPosts.collectAsState()
    val selectedCategory by viewModel.selectedForumCategory.collectAsState()
    val userName by viewModel.userName.collectAsState()

    var showCreatePostDialog by remember { mutableStateOf(false) }
    var initialPostCategory by remember { mutableStateOf("সাধারণ দ্বীনি ভাবনা") }
    var replyingToPostId by remember { mutableStateOf<String?>(null) }
    var expandedCommentsPostId by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val socialCategories = remember {
        listOf("সবগুলো", "দোয়ার দরখাস্ত", "সুন্নাহ ও হাদিস", "কুরআন ও নাসীহত", "রমজান ও রোজা", "ফিকহ ও নামাজ")
    }

    val filteredPosts = remember(posts, selectedCategory, searchQuery) {
        posts.filter { post ->
            val matchesCategory = (selectedCategory == "সবগুলো" || selectedCategory == "All" || post.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    post.questionText.contains(searchQuery, ignoreCase = true) ||
                    post.authorName.contains(searchQuery, ignoreCase = true) ||
                    (post.quoteOrAyatText != null && post.quoteOrAyatText.contains(searchQuery, ignoreCase = true)) ||
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
            // --- 1. SOCIAL HEADER: Brand & Identity ---
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
                            text = "উম্মাহ সোশ্যাল",
                            color = IslamicGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldContainer
                        ) {
                            Text(
                                text = "লোকাল ডেমো",
                                color = EmeraldSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "পোস্টগুলো ডিভাইসের বাইরে শেয়ার হয় না; এআই উত্তর যাচাইকৃত নয়",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                }

                // Profile Avatar / Indicator
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NavySurface,
                    border = BorderStroke(1.dp, GoldBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(BrightGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1).ifBlank { "উ" },
                                color = DeepNavy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = userName.ifBlank { "দ্বীনি ভাই" },
                            color = TextWhite,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 2. SEARCH SOCIAL POSTS ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "পোস্ট, হাদিস, দোয়ার দরখাস্ত বা আলোচনা খুঁজুন...",
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

            Spacer(modifier = Modifier.height(12.dp))

            // --- 3. SOCIAL FEED LIST (With Post Composer at Top) ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 28.dp)
            ) {
                // ITEM 1: SOCIAL "WHAT'S ON YOUR MIND" COMPOSER CARD
                item {
                    SocialPostComposerCard(
                        userName = userName,
                        onOpenComposer = { category ->
                            initialPostCategory = category
                            showCreatePostDialog = true
                        }
                    )
                }

                // ITEM 2: CATEGORY FILTER PILLS
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(socialCategories) { cat ->
                            val isSelected = selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setForumCategory(cat) },
                                label = {
                                    Text(
                                        text = cat,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = IslamicGold,
                                    selectedLabelColor = DeepNavy,
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
                }

                // FEED EMPTY STATE
                if (filteredPosts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
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
                                    text = "এই বিভাগে কোনো পোস্ট নেই",
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "প্রথম ব্যক্তি হিসেবে আপনার নাসীহত বা দোয়ার দরখাস্ত পোস্ট করুন।",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Button(
                                    onClick = {
                                        initialPostCategory = if (selectedCategory == "সবগুলো") "সাধারণ দ্বীনি ভাবনা" else selectedCategory
                                        showCreatePostDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = IslamicGold,
                                        contentColor = DeepNavy
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("দ্বীনি পোস্ট তৈরি করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // FEED POST CARDS
                    items(filteredPosts, key = { it.id }) { post ->
                        IslamicSocialPostCard(
                            post = post,
                            isCommentsExpanded = expandedCommentsPostId == post.id,
                            onToggleComments = {
                                expandedCommentsPostId = if (expandedCommentsPostId == post.id) null else post.id
                            },
                            onLike = { viewModel.togglePostLike(post.id) },
                            onAmin = { viewModel.togglePostAmin(post.id) },
                            onSave = {
                                viewModel.togglePostSaved(post.id)
                                val msg = if (!post.isSaved) "পোস্টটি সেভ করা হয়েছে!" else "সেভ তালিকা থেকে সরানো হয়েছে"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            onReplyClick = { replyingToPostId = post.id },
                            onAddQuickComment = { text ->
                                viewModel.addReplyToPost(post.id, text)
                                Toast.makeText(context, "আপনার মন্তব্য যুক্ত করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onShare = {
                                viewModel.incrementShareCount(post.id)
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "উম্মাহ সোশ্যাল: ${post.category}")
                                    val body = buildString {
                                        appendLine("【উম্মাহ সোশ্যাল — ইসলামিক পোস্ট】")
                                        appendLine("লেখক: ${post.authorName} (${post.authorRole})")
                                        appendLine("বিভাগ: ${post.category}")
                                        appendLine()
                                        appendLine(post.questionText)
                                        if (!post.quoteOrAyatText.isNullOrBlank()) {
                                            appendLine()
                                            appendLine("📖 ${post.quoteOrAyatText}")
                                            if (!post.quoteReference.isNullOrBlank()) {
                                                appendLine("দলিল: ${post.quoteReference}")
                                            }
                                        }
                                        appendLine()
                                        appendLine("— Al-Hujur Islamic App")
                                    }
                                    putExtra(Intent.EXTRA_TEXT, body)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "পোস্টটি শেয়ার করুন"))
                            },
                            onCopy = { text ->
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Islamic Social Post", text)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // --- DIALOG: CREATE ISLAMIC SOCIAL POST ---
    if (showCreatePostDialog) {
        CreateIslamicPostDialog(
            defaultCategory = initialPostCategory,
            authorName = userName,
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { text, category, role, quote, ref ->
                viewModel.addForumPost(
                    question = text,
                    category = category,
                    authorRole = role,
                    quoteText = quote,
                    quoteReference = ref
                )
                showCreatePostDialog = false
                Toast.makeText(context, "আপনার পোস্টটি সফলভাবে প্রকাশ করা হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- DIALOG: REPLY / COMMENT MODAL ---
    if (replyingToPostId != null) {
        ReplyDialog(
            onDismiss = { replyingToPostId = null },
            onSubmit = { replyText ->
                viewModel.addReplyToPost(replyingToPostId!!, replyText)
                replyingToPostId = null
                Toast.makeText(context, "আপনার মন্তব্য প্রকাশ করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// -------------------------------------------------------------------------------------
// 1. SOCIAL "WHAT'S ON YOUR MIND" COMPOSER CARD
// -------------------------------------------------------------------------------------
@Composable
private fun SocialPostComposerCard(
    userName: String,
    onOpenComposer: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .testTag("social_post_composer_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top: User Avatar + Clickable Input Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(38.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = NavySurface,
                        border = BorderStroke(1.2.dp, IslamicGold),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = userName.take(1).ifBlank { "মু" },
                                color = BrightGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    // Green live indicator
                    Surface(
                        shape = CircleShape,
                        color = EmeraldSuccess,
                        border = BorderStroke(1.dp, DeepNavy),
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.BottomEnd)
                    ) {}
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = NavySurface,
                    border = BorderStroke(0.8.dp, Color(0x33D4AF37)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenComposer("সাধারণ দ্বীনি ভাবনা") }
                ) {
                    Text(
                        text = "আপনার দ্বীনি অনুভূতি, নাসীহত বা প্রশ্ন শেয়ার করুন...",
                        color = TextMuted,
                        fontSize = 12.5.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider(color = Color(0x18D4AF37))

            // Bottom Quick Action Buttons: নাসীহত, দোয়ার দরখাস্ত, হাদিস/আয়াত, মাসআলা
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickComposerPill(
                    icon = Icons.Outlined.EditNote,
                    label = "নাসীহত",
                    color = BrightGold,
                    onClick = { onOpenComposer("কুরআন ও নাসীহত") }
                )
                QuickComposerPill(
                    icon = Icons.Outlined.VolunteerActivism,
                    label = "দোয়ার দরখাস্ত",
                    color = EmeraldSuccess,
                    onClick = { onOpenComposer("দোয়ার দরখাস্ত") }
                )
                QuickComposerPill(
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    label = "হাদিস শেয়ার",
                    color = Color(0xFF64B5F6),
                    onClick = { onOpenComposer("সুন্নাহ ও হাদিস") }
                )
                QuickComposerPill(
                    icon = Icons.Outlined.HelpCenter,
                    label = "মাসআলা জিজ্ঞাসা",
                    color = Color(0xFFFFB74D),
                    onClick = { onOpenComposer("ফিকহ ও নামাজ") }
                )
            }
        }
    }
}

@Composable
private fun QuickComposerPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.7.dp, color.copy(alpha = 0.35f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                color = TextLight,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// -------------------------------------------------------------------------------------
// 2. ISLAMIC SOCIAL FEED POST CARD
// -------------------------------------------------------------------------------------
@Composable
private fun IslamicSocialPostCard(
    post: ForumPost,
    isCommentsExpanded: Boolean,
    onToggleComments: () -> Unit,
    onLike: () -> Unit,
    onAmin: () -> Unit,
    onSave: () -> Unit,
    onReplyClick: () -> Unit,
    onAddQuickComment: (String) -> Unit,
    onShare: () -> Unit,
    onCopy: (String) -> Unit
) {
    var quickCommentText by remember { mutableStateOf("") }
    val isDuaPost = post.category == "দোয়ার দরখাস্ত"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(22.dp))
            .testTag("forum_post_${post.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.2.dp, GoldBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- POST HEADER: Avatar, Name, Verified Badge, Role, Category & Save ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(DeepNavy, NavySurface)
                                )
                            )
                            .border(1.2.dp, IslamicGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1).ifBlank { "মু" },
                            color = BrightGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                text = post.authorName,
                                color = TextWhite,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.isVerified) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "যাচাইকৃত",
                                    tint = BrightGold,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0x22D4AF37)
                            ) {
                                Text(
                                    text = post.authorRole,
                                    color = BrightGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = "•  ${post.timeAgo}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Category Badge & Save Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isDuaPost) EmeraldContainer else LightGoldTint
                    ) {
                        Text(
                            text = post.category,
                            color = if (isDuaPost) EmeraldSuccess else IslamicGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(
                        onClick = onSave,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "সংরক্ষণ",
                            tint = if (post.isSaved) BrightGold else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // --- MAIN POST TEXT ---
            Text(
                text = post.questionText,
                color = TextLight,
                fontSize = 14.5.sp,
                lineHeight = 22.sp
            )

            // --- OPTIONAL CALLOUT FOR SACRED HADITH OR QURANIC AYAH ---
            if (!post.quoteOrAyatText.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MidnightBlue.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Color(0x44D4AF37)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                contentDescription = null,
                                tint = BrightGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "কুরআন ও হাদিসের পবিত্র উদ্ধৃতি",
                                color = BrightGold,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = post.quoteOrAyatText,
                            color = TextWhite,
                            fontSize = 15.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (!post.quoteReference.isNullOrBlank()) {
                            Text(
                                text = post.quoteReference,
                                color = IslamicGold,
                                fontSize = 11.5.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // --- ENGAGEMENT STATS BAR (Like count, Amin count, Comments, Shares) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (post.likes > 0) {
                        Text(
                            text = "❤️ ${post.likes} মাশাআল্লাহ",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                    if (post.aminCount > 0) {
                        Text(
                            text = "🤲 ${post.aminCount} জন আমিন বলেছেন",
                            color = EmeraldSuccess,
                            fontSize = 11.5.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${post.replies.size}টি মন্তব্য",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                    if (post.sharesCount > 0) {
                        Text(
                            text = "${post.sharesCount}টি শেয়ার",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0x18D4AF37))

            // --- INTERACTIVE ACTION BAR (Like, Amin, Comment, Share, Copy) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / মাশাআল্লাহ Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (post.isLiked) Color(0x22D4AF37) else Color.Transparent,
                    modifier = Modifier.clickable { onLike() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "পছন্দ",
                            tint = if (post.isLiked) Color(0xFFFF5252) else TextLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (post.isLiked) "মাশাআল্লাহ" else "পছন্দ",
                            color = if (post.isLiked) BrightGold else TextLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Interactive "আমিন বলুন" Button for Dua Requests
                if (isDuaPost) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (post.isAminGiven) EmeraldContainer else Color(0x1500C853),
                        border = BorderStroke(1.dp, if (post.isAminGiven) EmeraldSuccess else Color(0x3300C853)),
                        modifier = Modifier.clickable { onAmin() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🤲",
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (post.isAminGiven) "আমিন বলা হয়েছে" else "আমিন বলুন",
                                color = EmeraldSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Comment Button
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCommentsExpanded) Color(0x22D4AF37) else Color.Transparent,
                    modifier = Modifier.clickable { onToggleComments() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "মন্তব্য",
                            tint = TextLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "মন্তব্য",
                            color = TextLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Share Button
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "শেয়ার",
                        tint = TextLight,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Copy Button
                IconButton(
                    onClick = { onCopy(post.questionText) },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "কপি",
                        tint = TextLight,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // --- EXPANDED COMMENTS SECTION & QUICK INLINE COMPOSER ---
            AnimatedVisibility(visible = isCommentsExpanded || post.replies.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Quick Inline Comment Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quickCommentText,
                            onValueChange = { quickCommentText = it },
                            placeholder = {
                                Text(
                                    text = if (isDuaPost) "আমিন লিখে দোয়া করুন..." else "দ্বীনি মন্তব্য বা উত্তর লিখুন...",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextLight,
                                focusedContainerColor = DeepNavy,
                                unfocusedContainerColor = DeepNavy,
                                focusedBorderColor = BrightGold,
                                unfocusedBorderColor = Color(0x33D4AF37)
                            ),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (quickCommentText.isNotBlank()) {
                                    onAddQuickComment(quickCommentText.trim())
                                    quickCommentText = ""
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .background(BrightGold, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "পাঠান",
                                tint = DeepNavy,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    // Existing Replies & AI Scholar Verified Responses
                    if (post.replies.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            post.replies.forEach { reply ->
                                IslamicReplyItem(
                                    reply = reply,
                                    onCopyReply = { onCopy(reply.replyText) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 3. REPLY / COMMENT ITEM (Special Golden Frame for Al-Hujur AI Scholar)
// -------------------------------------------------------------------------------------
@Composable
private fun IslamicReplyItem(
    reply: ForumReply,
    onCopyReply: () -> Unit
) {
    val isAi = reply.isAiModerator

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isAi) Color(0xFF102344) else DeepNavy,
        border = BorderStroke(
            1.2.dp,
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
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IslamicGold,
                            modifier = Modifier.testTag("ai_scholar_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "এআই উত্তর • যাচাই করা হয়নি",
                                    color = DeepNavy,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(NavySurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = reply.authorName.take(1),
                                color = BrightGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "কপি করুন",
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Text(
                text = reply.replyText,
                color = TextLight,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            if (!isAi && reply.verifiedReference != null) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0x18D4AF37)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
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
                            color = IslamicGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 4. DIALOG: CREATE NEW ISLAMIC SOCIAL POST
// -------------------------------------------------------------------------------------
@Composable
private fun CreateIslamicPostDialog(
    defaultCategory: String,
    authorName: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, String?, String?) -> Unit
) {
    var contentText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(defaultCategory) }
    var selectedRole by remember { mutableStateOf("দ্বীনি ভাই") }
    var quoteText by remember { mutableStateOf("") }
    var quoteReference by remember { mutableStateOf("") }
    var isQuoteSectionExpanded by remember { mutableStateOf(false) }

    val categories = remember {
        listOf("দোয়ার দরখাস্ত", "সুন্নাহ ও হাদিস", "কুরআন ও নাসীহত", "রমজান ও রোজা", "ফিকহ ও নামাজ")
    }
    val roles = remember {
        listOf("দ্বীনি ভাই", "দ্বীনি বোন", "তালেবে ইলম", "হাফেজ", "স্কলার ও শিক্ষক")
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "নতুন দ্বীনি পোস্ট প্রকাশ করুন",
                            color = BrightGold,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "উম্মাহর সাথে পবিত্র কুরআন ও সুন্নাহর কল্যাণ ভাগ করে নিন",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ", tint = TextLight)
                    }
                }

                // Category Selector
                Text(
                    text = "পোস্টের ধরন / বিভাগ:",
                    color = TextLight,
                    fontSize = 11.5.sp,
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
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IslamicGold,
                                selectedLabelColor = DeepNavy,
                                containerColor = NavySurface,
                                labelColor = TextLight
                            )
                        )
                    }
                }

                // Main Content Text Field
                OutlinedTextField(
                    value = contentText,
                    onValueChange = { contentText = it },
                    placeholder = {
                        Text(
                            when (selectedCategory) {
                                "দোয়ার দরখাস্ত" -> "আপনার বা আপনার পরিবারের জন্য দোয়ার বিবরণ লিখুন... দ্বীনি ভাই-বোনেরা আমিন বলে দোয়া করবেন।"
                                "সুন্নাহ ও হাদিস" -> "একটি সহীহ হাদিস, শিক্ষা ও এর সুন্দর ব্যাখ্যা উপস্থাপন করুন..."
                                "ফিকহ ও নামাজ" -> "আপনার শারঈ প্রশ্নটি লিখুন... এআই উত্তর যাচাই না করে অনুসরণ করবেন না।"
                                else -> "আপনার দ্বীনি অনুভূতি, নাসীহত বা আলোচনা লিখুন..."
                            },
                            color = TextMuted,
                            fontSize = 12.5.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("post_text_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x33D4AF37)
                    )
                )

                // Optional Hadith / Ayat Quote Box Toggle
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x18D4AF37),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isQuoteSectionExpanded = !isQuoteSectionExpanded }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📖 হাদিস বা কুরআনের আরবি ও রেফারেন্স যোগ করুন (ঐচ্ছিক)",
                            color = IslamicGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (isQuoteSectionExpanded) "▲" else "▼",
                            color = BrightGold,
                            fontSize = 11.sp
                        )
                    }
                }

                if (isQuoteSectionExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quoteText,
                            onValueChange = { quoteText = it },
                            placeholder = { Text("আরবি টেক্সট বা মূল বাণী...", color = TextMuted, fontSize = 11.5.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
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
                        OutlinedTextField(
                            value = quoteReference,
                            onValueChange = { quoteReference = it },
                            placeholder = { Text("রেফারেন্স (যেমন: সহীহ বুখারী ১৯৩৩)", color = TextMuted, fontSize = 11.5.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
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
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        if (contentText.isNotBlank()) {
                            onSubmit(
                                contentText.trim(),
                                selectedCategory,
                                selectedRole,
                                quoteText.trim().ifBlank { null },
                                quoteReference.trim().ifBlank { null }
                            )
                        }
                    },
                    enabled = contentText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_post_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("উম্মাহ সোশ্যালে প্রকাশ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// 5. DIALOG: REPLY MODAL
// -------------------------------------------------------------------------------------
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
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.2.dp, IslamicGold)
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
                        text = "দ্বীনি মন্তব্য বা উত্তর দিন",
                        color = BrightGold,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ", tint = TextLight)
                    }
                }

                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = {
                        Text(
                            text = "আপনার আন্তরিক মতামত, উত্তর বা দোয়া লিখুন...",
                            color = TextMuted,
                            fontSize = 12.5.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("reply_input_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextLight,
                        focusedContainerColor = NavySurface,
                        unfocusedContainerColor = NavySurface,
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x33D4AF37)
                    )
                )

                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onSubmit(replyText.trim())
                        }
                    },
                    enabled = replyText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("মন্তব্য প্রকাশ করুন", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
