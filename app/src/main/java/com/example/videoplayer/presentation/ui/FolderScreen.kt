package com.example.videoplayer.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.videoplayer.presentation.Utils.FolderCart
import com.example.videoplayer.viewModel.MyViewModel

@Composable
fun FolderScreen(
    navController: NavController,
    viewModel: MyViewModel

) {
    val videoFolder = viewModel.FolderList.collectAsState().value

    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ){
        if(videoFolder.isEmpty()){
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Folders found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }else{
            items(videoFolder.toList()) {(folderName,video)->

                FolderCart(
                    folderName = folderName,
                    videoCount = video.size,
                    navController = navController

                )



            }

        }


    }

}