package com.example.androidsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidsync.ui.screen.folderdetail.FolderDetailScreen
import com.example.androidsync.ui.screen.folderlist.FolderListScreen
import com.example.androidsync.ui.screen.settings.SettingsScreen
import com.example.androidsync.ui.theme.AndroidSyncTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidSyncTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf("folders", "settings")

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Folder, contentDescription = null) },
                                    label = { Text("Folders") },
                                    selected = navBackStackEntry?.destination?.hierarchy?.any { it.route == "folders" } == true,
                                    onClick = {
                                        navController.navigate("folders") {
                                            popUpTo("folders") { inclusive = true }
                                        }
                                    },
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                    label = { Text("Settings") },
                                    selected = currentRoute == "settings",
                                    onClick = {
                                        navController.navigate("settings") {
                                            popUpTo("folders")
                                            launchSingleTop = true
                                        }
                                    },
                                )
                            }
                        }
                    },
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "folders",
                        modifier = Modifier.padding(innerPadding),
                    ) {
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
                        composable("settings") {
                            SettingsScreen()
                        }
                    }
                }
            }
        }
    }
}
