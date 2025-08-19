package com.example.videoplayer.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.CustomTopAppBar
import com.example.videoplayer.presentation.Utils.videoCard
import com.example.videoplayer.viewModel.MyViewModel


@Composable
fun FolderVideoScreen(navController: NavController,folderName: String,
                      viewModel: MyViewModel = hiltViewModel()
) {
    Scaffold (
        topBar ={ CustomTopAppBar(
            topAppBarText = folderName,
            navController = navController

        )}
    ){innerpadding->

        val videoFolder = viewModel.FolderList.collectAsState().value
        val videosInFolder = videoFolder[folderName]?:emptyList()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerpadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(
                videosInFolder
            ) {video->

                videoCard(
                    path = video.path?:"Unknown",
                    title = video.title?:"Untitled",
                    size = video.size?:"Unknown",
                    duration = video.duration?:"Unknown",
                    dateAdded = video.dateAdded?:"Unknown",
                    fileName = video.filename?:"Unknown",
                    thumbnail = video.thumbnailUri?:"Unknown",
                    id = video.id?:"Unknown",
                    navController = navController
                )

            }


        }

    }



}