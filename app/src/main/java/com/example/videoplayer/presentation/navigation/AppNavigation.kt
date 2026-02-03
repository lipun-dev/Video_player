package com.example.videoplayer.presentation.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.videoplayer.presentation.ui.FolderScreen
import com.example.videoplayer.presentation.ui.FolderVideoScreen
import com.example.videoplayer.presentation.ui.HomeScreen
import com.example.videoplayer.presentation.ui.VideoPlayer
import com.example.videoplayer.viewModel.MyViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation(startDestination: NavigationItem, viewModel: MyViewModel,
                  pendingVideoUri: Uri? = null,
                  pendingVideoTitle: String? = null,
                  onVideoHandled: () -> Unit = {}) {

    val navController = rememberNavController()
    val animationDuration = 400

    LaunchedEffect(pendingVideoUri) {
        if (pendingVideoUri != null) {
            // IMPORTANT: Encode the URI for navigation
            val encodedUri = Uri.encode(pendingVideoUri.toString())

            // Navigate to video player
            navController.navigate(
                NavigationItem.Video_player(
                    VideoUri = encodedUri,
                    title = pendingVideoTitle ?: "External Video"
                )
            ) {
                // Clear back stack so user can't go back to home
                // This makes video player the first screen
                popUpTo(0) {
                    inclusive = true
                }
            }
            onVideoHandled()
        }
    }

    NavHost(navController = navController, startDestination = startDestination ) {


        composable<NavigationItem.HomeScreen>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(animationDuration)
                )
            }
        ) {

            HomeScreen(navController = navController, viewModel = viewModel)

        }
        composable<NavigationItem.Video_player> {backStackEntry->

            val args: NavigationItem.Video_player =backStackEntry.toRoute()


            VideoPlayer(args.VideoUri,viewModel, title = args.title, navController = navController)

        }
        composable<NavigationItem.AllVideoFolder>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(animationDuration)
                )
            }
        ) {
            FolderScreen(navController, viewModel = viewModel)
        }
        composable<NavigationItem.folderVideoScreen> (
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(animationDuration)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(animationDuration)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(animationDuration)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(animationDuration)
                )
            }
        ){backStackEntry->

            val folderName: NavigationItem.folderVideoScreen = backStackEntry.toRoute()
            FolderVideoScreen(navController,folderName.folderName, viewModel = viewModel)

        }




    }

}