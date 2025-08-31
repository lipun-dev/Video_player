package com.example.videoplayer.presentation.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.CustomTopAppBar
import com.example.videoplayer.presentation.Utils.videoCard
import com.example.videoplayer.viewModel.MyViewModel


@OptIn(UnstableApi::class)
@Composable
fun FolderVideoScreen(navController: NavController,folderName: String,
                      viewModel: MyViewModel
) {
    val name = folderName.split("/").lastOrNull().toString()
    Scaffold (
        topBar ={ CustomTopAppBar(
            topAppBarText = name,
            navController = navController

        )}
    ){innerpadding->

        Log.d("FolderVideoScreen", "Step1: Using ViewModel instance ID = ${viewModel.getInstanceId()}")

        val videoFolder = viewModel.FolderList.collectAsStateWithLifecycle().value

        val videosInFolder = videoFolder[folderName]?:emptyList()


        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerpadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            if(videosInFolder.isEmpty()){
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No videos found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }else{
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



}