package com.example.videoplayer.presentation.ui

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.videoCard
import com.example.videoplayer.viewModel.MyViewModel


@OptIn(UnstableApi::class)
@Composable
fun AllVideoScreen(navController: NavController,
                   modifier: Modifier = Modifier,
                   viewModel: MyViewModel = hiltViewModel<MyViewModel>()
) {

    Column (
        modifier = Modifier.fillMaxSize()
    ){
        LaunchedEffect(key1 = true) {
            viewModel.LoadAllvideos()
        }

        val videoList = viewModel.videoList.collectAsState()
        //9cf214fe-592e-469b-8cd6-56ff3df49a6e  9cf214fe-592e-469b-8cd6-56ff3df49a6e
        Log.d("AllVideoScreen", "Step1: Using ViewModel instance ID = ${viewModel.getInstanceId()}")

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if(videoList.value.isEmpty()){
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
                items(videoList.value) {

                    videoCard(
                        path = it.path?:"Unknown",
                        title = it.title?:"Untitled",
                        size = it.size,
                        duration = it.duration,
                        dateAdded = it.dateAdded,
                        fileName = it.filename,
                        thumbnail = it.thumbnailUri.toString(),
                        id = it.id.toString(),
                        navController = navController
                    )
                }

            }



        }

    }

}