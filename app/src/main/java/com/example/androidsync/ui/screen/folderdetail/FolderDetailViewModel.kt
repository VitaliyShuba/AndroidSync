package com.example.androidsync.ui.screen.folderdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidsync.data.local.db.entity.FileMetadata
import com.example.androidsync.data.local.db.entity.SyncFolder
import com.example.androidsync.data.repository.FileRepository
import com.example.androidsync.data.repository.SyncFolderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderDetailViewModel(
    private val fileRepository: FileRepository,
    private val folderRepository: SyncFolderRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val folderId: Long = savedStateHandle["folderId"]!!

    val folder: StateFlow<SyncFolder?> = folderRepository.getById(folderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val files: StateFlow<List<FileMetadata>> = fileRepository.getFilesForFolder(folderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    fun scan() {
        val currentFolder = folder.value ?: return
        viewModelScope.launch {
            _isScanning.value = true
            try {
                fileRepository.scanFolder(folderId, currentFolder.uri)
            } finally {
                _isScanning.value = false
            }
        }
    }
}
