package com.example.videoplayer.presentation.Utils

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.video.VideoFrameDecoder
import coil3.video.videoFrameMillis
import com.example.videoplayer.presentation.Utils.vControls.shareVideo
import com.example.videoplayer.presentation.navigation.NavigationItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    navController: NavController,
    onFileChanged:()-> Unit = {}
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        add(VideoFrameDecoder.Factory())
    }.build()

    var showBottomSheet = rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    var showRenameDialog = rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog = rememberSaveable { mutableStateOf(false) }
    var renameText = rememberSaveable { mutableStateOf(fileName) }

    val scope = rememberCoroutineScope()

    val intentSenderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {result ->
        if(result.resultCode == Activity.RESULT_OK){
            onFileChanged()
            Toast.makeText(context,"Permission Granted , please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    val permanentTextColor = Color.White
    val permanentSecondaryTextColor = Color(0xFFCCCCCC) // Light Gray
    val permanentSheetColor = Color(0xFF1E1E1E) // Dark Gray for Sheet/Dialogs
    val permanentErrorColor = Color(0xFFFF5252)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(110.dp)
            .shadow(
                elevation = 8.dp,
                shape = WavyFolderShape(),
                spotColor = Color.Black.copy(0.5f),
                ambientColor = Color.Black.copy(0.5f)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                ),
                shape = WavyFolderShape()
            )
            .clip(WavyFolderShape())
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF383838), // Lighter top
                        Color(0xFF2C2C2C)  // Darker bottom
                    )
                )
            )
            .padding(horizontal = 8.dp)
            .combinedClickable(
                onClick = {
                    navController.navigate(
                        NavigationItem.Video_player(
                            VideoUri = path,
                            title = fileName
                        )
                    )
                },
                onLongClick = {
                    showBottomSheet.value = true
                }
            ),
        contentAlignment = Alignment.Center

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(thumbnail)
                    .videoFrameMillis(1000).build(),
                contentDescription = "Video Thumbnail",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                imageLoader = imageLoader
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = fileName.takeIf { it.isNotBlank() }?:"Untitled",
                    style = MaterialTheme.typography.titleMedium,
                    color = permanentTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = irishGroverFamily

                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Duration : ${formatter(duration.toLongOrNull()?:0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = permanentSecondaryTextColor,
                    fontFamily = irishGroverFamily
                )

                Text(
                    text = "Size : ${formatFIleSize(size.toLongOrNull()?:0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = permanentSecondaryTextColor,
                    fontFamily = irishGroverFamily
                )

                Text(
                    text = "Added : ${formatDate(dateAdded.toLongOrNull()?:0)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = permanentSecondaryTextColor,
                    fontFamily = irishGroverFamily
                )



            }

        }




    }

    if(showBottomSheet.value){
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState,
            containerColor = permanentSheetColor,
            contentColor = permanentTextColor
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ){
                BottomSheetItem(
                    icon = Icons.Default.Share,
                    label = "Share",
                    contentColor = permanentTextColor,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                                //onShareClick(path) // Trigger share callback
                                shareVideo(context = context, videoLocation = path)
                            }
                        }
                    }
                )

                BottomSheetItem(
                    icon = Icons.Default.Edit,
                    label = "Rename",
                    contentColor = permanentTextColor,
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                                //onRenameClick(path) // Trigger rename callback
                                renameText.value = fileName
                                showRenameDialog.value = true
                            }
                        }
                    }
                )

                // Delete Option
                BottomSheetItem(
                    icon = Icons.Default.Delete,
                    label = "Delete",
                    contentColor = permanentErrorColor, // Red color for delete
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                                //onDeleteClick(path) // Trigger delete callback
                                showDeleteDialog.value = true
                            }
                        }
                    }
                )

            }
        }
    }

    if (showRenameDialog.value) {
        AlertDialog(
            onDismissRequest = { showRenameDialog.value = false },
            containerColor = permanentSheetColor, // FIXED: Dark background
            titleContentColor = permanentTextColor,
            textContentColor = permanentSecondaryTextColor,
            title = { Text("Rename Video") },
            text = {
                OutlinedTextField(
                    value = renameText.value,
                    onValueChange = { renameText.value = it },
                    label = { Text("New Name", color = permanentSecondaryTextColor) },
                    singleLine = true,
                    textStyle = TextStyle(color = permanentTextColor), // Input text color
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = permanentTextColor,
                        unfocusedBorderColor = permanentSecondaryTextColor,
                        cursorColor = permanentTextColor
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showRenameDialog.value = false
                    // Perform Rename
                    val intentSender = MediaModificationHelper.renameVideo(context, id, renameText.value)
                    if (intentSender != null) {
                        // Permission needed: Launch system dialog
                        intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                    } else {
                        // Success
                        onFileChanged()
                        Toast.makeText(context, "Renamed successfully", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Rename", color = permanentTextColor) }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog.value = false }) { Text("Cancel", color = permanentSecondaryTextColor) }
            }
        )
    }

    // --- DELETE DIALOG ---
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            containerColor = permanentSheetColor, // FIXED
            titleContentColor = permanentTextColor,
            textContentColor = permanentSecondaryTextColor,
            title = { Text("Delete Video") },
            text = { Text("Are you sure you want to delete '$title'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog.value = false
                        // Perform Delete
                        val intentSender = MediaModificationHelper.deleteVideo(context, id)
                        if (intentSender != null) {
                            // Permission needed: Launch system dialog
                            intentSenderLauncher.launch(
                                IntentSenderRequest.Builder(intentSender).build()
                            )
                        } else {
                            // Success
                            onFileChanged()
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete", color = permanentErrorColor) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) { Text("Cancel", color = permanentSecondaryTextColor) }
            }
        )
    }

}

@Composable
fun BottomSheetItem(
    icon: ImageVector,
    label: String,
    contentColor: Color,
    onClick:()-> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable{
                onClick()
            }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor,
            fontFamily = irishGroverFamily // Kept your custom font
        )

    }

}