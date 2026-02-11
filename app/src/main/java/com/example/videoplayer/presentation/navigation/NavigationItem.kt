package com.example.videoplayer.presentation.navigation

import kotlinx.serialization.Serializable

sealed class NavigationItem {




    @Serializable
    object HomeScreen: NavigationItem()

    @Serializable
    data class Video_player(val VideoUri: String? = null,val title: String? = null): NavigationItem()

    @Serializable
    data class folderVideoScreen(val folderName: String): NavigationItem()

    @Serializable
    data class AllVideoFolder(val folderName: String): NavigationItem()

}