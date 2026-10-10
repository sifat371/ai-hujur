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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LearningQuizBank
import com.example.ui.theme.*

/** Deliberately small QA feature: no network calls, tracking, or fake leaderboard. */
@Composable
fun IslamicQuizScreen(onBack: () -> Unit) {
    val questions = LearningQuizBank.questions
    var questionIndex by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    var submitted by rememberSaveable { mutableStateOf(false) }
    var score by rememberSaveable { mutableIntStateOf(0) }
    val finished = questionIndex >= questions.size
    val question = questions.getOrNull(questionIndex)
    Column(
        modifier = Modifier.fillMaxSize().background(DeepNavy)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("quiz_back")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "ফিরে যান",
                    tint = BrightGold)
            }
            Text("ইসলামিক কুইজ", color = BrightGold,
                fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Text("শেখার জন্য সংক্ষিপ্ত প্রশ্নোত্তর। প্রকাশের আগে বিষয়বস্তু যাচাই করা হবে।",
            color = TextMuted, fontSize = 13.sp)
        if (finished) {
            Card(colors = CardDefaults.cardColors(containerColor = NavyCard),
                border = BorderStroke(1.dp, GoldBorder)) {
                Column(modifier = Modifier.fillMaxWidth().padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("কুইজ সম্পন্ন!", color = BrightGold, fontSize = 22.sp,
                        fontWeight = FontWeight.Bold)
                    Text("আপনার স্কোর: ${score} / ${questions.size}",
                        color = TextWhite, fontSize = 18.sp)
                    Button(onClick = {
                        questionIndex = 0; selected = -1; submitted = false; score = 0
                    }, modifier = Modifier.testTag("quiz_restart")) {
                        Text("আবার শুরু করুন")
                    }
                }
            }
        } else if (question != null) {
            LinearProgressIndicator(
                progress = { questionIndex.toFloat() / questions.size.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = IslamicGold
            )
            Text("প্রশ্ন ${questionIndex + 1} / ${questions.size}",
                color = TextMuted, fontSize = 13.sp)
            Text(question.question, color = TextWhite, fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold)
            question.choices.forEachIndexed { index, choice ->
                val chosen = selected == index
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .clickable(enabled = !submitted) { selected = index }
                        .testTag("quiz_option_${index}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (chosen) NavySurface else NavyCard),
                    border = BorderStroke(if (chosen) 2.dp else 1.dp,
                        if (chosen) IslamicGold else GoldBorder)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RadioButton(selected = chosen, onClick = null)
                        Text(choice, color = TextLight, fontSize = 15.sp)
                    }
                }
            }
            if (submitted) {
                Text(if (selected == question.correctChoice) "সঠিক উত্তর!"
                    else "সঠিক উত্তর: ${question.choices[question.correctChoice]}",
                    color = BrightGold, fontWeight = FontWeight.SemiBold)
                Text(question.explanation, color = TextLight, fontSize = 13.sp)
            }
            Button(
                enabled = submitted || selected >= 0,
                onClick = {
                    if (!submitted) {
                        if (selected == question.correctChoice) score += 1
                        submitted = true
                    } else {
                        questionIndex += 1
                        selected = -1
                        submitted = false
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("quiz_next")
            ) {
                Text(if (submitted) "পরবর্তী প্রশ্ন" else "উত্তর জমা দিন")
            }
        }
    }
}
