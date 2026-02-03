package com.example.videoplayer.presentation.Utils.vControls

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun TopControls(modifier: Modifier = Modifier,
                title: String,
                onBackClick: () -> Unit,
                onCaptionClick: () -> Unit,
                onAudioTrackClick: () -> Unit,
                onShareClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.7f),Color.Transparent)
                )
            )
            .padding(top = 32.dp, bottom = 16.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {


            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back Arrow",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }


            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                modifier = Modifier.weight(1f)
                    .padding(horizontal = 8.dp)
                    .basicMarquee(
                        iterations = Int.MAX_VALUE,
                        velocity = 40.dp
                    )
            )


        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End) {

            IconButton(onClick = onCaptionClick,
                modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.ClosedCaption, // Ensure you have extended icons or use a resource
                    contentDescription = "Captions",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = onAudioTrackClick,
                modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.Audiotrack,
                    contentDescription = "Audio Track",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onShareClick,
                modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Video",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }

}


fun shareVideo(context: Context, videoLocation: String) {
    try {
        val file = File(videoLocation) // Assuming videoLocation is a local file path

        // Generate a content URI using FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share Video"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}