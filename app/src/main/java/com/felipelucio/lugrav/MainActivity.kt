package com.felipelucio.lugrav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.felipelucio.lugrav.ui.theme.LugravTheme
import com.felipelucio.lugrav.view.LugravScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LugravTheme {
                val viewModel: AudioRecordingViewModel by viewModel()
                LugravScreen(viewModel = viewModel)
            }
        }
    }
}