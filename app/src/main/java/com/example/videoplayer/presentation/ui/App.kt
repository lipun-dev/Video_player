package com.example.videoplayer.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.videoplayer.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel = hiltViewModel()
) {

//    val mediaPermission = rememberPermissionState(
//        permission = Manifest.permission.READ_MEDIA_VIDEO
//    )
//    val mediaPermissionLauncher =
//        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
//            if(it){
//                viewModel.showUi.value = true
//
//            }else{
//                viewModel.showUi.value = false
//            }
//        }
//
//    LaunchedEffect(key1 = mediaPermission) {
//
//        if(!mediaPermission.status.isGranted){
//            mediaPermissionLauncher.launch(
//                Manifest.permission.READ_MEDIA_VIDEO
//            )
//        }else{
//            viewModel.showUi.value = true
//        }
//    }
//
//    val state = viewModel.showUi.collectAsState()
//
//    if(state.value){
//        HomeScreen(navController)
//    }else{
//        Box (
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ){
//            Text(text = "Please Grant Permission")
//
//        }
//    }

//    LaunchedEffect(Unit) {
//        mediaPermission.launchPermissionRequest()
//    }
//    val state = viewModel.showUi.collectAsState()
//
//    if (mediaPermission.status.isGranted) {
//        state.value = true
//        HomeScreen(navController)
//    } else {
//        viewModel.showUi.value = false
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text("Please Grant Permission")
//        }
//    }

    val context = LocalContext.current

    val mediaPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.showUi.value = granted
        }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VIDEO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            mediaPermissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            viewModel.showUi.value = true
        }
    }

    val state = viewModel.showUi.collectAsState()

    if (state.value) {
        HomeScreen(navController)
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Please Grant Permission")
        }
    }
}
