package com.example.projteste.di

import com.example.projteste.AudioRecordingRepository
import com.example.projteste.AudioRecordingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { AudioRecordingRepository(androidContext()) }
    viewModel { AudioRecordingViewModel(get()) }
}