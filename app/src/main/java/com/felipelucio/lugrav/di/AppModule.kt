package com.felipelucio.lugrav.di

import com.felipelucio.lugrav.AudioRecordingRepository
import com.felipelucio.lugrav.AudioRecordingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AudioRecordingRepository(androidContext()) }
    viewModel { AudioRecordingViewModel(get()) }
}