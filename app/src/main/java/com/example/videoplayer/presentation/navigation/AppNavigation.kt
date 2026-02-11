package com.example.videoplayer.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.net.toUri
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
fun AppNavigation(startDestination: NavigationItem, viewModel: MyViewModel) {

    val navController = rememberNavController()
    val animationDuration = 400

    LaunchedEffect(viewModel.navigateToPlayerTrigger) {
        if (viewModel.navigateToPlayerTrigger) {
            // Navigate WITHOUT passing the complex URI string
            navController.navigate(
                NavigationItem.Video_player(
                    VideoUri = null, // Passing null indicates "Check ViewModel"
                    title = viewModel.currentExternalVideoTitle
                )
            ) {
                // Optional: Clear backstack only if you want the app to close after video
                popUpTo(0) { inclusive = true }
            }
            viewModel.onNavigationHandled()
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

            val args = backStackEntry.toRoute<NavigationItem.Video_player>()


            val effectiveUri = if (args.VideoUri != null) {
                args.VideoUri.toUri()
            } else {
                viewModel.currentExternalVideoUri
            }

            val effectiveTitle = args.title ?: viewModel.currentExternalVideoTitle ?: "Video"

            if (effectiveUri != null) {
                VideoPlayer(
                    uri = effectiveUri.toString(),
                    viewModel = viewModel,
                    title = effectiveTitle,
                    navController = navController
                )
            } else {
                // Fallback if something went wrong
                LaunchedEffect(Unit) { navController.popBackStack() }
            }

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