package com.felipelucio.lugrav.view.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.felipelucio.lugrav.R

@Composable
fun PermissionRationaleDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permissão necessária") },
        text = { Text(stringResource(R.string.permission_microphone_rationale)) },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text("Abrir configurações")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}