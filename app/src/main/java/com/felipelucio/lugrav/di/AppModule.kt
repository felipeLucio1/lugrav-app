package com.felipelucio.lugrav.di

import com.felipelucio.lugrav.AudioRecordingRepository
import com.felipelucio.lugrav.AudioRecordingViewModel
import com.felipelucio.lugrav.interfaces.AudioPlayer
import com.felipelucio.lugrav.interfaces.AudioRecorder
import com.felipelucio.lugrav.interfaces.AudioRecorderImpl
import com.felipelucio.lugrav.interfaces.AudioPlayerImpl
import com.felipelucio.lugrav.interfaces.FileProvider
import com.felipelucio.lugrav.interfaces.FileProviderImpl
import com.felipelucio.lugrav.interfaces.MetadataRetriever
import com.felipelucio.lugrav.interfaces.MetadataRetrieverImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FileProviderImpl(androidContext()) as FileProvider }
    single { AudioRecorderImpl(androidContext()) as AudioRecorder }
    single { AudioPlayerImpl() as AudioPlayer }
    single { MetadataRetrieverImpl() as MetadataRetriever }
    single { AudioRecordingRepository(get(), get(), get(), get()) }
    viewModel { AudioRecordingViewModel(get()) }
}
