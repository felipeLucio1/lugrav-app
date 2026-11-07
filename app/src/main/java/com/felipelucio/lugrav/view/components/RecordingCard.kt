package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.ui.theme.SkyBlue
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecordingCard(
    filePath: String,
    duration: String,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val fileName = File(filePath).nameWithoutExtension
    val dateStr = extractDateFromFile(filePath)
    
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, SkyBlue, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$dateStr · $duration",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private val RECORDING_FILE_NAME_PATTERN = Regex("""^lugrav_(\d{8})_\d{6}$""")

private fun extractDateFromFile(filePath: String): String {
    val file = File(filePath)
    val parsedDate = parseDateFromFileName(file.nameWithoutExtension)
        ?: Date(file.lastModified().takeIf { it > 0 } ?: System.currentTimeMillis())

    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsedDate)
}

private fun parseDateFromFileName(fileName: String): Date? {
    val match = RECORDING_FILE_NAME_PATTERN.matchEntire(fileName) ?: return null
    return runCatching {
        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(match.groupValues[1])
    }.getOrNull()
}
