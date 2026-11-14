package com.felipelucio.lugrav.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LugravTopBar(
    isSelectionMode: Boolean = false,
    onClearSelection: (() -> Unit)? = null,
    onShareClick: (() -> Unit)? = null,
    onRenameClick: (() -> Unit)? = null,
    onSelectTagsClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        navigationIcon = {
            if (isSelectionMode && onClearSelection != null) {
                IconButton(onClick = onClearSelection) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        title = {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        actions = {
            if (isSelectionMode) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = stringResource(R.string.more_options),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    onShareClick?.let {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.share_button)) },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onRenameClick?.let {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.rename_button)) },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onSelectTagsClick?.let {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.select_tags_button)) },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                    onDeleteClick?.let {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.delete_button)) },
                            onClick = {
                                showMenu = false
                                it()
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = SkyBlue,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}