package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.IslamicGold
import com.example.viewmodel.AlHujurViewModel

/**
 * Full Quran Reader modal dialog wrapping the [QuranReaderComponent].
 * Provides immersive offline verse reading, real-time search, bookmarking, and font scaling.
 */
@Composable
fun QuranReaderDialog(
    viewModel: AlHujurViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .testTag("dialog_quran_reader"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            border = BorderStroke(1.5.dp, IslamicGold)
        ) {
            QuranReaderComponent(
                viewModel = viewModel,
                onClose = onDismiss
            )
        }
    }
}
