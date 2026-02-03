package com.example.videoplayer.presentation.Utils.vControls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import com.example.videoplayer.viewModel.MyViewModel
import kotlinx.coroutines.delay

@Composable
fun VideoControls(player: ExoPlayer,
                  isVisible:()-> Boolean,
                  viewModel: MyViewModel,
                  onResizeClick: () -> Unit,
                  resizeMode: Int,
                  videoTitle: String,
                  onBackClick: () -> Unit,
                  onShareClick: () -> Unit) {
    var showTrackSheet = remember{
        mutableStateOf(false)
    }

    val audioTracks = viewModel.audioTracks.collectAsState()
    val subtitleTrack = viewModel.subtitleTracks.collectAsState()

    var isplaying = rememberSaveable { mutableStateOf(player.isPlaying) }
    val visible = rememberSaveable(isVisible()) { isVisible() }

    var totalDuration by rememberSaveable { mutableStateOf(0L) }

    var currentTime by rememberSaveable { mutableStateOf(0L) }

    var bufferedPercentage by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(key1 = player) {
        while (true) {
            if (player.isPlaying) {
                currentTime = player.currentPosition.coerceAtLeast(0L)
            }
            delay(1000L) // Update frequency. Decrease to 500L or 100L for smoother sliding
        }
    }
    AnimatedVisibility(
        modifier = Modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ){
            TopControls(
                modifier = Modifier.align(Alignment.TopCenter)
                    .animateEnterExit(
                        enter = slideInVertically(
                            initialOffsetY = {fullHeight ->
                                -fullHeight
                            }
                        ),
                        exit = slideOutVertically(
                            targetOffsetY = {fullHeight ->
                                -fullHeight
                            }
                        )
                    ),
                title = videoTitle,
                onBackClick = onBackClick,
                onCaptionClick = {
                    showTrackSheet.value = true
                },
                onAudioTrackClick = {
                    showTrackSheet.value = true
                },
                onShareClick = onShareClick
            )
            CentreControls(
                modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
                isPlaying = isplaying.value
            ) {actions ->
                viewModel.executeAction(actions)
            }

            BottomControls(modifier = Modifier.align(Alignment.BottomCenter)
                .fillMaxWidth()
                .animateEnterExit(
                    enter = slideInVertically(
                        initialOffsetY = {
                            fullHeight: Int ->
                            fullHeight
                        }
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = {fullHeight: Int ->
                            fullHeight
                        }
                    )
                ),
                totalDuration = {
                    totalDuration
                },
                currentTime = {
                    currentTime
                },
                bufferPercentage = {
                    bufferedPercentage
                },
                onSeekChanged = {timeMs ->
                    player.seekTo(timeMs.toLong())
                    currentTime = timeMs.toLong()
                },
                onResizeClick = onResizeClick,
                resizeMode = resizeMode
            )
        }

        TrackSelectionBottomSheet(
            visible = showTrackSheet.value,
            onDismiss = {
                showTrackSheet.value = false
            },
            audioTrack = audioTracks.value,
            subtitleTrack = subtitleTrack.value,
            onTrackSelected = {type , groupIndex->
                viewModel.selectTrack(player,type,groupIndex)
            },
            onDisableTrack = {type->
                viewModel.disableTrack(player = player, trackType =type )
            }
        )
    }


    Box(modifier = Modifier) {
        DisposableEffect(key1 = player) {
            val listener =
                object : Player.Listener {
                    override fun onEvents(player: Player, events: Player.Events) {
                        super.onEvents(player, events)
                        totalDuration = player.duration.coerceAtLeast(0L)
                        currentTime = player.currentPosition.coerceAtLeast(0L)
                        bufferedPercentage = player.bufferedPercentage
                        isplaying.value = player.isPlaying
                    }

                    override fun onTracksChanged(tracks: Tracks) {
                        viewModel.extractTracks(player = player)
                    }

                }

            player.addListener(listener)
            viewModel.extractTracks(player)

            onDispose {
                player.removeListener(listener)
            }
        }
    }

    
}

