package com.example.androidsync.di

import androidx.room.Room
import com.example.androidsync.data.local.db.AppDatabase
import com.example.androidsync.data.repository.SyncFolderRepository
import com.example.androidsync.ui.screen.folderlist.FolderListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "androidsync.db")
            .build()
    }
    single { get<AppDatabase>().syncFolderDao() }

    singleOf(::SyncFolderRepository)

    viewModelOf(::FolderListViewModel)
}
