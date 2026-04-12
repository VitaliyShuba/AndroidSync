package com.example.androidsync.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "file_metadata",
    foreignKeys = [ForeignKey(
        entity = SyncFolder::class,
        parentColumns = ["id"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("folderId"), Index("documentUri", unique = true)],
)
data class FileMetadata(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val folderId: Long,
    val documentUri: String,
    val relativePath: String,
    val fileName: String,
    val sizeBytes: Long,
    val lastModified: Long,
    val mimeType: String,
    val isDirectory: Boolean = false,
)
