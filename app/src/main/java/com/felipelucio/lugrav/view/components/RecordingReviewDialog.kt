package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.RecordingFileRules
import com.felipelucio.lugrav.UserTag

@Composable
fun RecordingReviewDialog(
    suggestedName: String,
    tags: List<UserTag>,
    isLoadingTags: Boolean,
    isSaving: Boolean,
    onSave: (String, Set<String>) -> Unit,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    var textFieldValue by remember(suggestedName) {
        mutableStateOf(
            TextFieldValue(
                text = suggestedName,
                selection = TextRange(0, suggestedName.length)
            )
        )
    }
    var selectedTagNames by remember { mutableStateOf(emptySet<String>()) }
    val focusRequester = remember { FocusRequester() }
    val normalizedName = RecordingFileRules.normalizeName(textFieldValue.text)
    val isNameValid = RecordingFileRules.isValidRecordingName(textFieldValue.text)
    val showInvalidName = textFieldValue.text.isNotBlank() && !isNameValid
    val canSave = isNameValid && !isSaving

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = {
            if (!isSaving) {
                onDismiss()
            }
        },
        title = {
            Text(
                text = stringResource(R.string.recording_review_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                            if (canSave) {
                                onSave(normalizedName, selectedTagNames)
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Text(
                    text = stringResource(R.string.recording_review_tags_label),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )

                when {
                    isLoadingTags -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    tags.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.select_tags_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                        ) {
                            items(tags, key = { it.name }) { tag ->
                                val isChecked = tag.name in selectedTagNames
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .toggleable(
                                            value = isChecked,
                                            enabled = !isSaving,
                                            role = Role.Checkbox,
                                            onValueChange = { checked ->
                                                selectedTagNames = if (checked) {
                                                    selectedTagNames + tag.name
                                                } else {
                                                    selectedTagNames - tag.name
                                                }
                                            }
                                        )
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = null,
                                        enabled = !isSaving
                                    )
                                    Text(
                                        text = tag.name,
                                        modifier = Modifier.padding(start = 8.dp),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(normalizedName, selectedTagNames) },
                enabled = canSave
            ) {
                Text(text = stringResource(R.string.recording_review_save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDiscard,
                enabled = !isSaving
            ) {
                Text(text = stringResource(R.string.recording_review_discard))
            }
        }
    )
}
