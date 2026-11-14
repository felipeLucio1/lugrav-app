package com.felipelucio.lugrav.di

import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.felipelucio.lugrav.AudioRecordingRepository
import com.felipelucio.lugrav.AudioRecordingViewModel
import com.felipelucio.lugrav.data.room.LugravDatabase
import com.felipelucio.lugrav.data.room.RecordingDao
import com.felipelucio.lugrav.data.room.RecordingTagDao
import com.felipelucio.lugrav.data.room.RecordingTagStore
import com.felipelucio.lugrav.data.room.RoomRecordingTagStore
import com.felipelucio.lugrav.data.room.TagDao
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
    single {
        Room.databaseBuilder<LugravDatabase>(androidContext(), "lugrav.db")
            .setDriver(AndroidSQLiteDriver())
            .build()
    }
    single<RecordingDao> { get<LugravDatabase>().recordingDao() }
    single<TagDao> { get<LugravDatabase>().tagDao() }
    single<RecordingTagDao> { get<LugravDatabase>().recordingTagDao() }
    single<RecordingTagStore> {
        RoomRecordingTagStore(
            database = get(),
            recordingDao = get(),
            tagDao = get(),
            recordingTagDao = get()
        )
    }
    single { AudioRecordingRepository(get(), get(), get(), get(), get()) }
    viewModel { AudioRecordingViewModel(get()) }
}
