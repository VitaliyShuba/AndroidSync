package com.example.androidsync.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.androidsync.data.local.db.entity.SyncFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncFolderDao {

    @Query("SELECT * FROM sync_folders ORDER BY addedAt DESC")
    fun getAllFolders(): Flow<List<SyncFolder>>

    @Insert
    suspend fun insert(folder: SyncFolder): Long

    @Delete
    suspend fun delete(folder: SyncFolder)

    @Query("SELECT * FROM sync_folders WHERE uri = :uri LIMIT 1")
    suspend fun getByUri(uri: String): SyncFolder?

    @Query("SELECT * FROM sync_folders WHERE id = :id")
    fun getById(id: Long): Flow<SyncFolder?>
}
