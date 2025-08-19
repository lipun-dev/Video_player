package com.example.videoplayer.presentation.Utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameMillis
import com.example.videoplayer.presentation.navigation.NavigationItem

@Composable
fun videoCard(
    path: String,
    title: String,
    size: String,
    duration: String,
    dateAdded: String,
    fileName: String,
    thumbnail: String,
    id: String,
    navController: NavController
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        add(VideoFrameDecoder.Factory())
    }.build()

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable{
                navController.navigate(NavigationItem.Video_player(VideoUri = path, title = title))
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(thumbnail)
                    .videoFrameMillis(1000).build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier.size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                imageLoader = imageLoader
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title.takeIf { it.isNotBlank() }?:"Untitled",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis

                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Duration : ${formatter(duration.toLongOrNull()?:0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Size : ${formatFIleSize(size.toLongOrNull()?:0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Added : ${formatDate(dateAdded.toLongOrNull()?:0)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )



            }

        }




    }

}