package com.example.videoplayer.presentation.navigation

import kotlinx.serialization.Serializable

sealed class NavigationItem {


    @Serializable
    object App: NavigationItem()

    @Serializable
    object HomeScreen: NavigationItem()

    @Serializable
    data class Video_player(val VideoUri: String,val title: String? = null): NavigationItem()

    @Serializable
    data class folderVideoScreen(val folderName: String): NavigationItem()

    @Serializable
    data class AllVideoFolder(val folderName: String): NavigationItem()

}