package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.TagRules
import com.felipelucio.lugrav.UserTag
import com.felipelucio.lugrav.ui.theme.DEFAULT_TAG_COLOR_HEX
import com.felipelucio.lugrav.ui.theme.TagColorOptions

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewTagDialog(
    existingTags: List<UserTag>,
    onConfirm: (name: String, colorHex: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var selectedColorHex by rememberSaveable { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }

    val normalizedName = TagRules.normalizeName(name)
    val existingNames = remember(existingTags) { existingTags.map { it.name } }
    val isDuplicate = normalizedName.isNotEmpty() &&
        TagRules.isDuplicateName(normalizedName, existingNames)
    val isTooLong = normalizedName.length > TagRules.MAX_NAME_LENGTH
    val hasControlCharacters = normalizedName.any {
        it == '\n' || it == '\r' || it.isISOControl()
    }
    val isValidName = TagRules.isValidName(name) && !isDuplicate
    val showError = name.isNotEmpty() && !isValidName
    val errorMessage = when {
        isDuplicate -> stringResource(R.string.tag_name_duplicate)
        isTooLong -> stringResource(R.string.tag_name_too_long)
        hasControlCharacters || normalizedName.isEmpty() -> {
            stringResource(R.string.tag_name_invalid)
        }
        else -> null
    }
    val selectedColor = selectedColorHex ?: DEFAULT_TAG_COLOR_HEX

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun confirm() {
        if (isValidName) {
            onConfirm(normalizedName, selectedColor)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.new_tag_dialog_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = {
                        Text(text = stringResource(R.string.tag_name_label))
                    },
                    singleLine = true,
                    isError = showError,
                    supportingText = if (showError && errorMessage != null) {
                        {
                            Text(text = errorMessage)
                        }
                    } else {
                        null
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { confirm() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )

                Text(
                    text = stringResource(R.string.tag_color_label),
                    style = MaterialTheme.typography.titleSmall
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 5
                ) {
                    TagColorOptions.forEach { option ->
                        val isSelected = selectedColorHex == option.hex
                        val colorName = stringResource(option.nameRes)
                        val checkColor = if (option.color.luminance() > 0.5f) {
                            Color.Black
                        } else {
                            Color.White
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(option.color)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onSurface
                                    } else {
                                        MaterialTheme.colorScheme.outline
                                    },
                                    shape = CircleShape
                                )
                                .selectable(
                                    selected = isSelected,
                                    role = Role.RadioButton,
                                    onClick = {
                                        selectedColorHex = if (isSelected) null else option.hex
                                    }
                                )
                                .semantics {
                                    contentDescription = colorName
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = checkColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.tag_color_default_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { confirm() },
                enabled = isValidName
            ) {
                Text(text = stringResource(R.string.tag_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.tag_cancel))
            }
        }
    )
}
