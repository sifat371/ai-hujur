package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.QuranVerseEntity
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel
import kotlinx.coroutines.launch

data class QuranSurahItem(
    val number: Int,
    val nameArabic: String,
    val nameBengali: String,
    val totalAyahs: Int,
    val revelationType: String
)

val allStoredSurahs = listOf(
    QuranSurahItem(1, "الفاتحة", "সূরা আল-ফাতিহা", 7, "মাক্কী"),
    QuranSurahItem(2, "البقرة", "সূরা আল-বাক্বারাহ (আয়াতুল কুরসী)", 286, "মাদানী"),
    QuranSurahItem(18, "الكهف", "সূরা আল-কাহাফ", 110, "মাক্কী"),
    QuranSurahItem(36, "يس", "সূরা ইয়া-সীন", 83, "মাক্কী"),
    QuranSurahItem(55, "الرحمن", "সূরা আর-রহমান", 78, "মাদানী"),
    QuranSurahItem(67, "الملك", "সূরা আল-মুলক", 30, "মাক্কী"),
    QuranSurahItem(97, "القدر", "সূরা আল-ক্বদর", 5, "মাক্কী"),
    QuranSurahItem(103, "العصر", "সূরা আল-আসর", 3, "মাক্কী"),
    QuranSurahItem(108, "الكوثر", "সূরা আল-কাওছার", 3, "মাক্কী"),
    QuranSurahItem(109, "الكافرون", "সূরা আল-কাফিরূন", 6, "মাক্কী"),
    QuranSurahItem(110, "النصر", "সূরা আন-নাসর", 3, "মাদানী"),
    QuranSurahItem(112, "الإخلاص", "সূরা আল-ইখলাস", 4, "মাক্কী"),
    QuranSurahItem(113, "الفلق", "সূরা আল-ফালাক্ব", 5, "মাক্কী"),
    QuranSurahItem(114, "الناس", "সূরা আন-নাস", 6, "মাক্কী")
)

/**
 * Modern Quran Reader component powered by Room local database with:
 * - Offline verse-by-verse recitation
 * - Full-text search across meanings, transliteration, and Surahs
 * - Bookmarking functionality with persistent storage
 * - Last-read verse tracking & smooth resumption
 * - Font customization & reading mode toggles
 */
@Composable
fun QuranReaderComponent(
    viewModel: AlHujurViewModel,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val currentSurah by viewModel.selectedQuranSurahNumber.collectAsState()
    val searchQuery by viewModel.quranSearchQuery.collectAsState()
    val verses by viewModel.quranVerses.collectAsState()
    val lastReadVerse by viewModel.lastReadVerse.collectAsState()
    val bookmarkedVerses by viewModel.bookmarkedQuranVerses.collectAsState()

    val isSearching = searchQuery.isNotBlank()

    var arabicFontSize by remember { mutableStateOf(21.sp) }
    var showTransliteration by remember { mutableStateOf(true) }
    var showTranslation by remember { mutableStateOf(true) }
    var showBookmarksOnly by remember { mutableStateOf(false) }

    val displayedVerses = remember(verses, bookmarkedVerses, showBookmarksOnly) {
        if (showBookmarksOnly) {
            bookmarkedVerses
        } else {
            verses
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("quran_reader_component")
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // --- TOP HEADER BAR ---
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
                    border = BorderStroke(1.dp, IslamicGold),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = BrightGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "পবিত্র কুরআনুল কারীম",
                        color = BrightGold,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "স্থানীয় ডাটাবেজ • অফলাইন তিলাওয়াত ও বুকমার্ক",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (onClose != null) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(34.dp)
                        .testTag("button_close_quran_reader")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        tint = TextLight
                    )
                }
            }
        }

        // --- SEARCH BAR ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setQuranSearchQuery(it) },
            placeholder = {
                Text(
                    text = "সূরা বা আয়াতের অর্থ খুঁজুন (যেমন: ক্ষমা, আলো, রহমান...)",
                    color = TextMuted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "খুঁজুন",
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { viewModel.setQuranSearchQuery("") },
                        modifier = Modifier.testTag("clear_quran_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "মুছে ফেলুন",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("quran_reader_search_input"),
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

        // --- SURAH SWITCHER CHIPS (Visible when not searching and not showing bookmarks only) ---
        if (!isSearching && !showBookmarksOnly) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allStoredSurahs) { surah ->
                    val isSelected = surah.number == currentSurah
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) IslamicGold else NavySurface,
                        border = BorderStroke(1.dp, if (isSelected) BrightGold else GoldBorder),
                        modifier = Modifier
                            .clickable {
                                viewModel.selectSurah(surah.number)
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }
                            .testTag("surah_chip_${surah.number}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${surah.number}.",
                                color = if (isSelected) MidnightBlue else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = surah.nameBengali,
                                color = if (isSelected) MidnightBlue else TextLight,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        } else if (isSearching) {
            // Search Status Notice
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NavySurface,
                border = BorderStroke(0.8.dp, GoldBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সার্চ ফলাফল: ${displayedVerses.size} টি আয়াত মিলেছে",
                        color = BrightGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(
                        onClick = { viewModel.setQuranSearchQuery("") },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("সকল সূরায় ফিরুন", color = CoralAccent, fontSize = 11.sp)
                    }
                }
            }
        }

        // --- CONTROLS TOOLBAR (Font size, Pronunciation, Translation, Bookmarks filter) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Font Size Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("ফন্ট:", color = TextMuted, fontSize = 11.sp)
                IconButton(
                    onClick = {
                        if (arabicFontSize.value > 16) arabicFontSize = (arabicFontSize.value - 2).sp
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("button_font_decrease")
                ) {
                    Text("A-", color = IslamicGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "${arabicFontSize.value.toInt()}pt",
                    color = BrightGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(
                    onClick = {
                        if (arabicFontSize.value < 34) arabicFontSize = (arabicFontSize.value + 2).sp
                    },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("button_font_increase")
                ) {
                    Text("A+", color = IslamicGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Toggles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pronunciation Toggle
                FilterChip(
                    selected = showTransliteration,
                    onClick = { showTransliteration = !showTransliteration },
                    label = { Text("উচ্চারণ", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0x33D4AF37),
                        selectedLabelColor = BrightGold,
                        containerColor = NavySurface,
                        labelColor = TextMuted
                    ),
                    border = BorderStroke(0.8.dp, if (showTransliteration) IslamicGold else GoldBorder),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("filter_chip_transliteration")
                )

                // Translation Toggle
                FilterChip(
                    selected = showTranslation,
                    onClick = { showTranslation = !showTranslation },
                    label = { Text("অনুবাদ", fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0x33D4AF37),
                        selectedLabelColor = BrightGold,
                        containerColor = NavySurface,
                        labelColor = TextMuted
                    ),
                    border = BorderStroke(0.8.dp, if (showTranslation) IslamicGold else GoldBorder),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("filter_chip_translation")
                )

                // Bookmark Only Filter
                FilterChip(
                    selected = showBookmarksOnly,
                    onClick = { showBookmarksOnly = !showBookmarksOnly },
                    label = { Text("বুকমার্ক (${bookmarkedVerses.size})", fontSize = 10.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (showBookmarksOnly) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = if (showBookmarksOnly) BrightGold else TextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0x33D4AF37),
                        selectedLabelColor = BrightGold,
                        containerColor = NavySurface,
                        labelColor = TextMuted
                    ),
                    border = BorderStroke(0.8.dp, if (showBookmarksOnly) IslamicGold else GoldBorder),
                    modifier = Modifier
                        .height(28.dp)
                        .testTag("filter_chip_bookmarks")
                )
            }
        }

        // --- LAST READ QUICK JUMP BANNER ---
        if (!isSearching && !showBookmarksOnly && lastReadVerse != null) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x2210B981),
                border = BorderStroke(0.8.dp, EmeraldSuccess),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("last_read_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "সর্বশেষ পঠিত: ${lastReadVerse?.surahNameBengali} (আয়াত ${lastReadVerse?.ayahNumber})",
                            color = EmeraldSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    TextButton(
                        onClick = {
                            val targetVerse = lastReadVerse
                            if (targetVerse != null) {
                                if (currentSurah != targetVerse.surahNumber) {
                                    viewModel.selectSurah(targetVerse.surahNumber)
                                }
                                coroutineScope.launch {
                                    val targetIdx = displayedVerses.indexOfFirst { it.id == targetVerse.id }
                                    if (targetIdx >= 0) {
                                        listState.animateScrollToItem(targetIdx)
                                    }
                                }
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("সেখানে যান", color = BrightGold, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SURAH HEADER BANNER (When reading a specific Surah) ---
        if (!isSearching && !showBookmarksOnly && displayedVerses.isNotEmpty()) {
            val first = displayedVerses.first()
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MidnightBlue,
                border = BorderStroke(1.dp, GoldBorder)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = first.surahNameArabic,
                        color = BrightGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${first.surahNameBengali} • ${first.totalAyahs} আয়াত • ${first.revelationType}",
                        color = TextLight,
                        fontSize = 12.sp
                    )
                    if (first.surahNumber != 9 && first.surahNumber != 1) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                            color = BrightGold,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // --- VERSE LIST OR EMPTY STATE ---
        if (displayedVerses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (showBookmarksOnly) Icons.Outlined.BookmarkBorder else Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(46.dp)
                    )
                    Text(
                        text = if (showBookmarksOnly) {
                            "কোনো সংরক্ষিত বুকমার্ক নেই\nপড়ার সময় বুকমার্ক আইকনে চাপুন"
                        } else {
                            "অনুসন্ধানের সাথে কোনো আয়াত মেলেনি"
                        },
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    if (isSearching) {
                        TextButton(onClick = { viewModel.setQuranSearchQuery("") }) {
                            Text("সার্চ রিসেট করুন", color = BrightGold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("quran_verses_lazy_column"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedVerses, key = { it.id }) { verse ->
                    VerseItemCardView(
                        verse = verse,
                        arabicFontSize = arabicFontSize,
                        showTransliteration = showTransliteration,
                        showTranslation = showTranslation,
                        isLastRead = (lastReadVerse?.id == verse.id),
                        onToggleBookmark = {
                            viewModel.toggleQuranBookmark(verse)
                        },
                        onMarkAsLastRead = {
                            viewModel.markVerseAsLastRead(verse)
                            Toast.makeText(
                                context,
                                "পড়া সম্পন্ন: ${verse.surahNameBengali} আয়াত ${verse.ayahNumber}",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onCopyVerse = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val textToCopy = buildString {
                                appendLine(verse.arabicText)
                                if (verse.pronunciationBengali.isNotBlank()) {
                                    appendLine("উচ্চারণ: ${verse.pronunciationBengali}")
                                }
                                appendLine("অর্থ: ${verse.translationBengali}")
                                appendLine("[${verse.surahNameBengali}: আয়াত ${verse.ayahNumber}]")
                            }
                            val clip = ClipData.newPlainText("Quran Ayah", textToCopy)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "আয়াত ও অর্থ কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        onShareVerse = {
                            val shareText = buildString {
                                appendLine("পবিত্র কুরআনুল কারীম")
                                appendLine(verse.arabicText)
                                if (verse.pronunciationBengali.isNotBlank()) {
                                    appendLine("উচ্চারণ: ${verse.pronunciationBengali}")
                                }
                                appendLine("অর্থ: ${verse.translationBengali}")
                                appendLine("[${verse.surahNameBengali}, আয়াত ${verse.ayahNumber}]")
                                appendLine("- Islamic Mind অ্যাপ থেকে শেয়ারকৃত")
                            }
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন"))
                        }
                    )
                }
            }
        }
    }
}

/**
 * Clean, ornate card displaying an individual Ayah with interactive actions:
 * Bookmark, Mark as Last Read, Copy, and Native Share.
 */
@Composable
private fun VerseItemCardView(
    verse: QuranVerseEntity,
    arabicFontSize: androidx.compose.ui.unit.TextUnit,
    showTransliteration: Boolean,
    showTranslation: Boolean,
    isLastRead: Boolean,
    onToggleBookmark: () -> Unit,
    onMarkAsLastRead: () -> Unit,
    onCopyVerse: () -> Unit,
    onShareVerse: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${verse.surahNumber}_${verse.ayahNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLastRead) Color(0xFF132338) else NavyCard
        ),
        border = BorderStroke(
            width = if (isLastRead) 1.2.dp else 1.dp,
            color = if (isLastRead) EmeraldSuccess else GoldBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Verse Header Row (Ayah number, Surah reference, action buttons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Ayah number badge
                    Surface(
                        shape = CircleShape,
                        color = Color(0x22D4AF37),
                        border = BorderStroke(1.dp, IslamicGold),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${verse.ayahNumber}",
                                color = BrightGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = "${verse.surahNameBengali}",
                        color = TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (isLastRead) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "পঠিত",
                                color = EmeraldSuccess,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Mark as Last Read
                    IconButton(
                        onClick = onMarkAsLastRead,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("mark_read_button_${verse.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "সর্বশেষ পঠিত চিহ্নিত করুন",
                            tint = if (isLastRead) EmeraldSuccess else TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Bookmark
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("bookmark_button_${verse.id}")
                    ) {
                        Icon(
                            imageVector = if (verse.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "বুকমার্ক করুন",
                            tint = if (verse.isBookmarked) BrightGold else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Copy
                    IconButton(
                        onClick = onCopyVerse,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("copy_button_${verse.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "কপি করুন",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Share
                    IconButton(
                        onClick = onShareVerse,
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("share_button_${verse.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার করুন",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Arabic Calligraphy
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MidnightBlue.copy(alpha = 0.6f),
                border = BorderStroke(0.8.dp, Color(0x22D4AF37))
            ) {
                Text(
                    text = verse.arabicText,
                    color = TextWhite,
                    fontSize = arabicFontSize,
                    lineHeight = (arabicFontSize.value * 1.65f).sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // Bengali Pronunciation (Transliteration)
            if (showTransliteration && verse.pronunciationBengali.isNotBlank()) {
                Text(
                    text = "উচ্চারণ: ${verse.pronunciationBengali}",
                    color = IslamicGold.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            // Bengali Meaning (Translation)
            if (showTranslation && verse.translationBengali.isNotBlank()) {
                Text(
                    text = "অর্থ: \"${verse.translationBengali}\"",
                    color = TextLight,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
