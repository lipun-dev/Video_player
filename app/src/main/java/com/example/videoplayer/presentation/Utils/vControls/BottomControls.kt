package com.example.videoplayer.presentation.Utils.vControls

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.FitScreen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.videoplayer.presentation.Utils.CustomSeekBar
import com.example.videoplayer.presentation.Utils.formatter

@OptIn(UnstableApi::class)
@Composable
fun BottomControls(modifier: Modifier = Modifier,
                   totalDuration:() -> Long,
                   currentTime:() -> Long,
                   bufferPercentage:() -> Int,
                   onSeekChanged:(timeMs: Float) -> Unit,
                   onResizeClick: () -> Unit,
                   resizeMode: Int) {
    val duration = rememberSaveable(totalDuration()) { totalDuration() }

    val videoTime = rememberSaveable(currentTime()) { currentTime() }

    val buffer = rememberSaveable(bufferPercentage()) { bufferPercentage() }

    val resizeIcon = when(resizeMode){
        AspectRatioFrameLayout.RESIZE_MODE_FIT -> Icons.Default.FitScreen
        AspectRatioFrameLayout.RESIZE_MODE_ZOOM -> Icons.Default.Crop
        AspectRatioFrameLayout.RESIZE_MODE_FILL -> Icons.Default.AspectRatio
        else -> Icons.Default.FitScreen
    }

    var isDragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val sliderValue = if (isDragging) {
        dragProgress
    } else {
        if (duration > 0) videoTime.toFloat() / duration else 0f
    }
    Column (
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                )
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. Current Time
            Text(
                text = formatter(if (isDragging) (dragProgress * duration).toLong() else videoTime),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.widthIn(min = 45.dp)
            )

            // 2. Seek Bar (Fills the middle)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomSeekBar(
                    value = sliderValue,
                    bufferedPercentage = buffer / 100f,
                    onValueChange = {
                        isDragging = true
                        dragProgress = it
                    },
                    onValueChangeFinished = {
                        val newTimeMs = dragProgress * duration
                        onSeekChanged(newTimeMs)
                        isDragging = false
                    },
                    activeColor = Color(0xFFD0BCFF),
                    trackHeight = 4.dp,
                    thumbRadius = 8.dp
                )
            }

            // 3. Total Duration
            Text(
                text = formatter(duration),
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.widthIn(min = 45.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp) // Space between seekbar and button
        ) {
            IconButton(
                onClick = onResizeClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd) // Pushes icon to the far right
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = resizeIcon,
                    contentDescription = "Resize Mode",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
    
}