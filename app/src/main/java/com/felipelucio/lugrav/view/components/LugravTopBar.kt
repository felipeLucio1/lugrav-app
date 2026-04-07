package com.felipelucio.lugrav.view.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.ui.theme.SkyBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LugravTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = SkyBlue,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}