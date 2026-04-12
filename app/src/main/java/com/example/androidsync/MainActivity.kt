package com.example.androidsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.androidsync.ui.screen.folderlist.FolderListScreen
import com.example.androidsync.ui.theme.AndroidSyncTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSyncTheme {
                FolderListScreen()
            }
        }
    }
}
