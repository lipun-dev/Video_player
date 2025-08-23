package com.example.videoplayer.presentation.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.videoplayer.viewModel.MyViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@androidx.annotation.OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun App(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MyViewModel
) {
    val context = LocalContext.current

    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
        } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
        }
//


    val readVideoPermissionState = rememberPermissionState(permission = mediaPermission)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.setPermissionGranted(isGranted)
    }

    // Optionally auto-request on first launch if not granted
    LaunchedEffect(Unit) {
        if (!readVideoPermissionState.status.isGranted) {
            permissionLauncher.launch(mediaPermission)

        }else{
            viewModel.setPermissionGranted(true)
        }
    }

    // If you also want to read VM state in UI:
    val showUi by viewModel.showUi.collectAsState()

    when {
        readVideoPermissionState.status.isGranted && showUi -> {
            //9cf214fe-592e-469b-8cd6-56ff3df49a6e
            Log.d("App", "Step1: Using ViewModel instance ID = ${viewModel.getInstanceId()}")
            HomeScreen(navController, viewModel = viewModel)
        }

        readVideoPermissionState.status.shouldShowRationale -> {
            // User denied once (no "Don't ask again")
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("We need video access to show your media.")
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { permissionLauncher.launch(mediaPermission) }) {
                    Text("Grant Permission")
                }
            }
        }

        else -> {
            // First time OR permanently denied ("Don't ask again")
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Please grant access to your videos.")
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = { permissionLauncher.launch(mediaPermission) }) {
                    Text("Request Permission")
                }

                // If permanently denied, offer Settings shortcut
                if (!readVideoPermissionState.status.isGranted &&
                    !readVideoPermissionState.status.shouldShowRationale
                ) {
                    Spacer(Modifier.height(8.dp))
                    val openSettings = {
                        val intent = Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    }
                    TextButton(onClick = openSettings) { Text("Open App Settings") }
                }
            }
        }


    }
}
