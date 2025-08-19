package com.example.videoplayer.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.videoplayer.presentation.ui.App
import com.example.videoplayer.presentation.ui.FolderScreen
import com.example.videoplayer.presentation.ui.FolderVideoScreen
import com.example.videoplayer.presentation.ui.HomeScreen
import com.example.videoplayer.presentation.ui.VideoPlayer

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavigationItem.HomeScreen ) {

        composable<NavigationItem.App> {
            App(navController)



        }
        composable<NavigationItem.HomeScreen> {

            HomeScreen(navController = navController)

        }
        composable<NavigationItem.Video_player> {BackStackEntry->

            val Uri: NavigationItem.Video_player = BackStackEntry.toRoute()
            VideoPlayer(Uri.VideoUri,navController)

        }
        composable<NavigationItem.AllVideoFolder> {

            val folderName: NavigationItem.AllVideoFolder = it.toRoute()
            FolderScreen(navController)
        }
        composable<NavigationItem.folderVideoScreen> {

            val folderName: NavigationItem.folderVideoScreen = it.toRoute()
            FolderVideoScreen(navController,folderName.folderName)

        }




    }

}