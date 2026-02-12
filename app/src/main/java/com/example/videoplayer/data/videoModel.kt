package com.example.videoplayer.data

import androidx.compose.runtime.Immutable

@Immutable
data class videoModel(
    val id: String?,
    val path: String?,
    val title: String?,
    val filename: String,
    val size: String,
    val duration: String,
    val dateAdded: String,
    val thumbnailUri: String? = null

)