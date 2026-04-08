package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.ui.theme.SkyBlue
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordingCard(
    filePath: String,
    duration: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fileName = File(filePath).nameWithoutExtension
    val dateStr = extractDateFromFileName(filePath)
    
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = { },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.play_button),
                    tint = SkyBlue
                )
            }
            
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

private fun extractDateFromFileName(filePath: String): String {
    return try {
        val fileName = File(filePath).nameWithoutExtension
        val parts = fileName.split("_")
        if (parts.size >= 2) {
            val datePart = parts[1]
            val parsedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(datePart)
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(parsedDate ?: Date())
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }
}