package com.example.androidsync.data.repository

import com.example.androidsync.data.local.db.dao.SyncFolderDao
import com.example.androidsync.data.local.db.entity.SyncFolder
import kotlinx.coroutines.flow.Flow

class SyncFolderRepository(
    private val syncFolderDao: SyncFolderDao,
) {

    fun getAllFolders(): Flow<List<SyncFolder>> = syncFolderDao.getAllFolders()

    suspend fun addFolder(uri: String, displayName: String): Long {
        return syncFolderDao.insert(
            SyncFolder(
                uri = uri,
                displayName = displayName,
                addedAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun removeFolder(folder: SyncFolder) {
        syncFolderDao.delete(folder)
    }

    suspend fun isFolderAlreadyAdded(uri: String): Boolean {
        return syncFolderDao.getByUri(uri) != null
    }

    fun getById(id: Long): Flow<SyncFolder?> = syncFolderDao.getById(id)
}
