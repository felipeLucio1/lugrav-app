package com.felipelucio.lugrav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.felipelucio.lugrav.ui.theme.LugravTheme
import com.felipelucio.lugrav.view.LugravScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: AudioRecordingViewModel by viewModel()

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoadingRecordings.value
        }

        viewModel.loadRecordings()

        setContent {
            LugravTheme {
                LugravScreen(viewModel = viewModel)
            }
        }
    }
}