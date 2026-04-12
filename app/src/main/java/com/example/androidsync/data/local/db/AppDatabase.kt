package com.example.androidsync.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.androidsync.data.local.db.dao.FileMetadataDao
import com.example.androidsync.data.local.db.dao.SyncFolderDao
import com.example.androidsync.data.local.db.entity.FileMetadata
import com.example.androidsync.data.local.db.entity.SyncFolder

@Database(entities = [SyncFolder::class, FileMetadata::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun syncFolderDao(): SyncFolderDao
    abstract fun fileMetadataDao(): FileMetadataDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `file_metadata` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `folderId` INTEGER NOT NULL,
                        `documentUri` TEXT NOT NULL,
                        `relativePath` TEXT NOT NULL,
                        `fileName` TEXT NOT NULL,
                        `sizeBytes` INTEGER NOT NULL,
                        `lastModified` INTEGER NOT NULL,
                        `mimeType` TEXT NOT NULL,
                        `isDirectory` INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY(`folderId`) REFERENCES `sync_folders`(`id`) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_file_metadata_folderId` ON `file_metadata` (`folderId`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_file_metadata_documentUri` ON `file_metadata` (`documentUri`)")
            }
        }
    }
}
