package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.RecordingFileRules

@Composable
fun RenameRecordingDialog(
    initialName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var textFieldValue by remember(initialName) {
        mutableStateOf(
            TextFieldValue(
                text = initialName,
                selection = TextRange(0, initialName.length)
            )
        )
    }
    val focusRequester = remember { FocusRequester() }
    val normalizedName = RecordingFileRules.normalizeName(textFieldValue.text)
    val isSaveEnabled = RecordingFileRules.isValidName(
        raw = textFieldValue.text,
        currentName = initialName
    )
    val showInvalidName = textFieldValue.text.isNotBlank() &&
        normalizedName != initialName &&
        !isSaveEnabled

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.rename_recording_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                label = {
                    Text(text = stringResource(R.string.rename_recording_label))
                },
                singleLine = true,
                isError = showInvalidName,
                supportingText = if (showInvalidName) {
                    {
                        Text(text = stringResource(R.string.rename_invalid_name))
                    }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (isSaveEnabled) {
                            onConfirm(normalizedName)
                        }
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(normalizedName) },
                enabled = isSaveEnabled
            ) {
                Text(text = stringResource(R.string.rename_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.rename_cancel))
            }
        }
    )
}
