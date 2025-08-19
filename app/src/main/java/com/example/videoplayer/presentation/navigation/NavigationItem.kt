package com.example.videoplayer.presentation.navigation

import kotlinx.serialization.Serializable

sealed class NavigationItem {


    @Serializable
    object App

    @Serializable
    object HomeScreen

    @Serializable
    data class Video_player(val VideoUri: String,val title: String? = null)

    @Serializable
    data class folderVideoScreen(val folderName: String)

    @Serializable
    data class AllVideoFolder(val folderName: String)

}