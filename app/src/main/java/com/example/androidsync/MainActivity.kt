package com.example.androidsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidsync.ui.screen.folderdetail.FolderDetailScreen
import com.example.androidsync.ui.screen.folderlist.FolderListScreen
import com.example.androidsync.ui.theme.AndroidSyncTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSyncTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "folders") {
                    composable("folders") {
                        FolderListScreen(
                            onFolderClick = { folder ->
                                navController.navigate("folders/${folder.id}")
                            },
                        )
                    }
                    composable(
                        route = "folders/{folderId}",
                        arguments = listOf(navArgument("folderId") { type = NavType.LongType }),
                    ) {
                        FolderDetailScreen(
                            onBack = { navController.popBackStack() },
                        )
                    }
                }
            }
        }
    }
}
