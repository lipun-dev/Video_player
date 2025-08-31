package com.example.videoplayer.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.animation.OvershootInterpolator
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.videoplayer.R
import com.example.videoplayer.presentation.navigation.AppNavigation
import com.example.videoplayer.presentation.navigation.NavigationItem
import com.example.videoplayer.viewModel.MyViewModel
import kotlinx.coroutines.delay


@OptIn(UnstableApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MainScreen(viewModel: MyViewModel = hiltViewModel()) {

    val context = LocalContext.current

    // ✅ OS-level permission check (authoritative)
    val mediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_VIDEO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    val actualGranted = remember {
        ContextCompat.checkSelfPermission(
            context,
            mediaPermission
        ) == PackageManager.PERMISSION_GRANTED
    }
    var showSplash = remember { mutableStateOf(true) }

    if (showSplash.value) {
        SplashScreen(
            onFinished = { showSplash.value = false }
        )
    } else {
        when {

            actualGranted -> {
                Log.d("MainScreen", "state = true -> startDestination = HomeScreen")
                // Permission already granted → go home
                AppNavigation(startDestination = NavigationItem.HomeScreen)
            }
            else -> {
                Log.d("MainScreen", "state != true -> startDestination = app")
                // Permission not granted → go to App (your permission screen)
                AppNavigation(startDestination = NavigationItem.App)
            }
        }
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 500ms overshoot scale
        scale.animateTo(
            targetValue = 3f,
            animationSpec = tween(
                durationMillis = 500,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        // Optional: tiny pause, not 3 seconds
        delay(300)
        onFinished()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.play),
            contentDescription = null,
            modifier = Modifier.size(50.dp).scale(scale.value)
        )
    }
}