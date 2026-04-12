package com.example.androidsync.data.local.scanner

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.example.androidsync.data.local.db.entity.FileMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalFileScanner(private val context: Context) {

    suspend fun scanFolder(folderId: Long, treeUri: Uri): List<FileMetadata> =
        withContext(Dispatchers.IO) {
            val results = mutableListOf<FileMetadata>()
            val treeDocumentId = DocumentsContract.getTreeDocumentId(treeUri)
            scanDirectory(folderId, treeUri, treeDocumentId, "", results)
            results
        }

    private fun scanDirectory(
        folderId: Long,
        treeUri: Uri,
        parentDocumentId: String,
        pathPrefix: String,
        results: MutableList<FileMetadata>,
    ) {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocumentId)

        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )

        context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
            val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            val mimeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
            val sizeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
            val modifiedIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

            while (cursor.moveToNext()) {
                val documentId = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex) ?: continue
                val mimeType = cursor.getString(mimeIndex) ?: ""
                val size = cursor.getLong(sizeIndex)
                val lastModified = cursor.getLong(modifiedIndex)
                val isDirectory = mimeType == DocumentsContract.Document.MIME_TYPE_DIR

                val relativePath = if (pathPrefix.isEmpty()) name else "$pathPrefix/$name"
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, documentId)

                results.add(
                    FileMetadata(
                        folderId = folderId,
                        documentUri = documentUri.toString(),
                        relativePath = relativePath,
                        fileName = name,
                        sizeBytes = size,
                        lastModified = lastModified,
                        mimeType = mimeType,
                        isDirectory = isDirectory,
                    )
                )

                if (isDirectory) {
                    scanDirectory(folderId, treeUri, documentId, relativePath, results)
                }
            }
        }
    }
}
