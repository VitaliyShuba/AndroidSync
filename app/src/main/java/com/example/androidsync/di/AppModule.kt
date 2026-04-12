package com.example.androidsync.di

import androidx.room.Room
import com.example.androidsync.data.local.db.AppDatabase
import com.example.androidsync.data.local.scanner.LocalFileScanner
import com.example.androidsync.data.remote.auth.GoogleAuthManager
import com.example.androidsync.data.remote.drive.DriveServiceProvider
import com.example.androidsync.data.repository.FileRepository
import com.example.androidsync.data.repository.SyncFolderRepository
import com.example.androidsync.ui.screen.folderdetail.FolderDetailViewModel
import com.example.androidsync.ui.screen.folderlist.FolderListViewModel
import com.example.androidsync.ui.screen.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "androidsync.db")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }
    single { get<AppDatabase>().syncFolderDao() }
    single { get<AppDatabase>().fileMetadataDao() }

    // Scanners & Repositories
    single { LocalFileScanner(androidContext()) }
    singleOf(::SyncFolderRepository)
    singleOf(::FileRepository)

    // Google Auth & Drive
    single { GoogleAuthManager(androidContext()) }
    single { DriveServiceProvider(androidContext()) }

    // ViewModels
    viewModelOf(::FolderListViewModel)
    viewModelOf(::FolderDetailViewModel)
    viewModelOf(::SettingsViewModel)
}
