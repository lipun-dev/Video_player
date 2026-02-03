package com.example.videoplayer.presentation.Utils.vControls


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.videoplayer.data.ActionType
import com.example.videoplayer.data.playerActions

@Composable
fun CentreControls(modifier: Modifier = Modifier,
                   isPlaying:Boolean,
                   playerActions:(playerActions)-> Unit) {


        Row(
            modifier = modifier.fillMaxWidth(),
            Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(onClick = {
                playerActions(playerActions(ActionType.PREVIOUS))
            }) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "play previous",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = {
                playerActions(playerActions(ActionType.REWIND))
            }) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    contentDescription = "Rewind 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = {
                playerActions(playerActions(if (isPlaying) ActionType.PAUSE else ActionType.PLAY))

            },
                modifier = Modifier.size(72.dp)) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = {
                playerActions(playerActions(ActionType.FORWARD))
            }) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    contentDescription = "Forward 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = {
                playerActions(playerActions(ActionType.NEXT))
            }) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

        }




}