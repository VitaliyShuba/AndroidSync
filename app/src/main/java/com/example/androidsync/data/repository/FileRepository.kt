package com.example.androidsync.data.repository

import android.net.Uri
import com.example.androidsync.data.local.db.dao.FileMetadataDao
import com.example.androidsync.data.local.db.entity.FileMetadata
import com.example.androidsync.data.local.scanner.LocalFileScanner
import kotlinx.coroutines.flow.Flow

class FileRepository(
    private val fileMetadataDao: FileMetadataDao,
    private val scanner: LocalFileScanner,
) {

    fun getFilesForFolder(folderId: Long): Flow<List<FileMetadata>> =
        fileMetadataDao.getFilesForFolder(folderId)

    fun getFileCountForFolder(folderId: Long): Flow<Int> =
        fileMetadataDao.getFileCountForFolder(folderId)

    suspend fun scanFolder(folderId: Long, treeUri: String) {
        val uri = Uri.parse(treeUri)
        val files = scanner.scanFolder(folderId, uri)
        fileMetadataDao.deleteAllForFolder(folderId)
        fileMetadataDao.upsertAll(files)
    }
}
