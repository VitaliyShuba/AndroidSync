package com.example.androidsync.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidsync.data.local.db.dao.SyncFolderDao
import com.example.androidsync.data.local.db.entity.SyncFolder

@Database(entities = [SyncFolder::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun syncFolderDao(): SyncFolderDao
}
