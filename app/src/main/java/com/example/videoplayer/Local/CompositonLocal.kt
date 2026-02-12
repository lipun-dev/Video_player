package com.example.videoplayer.Local

import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader

val LocalVideoImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("No ImageLoader provided")
}