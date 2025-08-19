package com.example.videoplayer.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.videoCard
import com.example.videoplayer.viewModel.MyViewModel


@Composable
fun AllVideoScreen(navController: NavController,
                   modifier: Modifier = Modifier,
                   viewModel: MyViewModel = hiltViewModel()
) {

    Column (
        modifier = Modifier.fillMaxSize()
    ){
        LaunchedEffect(key1 = true) {
            viewModel.LoadAllvideos()
        }

        val videoList = viewModel.videoList.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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