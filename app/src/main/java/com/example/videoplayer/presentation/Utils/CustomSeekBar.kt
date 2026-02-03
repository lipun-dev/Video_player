package com.example.videoplayer.presentation.Utils

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun CustomSeekBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    bufferedPercentage: Float = 0f, // 0.0 to 1.0
    trackHeight: Dp = 4.dp,
    thumbRadius: Dp = 8.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    bufferedColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
) {
    val interactionSource = remember { MutableInteractionSource() }
    val interactions = remember { mutableStateListOf<Interaction>() }

    // Logic to detect if the user is currently dragging/pressing the slider
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    val isInteracting = interactions.isNotEmpty()

    // Haptic Feedback for better UX
    val view = LocalView.current
    LaunchedEffect(isInteracting) {
        if (isInteracting) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    // ANIMATION: Thumb grows when interacting
    val currentThumbRadius by animateDpAsState(
        targetValue = if (isInteracting) thumbRadius * 1.5f else thumbRadius,
        animationSpec = tween(durationMillis = 200),
        label = "ThumbAnimation"
    )

    // ANIMATION: Glow opacity
    val glowAlpha by animateFloatAsState(
        targetValue = if (isInteracting) 0.3f else 0.0f,
        animationSpec = tween(durationMillis = 200),
        label = "GlowAnimation"
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp), // Increased height for easier touch
        thumb = {
            Box(contentAlignment = Alignment.Center) {
                // Glow Effect
                Box(
                    modifier = Modifier
                        .size(currentThumbRadius * 2.5f)
                        .background(activeColor.copy(alpha = glowAlpha), CircleShape)
                )

                // Actual Thumb
                Box(
                    modifier = Modifier
                        .size(currentThumbRadius * 2)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                ) {
                    // Inner Dot
                    Box(
                        modifier = Modifier
                            .size(currentThumbRadius)
                            .background(activeColor, CircleShape)
                            .align(Alignment.Center)
                    )
                }
            }
        },
        track = { sliderState ->
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
            ) {
                val width = size.width
                val height = size.height
                val cornerRadius = CornerRadius(height / 2, height / 2)

                // 1. Draw Background Track (Inactive)
                drawRoundRect(
                    color = inactiveColor,
                    size = Size(width, height),
                    cornerRadius = cornerRadius
                )

                // 2. Draw Buffered Progress
                // We map 0..1 buffer percentage to canvas width
                val bufferedEnd = width * bufferedPercentage
                drawRoundRect(
                    color = bufferedColor,
                    size = Size(bufferedEnd, height),
                    cornerRadius = cornerRadius
                )

                // 3. Draw Active Progress (The video played so far)
                // sliderState.positionFraction gives the thumb position 0..1
                val activeEnd = width * sliderState.value
                drawRoundRect(
                    color = activeColor,
                    size = Size(activeEnd, height),
                    cornerRadius = cornerRadius
                )
            }
        }
    )
}