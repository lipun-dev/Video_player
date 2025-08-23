package com.example.videoplayer.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.videoplayer.presentation.ui.App
import com.example.videoplayer.presentation.ui.FolderScreen
import com.example.videoplayer.presentation.ui.FolderVideoScreen
import com.example.videoplayer.presentation.ui.HomeScreen
import com.example.videoplayer.presentation.ui.VideoPlayer
import com.example.videoplayer.viewModel.MyViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavigationItem.App ) {

        composable<NavigationItem.App> {backStackEntry->
            val viewModel = hiltViewModel<MyViewModel>(backStackEntry)
            App(navController, viewModel = viewModel)



        }
        composable<NavigationItem.HomeScreen> {backStackEntry->
            val viewModel = hiltViewModel<MyViewModel>(backStackEntry)

            HomeScreen(navController = navController, viewModel = viewModel)

        }
        composable<NavigationItem.Video_player> {BackStackEntry->
            val viewModel = hiltViewModel<MyViewModel>(BackStackEntry)

            val Uri: NavigationItem.Video_player = BackStackEntry.toRoute()
            VideoPlayer(Uri.VideoUri,navController)

        }
        composable<NavigationItem.AllVideoFolder> {backStackEntry->
            val viewModel = hiltViewModel<MyViewModel>(backStackEntry)

            val folderName: NavigationItem.AllVideoFolder = backStackEntry.toRoute()
            FolderScreen(navController, viewModel = viewModel)
        }
        composable<NavigationItem.folderVideoScreen> {backStackEntry->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavigationItem.App)
            }
            val viewModel = hiltViewModel<MyViewModel>(parentEntry)

            val folderName: NavigationItem.folderVideoScreen = backStackEntry.toRoute()
            FolderVideoScreen(navController,folderName.folderName, viewModel = viewModel)

        }




    }

}