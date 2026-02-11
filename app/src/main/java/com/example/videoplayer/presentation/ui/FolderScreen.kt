package com.example.videoplayer.presentation.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.videoplayer.R
import com.example.videoplayer.presentation.Utils.FolderCart
import com.example.videoplayer.presentation.navigation.NavigationItem
import com.example.videoplayer.viewModel.MyViewModel

@Composable
fun FolderScreen(
    navController: NavController,
    viewModel: MyViewModel

) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. Define Permission
    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    // 2. Local state for permission status
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                mediaPermission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 3. Permission Launcher (For the "Grant Permission" button)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted // Update state immediately
        if (isGranted) {
            viewModel.setPermissionGranted(true)
            viewModel.LoadAllvideos()
            viewModel.LoadFolderVideos()
        }
    }

    // 4. Lifecycle Observer (CRITICAL for "Settings" return)
    // This checks permission every time the app comes to the foreground
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGranted = ContextCompat.checkSelfPermission(
                    context,
                    mediaPermission
                ) == PackageManager.PERMISSION_GRANTED

                // If status changed or we have permission but data is empty, reload
                if (isGranted != hasPermission || (isGranted && viewModel.FolderList.value.isEmpty())) {
                    hasPermission = isGranted
                    if (isGranted) {
                        viewModel.setPermissionGranted(true)
                        viewModel.LoadAllvideos()
                        viewModel.LoadFolderVideos()
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Collect data
    val videoFolder = viewModel.FolderList.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    // 5. Open Settings Function
    val openSettings = {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.back_black))
    ) {
        // CONTENT LOGIC
        if (hasPermission) {
            // Permission Granted: Show List

                when{
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){

                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    videoFolder.isEmpty()->{
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No Folders found",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                    else->{
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(videoFolder.toList()) { (folderName, videos) ->
                                FolderCart(
                                    folderName = folderName,
                                    videoCount = videos.size,
                                    onClick = {
                                        navController.navigate(
                                            NavigationItem.folderVideoScreen(
                                                folderName = folderName
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }

                }

        } else {

            // Permission Denied: Show Overlay
            PermissionRequestOverlay(
                // If we are here, it's denied. We assume permanent if rationale is false,
                // but for simplicity, we provide both options to the user.
                permanentlyDenied = true,
                onRequest = { permissionLauncher.launch(mediaPermission) },
                onOpenSettings = openSettings
            )
        }
    }

}