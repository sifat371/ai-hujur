package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.*
import com.example.viewmodel.AlHujurViewModel

@Composable
fun AuthDialog(viewModel: AlHujurViewModel, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.dp, GoldBorder)
        ) {
            Column(modifier = Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("অনলাইন লগইন এখনও উপলব্ধ নয়", color = BrightGold,
                    fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("আগের সংস্করণে লগইন ও Google যাচাইকরণ দেখানো হলেও " +
                    "প্রকৃত সার্ভার যাচাইকরণ বা ক্লাউড ব্যাকআপ ছিল না। " +
                    "আপনার আমল ও কুরআন বুকমার্ক শুধু এই ডিভাইসে সংরক্ষিত হয়। " +
                    "পাসওয়ার্ড প্রদান করবেন না।", color = TextLight, fontSize = 13.sp)
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = IslamicGold)) {
                    Text("লোকাল মোডে চালিয়ে যান", color = MidnightBlue)
                }
            }
        }
    }
}
