package com.example.androidsync.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidsync.data.local.db.entity.FileMetadata
import kotlinx.coroutines.flow.Flow

@Dao
interface FileMetadataDao {

    @Query("SELECT * FROM file_metadata WHERE folderId = :folderId AND isDirectory = 0 ORDER BY relativePath")
    fun getFilesForFolder(folderId: Long): Flow<List<FileMetadata>>

    @Query("SELECT COUNT(*) FROM file_metadata WHERE folderId = :folderId AND isDirectory = 0")
    fun getFileCountForFolder(folderId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(files: List<FileMetadata>)

    @Query("DELETE FROM file_metadata WHERE folderId = :folderId")
    suspend fun deleteAllForFolder(folderId: Long)
}
