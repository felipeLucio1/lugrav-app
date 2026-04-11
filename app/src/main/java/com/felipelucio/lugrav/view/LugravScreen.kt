package com.felipelucio.lugrav.view

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.AudioRecordingViewModel
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.view.components.LugravTopBar
import com.felipelucio.lugrav.view.components.PermissionRationaleDialog
import com.felipelucio.lugrav.view.components.RecordingBottomSheet
import com.felipelucio.lugrav.view.components.RecordingCard
import com.felipelucio.lugrav.view.components.RecordingFAB
import com.felipelucio.lugrav.view.components.formatTime
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
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
    
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playbackTimeFormatted by viewModel.playbackTimeFormatted.collectAsState()
    val selectedAudioPath by viewModel.selectedAudioPath.collectAsState()
    val currentPlayingPath by viewModel.currentPlayingPath.collectAsState()
    
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    
    var hasAttemptedPermissionRequest by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }
    
    var selectedRecording by remember { mutableStateOf<String?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val savedMessage = stringResource(R.string.recording_saved)
    val errorMessage = stringResource(R.string.recording_error)
    val permissionDeniedMessage = stringResource(R.string.permission_denied)
    
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
            onDismiss = { 
                showRationaleDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar(permissionDeniedMessage)
                }
            },
            onOpenSettings = {
                showRationaleDialog = false
                openAppSettings()
            }
        )
    }
    
    if (selectedRecording != null) {
        val isCurrentAudioPlaying = isPlaying && currentPlayingPath == selectedAudioPath
        RecordingBottomSheet(
            audioTitle = java.io.File(selectedRecording!!).nameWithoutExtension,
            isPlaying = isCurrentAudioPlaying,
            playbackTime = playbackTimeFormatted,
            onPlayPauseClick = { viewModel.togglePlayPause(selectedRecording!!) },
            onDismiss = { 
                selectedRecording = null
                viewModel.stopAudio()
                viewModel.clearSelectedAudio()
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
                        if (audioPermissionState.status.isGranted) {
                            viewModel.startRecording()
                            hasAttemptedPermissionRequest = false
                        } else {
                            if (hasAttemptedPermissionRequest) {
                                showRationaleDialog = true
                                hasAttemptedPermissionRequest = false
                            } else {
                                audioPermissionState.launchPermissionRequest()
                                hasAttemptedPermissionRequest = true
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
                            duration = recording.duration,
                            onClick = { selectedRecording = recording.path }
                        )
                    }
                }
            }
        }
    }
}