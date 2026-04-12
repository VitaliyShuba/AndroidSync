package com.example.androidsync.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_folders")
data class SyncFolder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val displayName: String,
    val addedAt: Long,
    val isEnabled: Boolean = true,
)
