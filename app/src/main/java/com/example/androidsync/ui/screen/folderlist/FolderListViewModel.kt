package com.example.androidsync.ui.screen.folderlist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidsync.data.local.db.entity.SyncFolder
import com.example.androidsync.data.repository.FileRepository
import com.example.androidsync.data.repository.SyncFolderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FolderWithCount(
    val folder: SyncFolder,
    val fileCount: Int,
)

class FolderListViewModel(
    private val folderRepository: SyncFolderRepository,
    private val fileRepository: FileRepository,
) : ViewModel() {

    val foldersWithCounts: StateFlow<List<FolderWithCount>> = folderRepository.getAllFolders()
        .flatMapLatest { folders ->
            if (folders.isEmpty()) {
                flowOf(emptyList())
            } else {
                val countFlows = folders.map { folder ->
                    fileRepository.getFileCountForFolder(folder.id).map { count ->
                        FolderWithCount(folder, count)
                    }
                }
                combine(countFlows) { it.toList() }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFolder(uri: Uri, displayName: String) {
        viewModelScope.launch {
            if (!folderRepository.isFolderAlreadyAdded(uri.toString())) {
                folderRepository.addFolder(uri.toString(), displayName)
            }
        }
    }

    fun removeFolder(folder: SyncFolder) {
        viewModelScope.launch {
            folderRepository.removeFolder(folder)
        }
    }
}
