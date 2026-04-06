package com.example.projteste.di

import com.example.projteste.AudioRecordingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { AudioRecordingRepository(androidContext()) }
}