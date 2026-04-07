package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.ui.theme.ErrorColor
import com.felipelucio.lugrav.ui.theme.OnPrimary

@Composable
fun RecordingFAB(
    isRecording: Boolean,
    formattedTime: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(height = 56.dp, width = 140.dp),
        containerColor = if (isRecording) ErrorColor else MaterialTheme.colorScheme.primary,
        contentColor = OnPrimary
    ) {
        Icon(
            imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
            contentDescription = if (isRecording) 
                stringResource(R.string.stop_button) 
            else 
                stringResource(R.string.record_button)
        )
        
        Text(
            text = if (isRecording) formattedTime else stringResource(R.string.record_button),
            style = if (isRecording) {
                MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace)
            } else {
                MaterialTheme.typography.labelLarge
            },
            modifier = Modifier
        )
    }
}

fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}