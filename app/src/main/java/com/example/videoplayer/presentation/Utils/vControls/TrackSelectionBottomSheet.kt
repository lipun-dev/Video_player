package com.example.videoplayer.presentation.Utils.vControls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import com.example.videoplayer.data.VideoTrackInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSelectionBottomSheet(
    visible: Boolean,
    onDismiss:() -> Unit,
    audioTrack: List<VideoTrackInfo>,
    subtitleTrack:List<VideoTrackInfo>,
    onTrackSelected:(Int, Int) -> Unit,
    onDisableTrack:(Int)-> Unit
) {

    if(visible){
        ModalBottomSheet(
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Audio & Subtitles",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text("Audio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)

                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(audioTrack){track->
                        TrackItemRow(track) {
                            // Use group index extracted earlier (parsing ID back to Int)
                            onTrackSelected(C.TRACK_TYPE_AUDIO, track.id.toInt())
                        }

                    }

                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                Text("Subtitles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold)

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    // Option to turn off subtitles
                    item {
                        Text(
                            text = "Off",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {  onDisableTrack(C.TRACK_TYPE_TEXT) }
                                .padding(12.dp)
                        )
                    }
                    items(subtitleTrack) { track ->
                        TrackItemRow(track) {
                            onTrackSelected(C.TRACK_TYPE_TEXT, track.id.toInt())
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))




            }
        }
    }
    
}


@Composable
fun TrackItemRow(track: VideoTrackInfo, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = track.name)
        if (track.isSelected) {
            Icon(imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary)
        }
    }
}