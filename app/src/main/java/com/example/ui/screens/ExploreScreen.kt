package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HijriCalendarDialog
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

/** Optional tools have a home of their own, rather than overwhelming the daily dashboard. */
@Composable
fun ExploreScreen(
    viewModel: AlHujurViewModel,
    onBack: () -> Unit,
    onOpenQuiz: () -> Unit
) {
    var activeTool by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier.fillMaxSize().background(DeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("explore_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান",
                    tint = IslamicGold)
            }
            Column {
                Text("আরও ইসলামিক টুলস", color = BrightGold,
                    fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("প্রয়োজনের সময় ব্যবহার করুন", color = TextMuted, fontSize = 12.sp)
            }
        }
        ExploreHeading("পড়ুন ও শিখুন")
        ExploreItem("ইসলামিক লাইব্রেরি", "পাঠ ও রেফারেন্স",
            Icons.Default.MenuBook, "explore_library") { activeTool = "library" }
        ExploreItem("ইসলামিক কুইজ", "সংক্ষিপ্ত পাঁচটি প্রশ্ন",
            Icons.Default.Quiz, "explore_quiz", onOpenQuiz)
        ExploreHeading("দৈনিক সহায়ক")
        ExploreItem("ডিজিটাল তাসবীহ", "জিকির গণনা",
            Icons.Default.Fingerprint, "explore_tasbeeh") { activeTool = "tasbeeh" }
        ExploreItem("দৈনিক দোয়া", "প্রয়োজনীয় দোয়ার তালিকা",
            Icons.Default.Favorite, "explore_dua") { activeTool = "dua" }
        ExploreItem("কিবলা কম্পাস", "অবস্থানভিত্তিক দিকনির্দেশ (আনুমানিক)",
            Icons.Default.Explore, "explore_qibla") { activeTool = "qibla" }
        ExploreHeading("ক্যালেন্ডার ও সময়")
        ExploreItem("হিজরি ক্যালেন্ডার", "স্থানভেদে তারিখ পরিবর্তিত হতে পারে",
            Icons.Default.CalendarMonth, "explore_hijri") { activeTool = "hijri" }
        ExploreItem("রমজান ক্যালেন্ডার", "আনুমানিক তথ্য ও নির্দেশনা",
            Icons.Default.DateRange, "explore_ramadan") { activeTool = "ramadan" }
        ExploreItem("নামাজের সময়", "স্থানভিত্তিক আনুমানিক হিসাব",
            Icons.Default.Schedule, "explore_prayer") { activeTool = "prayer" }
        Text("তালিকার কিছু তথ্য স্থানীয় হিসাবভিত্তিক। ইবাদতের সময়ের জন্য স্থানীয় কর্তৃপক্ষের সময়সূচি মিলিয়ে নিন।",
            color = TextMuted, fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp, bottom = 22.dp))
    }
    when (activeTool) {
        "library" -> IslamicLibraryDialog(onDismiss = { activeTool = null })
        "tasbeeh" -> TasbeehDialog(viewModel = viewModel, onDismiss = { activeTool = null })
        "dua" -> DailyDuaDialog(onDismiss = { activeTool = null })
        "qibla" -> QiblaCompassDialog(onDismiss = { activeTool = null })
        "hijri" -> HijriCalendarDialog(viewModel = viewModel, onDismiss = { activeTool = null })
        "ramadan" -> RamadanCalendarDialog(onDismiss = { activeTool = null })
        "prayer" -> PrayerTimesDialog(viewModel = viewModel, onDismiss = { activeTool = null })
    }
}

@Composable
private fun ExploreHeading(text: String) {
    Text(text, color = IslamicGold, fontWeight = FontWeight.Bold,
        fontSize = 16.sp, modifier = Modifier.padding(top = 10.dp, bottom = 1.dp))
}

@Composable
private fun ExploreItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).testTag(tag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        border = BorderStroke(1.dp, GoldBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Icon(icon, contentDescription = null, tint = BrightGold,
                modifier = Modifier.size(25.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = TextWhite, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = TextMuted, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
                tint = IslamicGold)
        }
    }
}
