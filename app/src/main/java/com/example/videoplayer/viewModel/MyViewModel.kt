package com.example.videoplayer.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.videoplayer.data.ActionType
import com.example.videoplayer.data.VideoTrackInfo
import com.example.videoplayer.data.playerActions
import com.example.videoplayer.data.videoModel
import com.example.videoplayer.dataStore.prefDataStore
import com.example.videoplayer.repo.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID


@OptIn(UnstableApi::class)
class MyViewModel(val repo: Repo,val application: Application,private val prefs: prefDataStore): ViewModel() {

    private val instanceId = UUID.randomUUID().toString()

    // Expose for debugging
    fun getInstanceId() = instanceId

    val isPermissionGranted: StateFlow<Boolean?> =
        prefs.isPermissionGranted()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)






    private val _videoList = MutableStateFlow(emptyList<videoModel>())
    val videoList = _videoList.asStateFlow()

    private val _FolderList = MutableStateFlow(emptyMap<String, List<videoModel>>())
    val FolderList = _FolderList.asStateFlow()

    private val _showUi = MutableStateFlow(false)
    val showUi: StateFlow<Boolean> = _showUi.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _playerState = MutableStateFlow<ExoPlayer?>(null)
    val playerState = _playerState.asStateFlow()

    private val _audioTracks = MutableStateFlow<List<VideoTrackInfo>>(emptyList())
    val audioTracks = _audioTracks.asStateFlow()

    private val _subtitleTracks = MutableStateFlow<List<VideoTrackInfo>>(emptyList())
    val subtitleTracks = _subtitleTracks.asStateFlow()

    fun createPlayerWithMediaItems(context: Context,uri: String){
        if(_playerState.value == null){
            val mediaItem = MediaItem.fromUri(uri.toUri())

            _playerState.update {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(mediaItem)
                    prepare()
                    playWhenReady = true
                }
            }
        }
    }

    init {
        observePermission()
    }

    private fun observePermission() {
        // 🔹 Auto-load if permission already granted
        viewModelScope.launch {
            isPermissionGranted
                .collect { granted ->
                if (granted == true) {
                    LoadAllvideos()
                    LoadFolderVideos()
                } else {
                    _videoList.value = emptyList()
                    _FolderList.value = emptyMap()
                }
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun LoadAllvideos(){
        viewModelScope.launch {
            _isLoading.value = true
            repo.getAllVideos(application).collect {
                _videoList.value = it

            }
            _isLoading.value = false
        }




    }

    @OptIn(UnstableApi::class)
    fun LoadFolderVideos(){
        viewModelScope.launch {
            _isLoading.value = true
            repo.getFolders(application).collect {
                Log.d("MyViewModel", "Step1: Emitted folders, size = ${it.size}, keys = ${it.keys.joinToString()}")
                _FolderList.value = it
            }
            _isLoading.value = false
        }


    }

    fun setPermissionGranted(granted: Boolean) {
       viewModelScope.launch {
           _showUi.value = granted
           prefs.setPermissionGranted(granted)
       }
    }

    fun executeAction(playerAction: playerActions) {
        when(playerAction.actionType) {
            ActionType.PLAY -> _playerState.value?.play()
            ActionType.PAUSE -> _playerState.value?.pause()
            ActionType.REWIND -> _playerState.value?.rewind()
            ActionType.FORWARD -> _playerState.value?.forward()
            ActionType.NEXT -> _playerState.value?.playNext()
            ActionType.PREVIOUS -> _playerState.value?.playPrevious()
        }
    }
    fun releasePlayer(){
        _playerState.value?.release()
        _playerState.value = null
    }

    private fun ExoPlayer.rewind() {
        val newPosition = (currentPosition - 10_000).coerceAtLeast(0)
        seekTo(newPosition)
    }


    private fun ExoPlayer.forward() {
        val newPosition = (currentPosition + 10_000)
            .coerceAtMost(duration)
        seekTo(newPosition)
    }

    private fun ExoPlayer.playNext() {
        if (hasNextMediaItem()) {
            val nextIndex = currentMediaItemIndex + 1
            val mediaItemId = getMediaItemAt(nextIndex)
            seekTo(nextIndex, 0)
        }
    }

    private fun ExoPlayer.playPrevious() {
        if (
            isCommandAvailable(Player.COMMAND_SEEK_TO_MEDIA_ITEM) &&
            hasPreviousMediaItem()
        ) {
            val previousIndex = currentMediaItemIndex - 1
            val mediaItemId = getMediaItemAt(previousIndex)
            seekTo(previousIndex, 0)
        }
    }


    fun extractTracks(player: Player) {
        val groups = player.currentTracks.groups
        val audioList = mutableListOf<VideoTrackInfo>()
        val subtitleList = mutableListOf<VideoTrackInfo>()

        for (i in 0 until groups.size) {
            val group = groups[i]
            val type = group.type

            if (type == C.TRACK_TYPE_AUDIO || type == C.TRACK_TYPE_TEXT) {
                for (j in 0 until group.length) {
                    val isSelected = group.isTrackSelected(j)
                    val format = group.getTrackFormat(j)

                    // Create a readable name (e.g., "English", "Stereo")
                    val trackName = "${format.language ?: "Undetermined"} ${format.label ?: ""}".trim()

                    val trackInfo = VideoTrackInfo(
                        id = i.toString(), // We use group index as ID for simplicity here
                        name = trackName.ifEmpty { "Track ${j + 1}" },
                        isSelected = isSelected,
                        trackType = type
                    )

                    if (type == C.TRACK_TYPE_AUDIO) audioList.add(trackInfo)
                    else subtitleList.add(trackInfo)
                }
            }
        }
        _audioTracks.value = audioList
        _subtitleTracks.value = subtitleList
    }

    fun selectTrack(player: Player, trackType: Int, groupIndex: Int) {
        // Get current tracks
        val groups = player.currentTracks.groups
        if (groupIndex < 0 || groupIndex >= groups.size) return

        val trackGroup = groups[groupIndex].mediaTrackGroup

        // Override the track selection to the specific group
        val override = TrackSelectionOverride(trackGroup, 0) // Select first track in group

        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setOverrideForType(override)
            .setTrackTypeDisabled(trackType, false) // Ensure type is enabled
            .build()

        // Refresh list to update UI selection state
        extractTracks(player)
    }

    fun disableTrack(player: Player, trackType: Int) {
        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setTrackTypeDisabled(trackType, true)
            .build()

        extractTracks(player)
    }




}