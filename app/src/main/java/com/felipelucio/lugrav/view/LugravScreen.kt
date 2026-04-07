package com.felipelucio.lugrav.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.felipelucio.lugrav.AudioRecordingViewModel
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.view.components.LugravTopBar
import com.felipelucio.lugrav.view.components.PermissionRationaleDialog
import com.felipelucio.lugrav.view.components.RecordingCard
import com.felipelucio.lugrav.view.components.RecordingFAB
import com.felipelucio.lugrav.view.components.formatTime

@Composable
fun LugravScreen(
    viewModel: AudioRecordingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingTimeSeconds by viewModel.recordingTimeSeconds.collectAsState()
    val recordingsList by viewModel.recordingsList.collectAsState()
    val recordingSuccess by viewModel.recordingSuccess.collectAsState()
    

    fun hasRecordAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun shouldShowRequestPermissionRationale(): Boolean {
        return (context as Activity).shouldShowRequestPermissionRationale(
            Manifest.permission.RECORD_AUDIO
        )
    }
    
    var hasAudioPermission by remember { 
        mutableStateOf(hasRecordAudioPermission()) 
    }
    
    var shouldShowRationale by remember { 
        mutableStateOf(shouldShowRequestPermissionRationale()) 
    }
    
    var showRationaleDialog by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(R.string.recording_saved)
    val errorMessage = stringResource(R.string.recording_error)
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (isGranted) {
            viewModel.startRecording()
        }
    }
    
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadRecordings()
    }
    
    LaunchedEffect(recordingSuccess) {
        recordingSuccess?.let { result ->
            result.onSuccess {
                snackbarHostState.showSnackbar(savedMessage)
            }.onFailure {
                snackbarHostState.showSnackbar(errorMessage)
            }
        }
    }
    
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            onDismiss = { showRationaleDialog = false },
            onOpenSettings = {
                showRationaleDialog = false
                openAppSettings()
            }
        )
    }
    
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { LugravTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            RecordingFAB(
                isRecording = isRecording,
                formattedTime = formatTime(recordingTimeSeconds),
                onClick = {
                    if (isRecording) {
                        viewModel.finalizeRecording()
                    } else {
                        if (hasAudioPermission) {
                            viewModel.startRecording()
                        } else {
                            hasAudioPermission = hasRecordAudioPermission()
                            shouldShowRationale = shouldShowRequestPermissionRationale()
                            
                            if (shouldShowRationale) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                showRationaleDialog = true
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (recordingsList.isEmpty()) {
                Text(
                    text = stringResource(R.string.recordings_not_found),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(recordingsList) { index, recording ->
                        RecordingCard(
                            filePath = recording.path,
                            duration = recording.duration
                        )
                    }
                }
            }
        }
    }
}