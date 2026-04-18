package com.felipelucio.lugrav.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LugravTopBar(
    onShareClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        actions = {
            onShareClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.share_button),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            onDeleteClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.delete_button),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = SkyBlue,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}