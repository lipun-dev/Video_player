package com.example.videoplayer.presentation.ui

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
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
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.videoCard
import com.example.videoplayer.presentation.navigation.NavigationItem
import com.example.videoplayer.viewModel.MyViewModel


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(UnstableApi::class)
@Composable
fun AllVideoScreen(navController: NavController,
                   modifier: Modifier = Modifier,
                   viewModel: MyViewModel
) {

    Column (
        modifier = Modifier.fillMaxSize()
    ){

        val videoList = viewModel.videoList.collectAsState()
        //9cf214fe-592e-469b-8cd6-56ff3df49a6e  9cf214fe-592e-469b-8cd6-56ff3df49a6e
        Log.d("AllVideoScreen", "Step1: Using ViewModel instance ID = ${viewModel.getInstanceId()}")

        LaunchedEffect(key1 = Unit) {
            if(videoList.value.isEmpty()){
                viewModel.LoadAllvideos()
            }
        }
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
                items(videoList.value) {video->

                    videoCard(
                        video = video,
                        onPlayClick = {
                            navController.navigate(
                                NavigationItem.Video_player(
                                    VideoUri = video.path, // Assuming path is safe or ID
                                    title = video.title
                                )
                            )
                        },
                        onFileChanged = {
                            viewModel.LoadAllvideos()
                        }
                    )
                }

            }



        }

    }

}