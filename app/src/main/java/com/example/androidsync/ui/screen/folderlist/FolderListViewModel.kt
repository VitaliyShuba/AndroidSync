package com.example.androidsync.ui.screen.folderlist

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidsync.data.local.db.entity.SyncFolder
import com.example.androidsync.data.repository.SyncFolderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderListViewModel(
    private val repository: SyncFolderRepository,
) : ViewModel() {

    val folders: StateFlow<List<SyncFolder>> = repository.getAllFolders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addFolder(uri: Uri, displayName: String) {
        viewModelScope.launch {
            if (!repository.isFolderAlreadyAdded(uri.toString())) {
                repository.addFolder(uri.toString(), displayName)
            }
        }
    }

    fun removeFolder(folder: SyncFolder) {
        viewModelScope.launch {
            repository.removeFolder(folder)
        }
    }
}
