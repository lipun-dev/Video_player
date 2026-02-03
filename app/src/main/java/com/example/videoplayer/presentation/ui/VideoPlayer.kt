package com.example.videoplayer.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.videoplayer.data.ActionType
import com.example.videoplayer.data.playerActions
import com.example.videoplayer.presentation.Utils.formatter
import com.example.videoplayer.presentation.Utils.vControls.VideoControls
import com.example.videoplayer.presentation.Utils.vControls.shareVideo
import com.example.videoplayer.viewModel.MyViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(uri: String,viewModel: MyViewModel,title: String?,navController: NavController) {

    val context = LocalContext.current
    val exoplayer = viewModel.playerState.collectAsStateWithLifecycle()
    var controlvisible by rememberSaveable { mutableStateOf(true) }

    val videoUri = remember(uri) {
        try {
            // First decode, then parse
            val decodedUri = Uri.decode(uri)
            decodedUri.toUri()
        } catch (e: Exception) {
            Log.e("VideoPlayer", "Failed to parse URI: $uri", e)
            null
        }
    }

    LaunchedEffect(videoUri) {
        if (videoUri != null) {
            Log.d("VideoPlayer", "Creating player with URI: $videoUri")
            viewModel.createPlayerWithMediaItems(context = context, uri = videoUri.toString())
        } else {
            Log.e("VideoPlayer", "Invalid URI, cannot create player")
            // Navigate back if URI is invalid
            navController.popBackStack()
        }
    }

    val activity = remember { context.findActivity() }
    val audiomanager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVol = remember { audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }


    var showVolumeUI = remember { mutableStateOf(false) }
    var showBrightnessUI = remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var hideJob by remember { mutableStateOf<Job?>(null) }


    var gestureText = remember { mutableStateOf<String?>(null) }
    var currentVolume by remember { mutableFloatStateOf(0f) }
    var currentBrightness by remember { mutableFloatStateOf(0.5f) }

    var resizeMode by rememberSaveable { mutableIntStateOf(AspectRatioFrameLayout.RESIZE_MODE_FIT) }

    Box(modifier = Modifier.fillMaxSize()
        .background(Color.Black)
        .pointerInput(Unit){
            detectTapGestures(
                onTap = {
                    controlvisible = !controlvisible
                }
            )
        }
        .pointerInput(Unit){
            detectDragGestures(
                onDragStart = {
                    currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                    val sysBrightness = activity?.window?.attributes?.screenBrightness?:-1f
                    currentBrightness = if(sysBrightness < 0) 0.5f else sysBrightness

                    hideJob?.cancel()
                    showVolumeUI.value = false
                    showBrightnessUI.value = false
                },
                onDragEnd = {
                    hideJob = scope.launch {
                        delay(2000) // Keep visible for 2 seconds after let go
                        showVolumeUI.value = false
                        showBrightnessUI.value = false
                    }
                },
                onDragCancel = {
                    gestureText.value = null
                },
                onDrag = {change, dragAmount ->
                    val width = size.width
                    val height = size.height
                    val touchX = change.position.x
                    change.consume()
                    if (kotlin.math.abs(dragAmount.x) > kotlin.math.abs(dragAmount.y)) {
                        // --- SEEK (Horizontal) ---
                        val seekSensitivity = 100 // 1px drag = 100ms
                        val seekAmount = (dragAmount.x * seekSensitivity).toLong()

                        exoplayer.value?.let { player ->
                            val newPos = (player.currentPosition + seekAmount).coerceIn(0, player.duration)
                            player.seekTo(newPos)

                            // Formatting time for display
                            gestureText.value = "SKIP:${formatter(newPos)}"
                        }

                    }else{
                        val deltaY = -dragAmount.y / height
                        val sensitivity = 3f

                        if(touchX < width/2){
                            val changeVal = deltaY * sensitivity
                            val newBrightness = (currentBrightness + changeVal).coerceIn(0f,1f)
                            activity?.window?.attributes = activity.window?.attributes?.apply {
                                screenBrightness = newBrightness
                            }
                            currentBrightness = newBrightness
                            showBrightnessUI.value = true  // <--- Trigger UI
                            showVolumeUI.value = false
                        }else{
                            val changeVal = deltaY * maxVol * sensitivity
                            val newVol = (currentVolume + changeVal).coerceIn(0f, maxVol.toFloat())

                            audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol.toInt(), 0)
                            currentVolume = newVol
                            showVolumeUI.value = true      // <--- Trigger UI
                            showBrightnessUI.value = false
                        }
                    }


                }

            )
        }
        ){
        exoplayer.value?.let {
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoplayer.value
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT

                        )
                        useController = false
                        setKeepContentOnPlayerReset(true)

                    }
                },
                modifier = Modifier.fillMaxSize()
                    .align(Alignment.Center),
                update = {view ->
                    view.resizeMode = resizeMode

                }
            )

            VideoControls(it,
                isVisible = {
                    controlvisible
                },
                viewModel = viewModel,
                resizeMode =resizeMode,
                onResizeClick = {
                    resizeMode = when(resizeMode){
                        AspectRatioFrameLayout.RESIZE_MODE_FIT -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                        else -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                },
                videoTitle = title?:"Unknown",
                onBackClick = {
                    navController.popBackStack()
                },
                onShareClick = {
                    shareVideo(context = context, videoLocation = uri)
                }

            )

            VideoGestureFeedbackUI(
                showBrightness = showBrightnessUI.value,
                showVolume = showVolumeUI.value,
                brightnessLevel = currentBrightness,
                volumeLevel = currentVolume / maxVol.toFloat() // Normalize volume to 0.0 - 1.0
            )



        }

    }

    LaunchedEffect(Unit) {
        viewModel.createPlayerWithMediaItems(context = context, uri = uri)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> viewModel.executeAction(playerActions(ActionType.PAUSE))
                Lifecycle.Event.ON_RESUME -> viewModel.executeAction(playerActions(ActionType.PLAY))
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.releasePlayer()
        }
    }

    LaunchedEffect(exoplayer.value?.isPlaying,controlvisible) {
        if(exoplayer.value?.isPlaying == true  && controlvisible){
            delay(3000)
            controlvisible = false
        }
    }


}



//helper function to find the activity
fun Context.findActivity(): Activity? = when(this){
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun VideoGestureFeedbackUI(
    showBrightness: Boolean,
    showVolume: Boolean,
    brightnessLevel: Float, // 0.0 to 1.0
    volumeLevel: Float,     // 0.0 to 1.0
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Left Side: Brightness
        AnimatedIndicator(
            isVisible = showBrightness,
            value = brightnessLevel,
            icon = Icons.Default.BrightnessMedium,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 32.dp)
        )

        // Right Side: Volume
        AnimatedIndicator(
            isVisible = showVolume,
            value = volumeLevel,
            icon = Icons.AutoMirrored.Filled.VolumeUp,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp)
        )
    }
}

@Composable
fun AnimatedIndicator(
    isVisible: Boolean,
    value: Float,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .width(50.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                // The Bar
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .width(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Animated Fill
                    val fillHeight by animateFloatAsState(targetValue = value, label = "fill")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(fillHeight)
                            .background(Color.White, RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}