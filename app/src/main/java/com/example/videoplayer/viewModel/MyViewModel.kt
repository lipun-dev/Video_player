package com.example.videoplayer.viewModel

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.videoplayer.data.videoModel
import com.example.videoplayer.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MyViewModel @OptIn(UnstableApi::class)
@Inject constructor(val repo: Repo,val application: Application): ViewModel() {

    private val instanceId = UUID.randomUUID().toString()
    init {
        //9cf214fe-592e-469b-8cd6-56ff3df49a6e  9cf214fe-592e-469b-8cd6-56ff3df49a6e
        Log.d("MyViewModel", "Step1: Created ViewModel instance ID = $instanceId")
    }
    // Expose for debugging
    fun getInstanceId() = instanceId




    private val _videoList = MutableStateFlow(emptyList<videoModel>())
    val videoList = _videoList.asStateFlow()

    private val _FolderList = MutableStateFlow(emptyMap<String, List<videoModel>>())
    val FolderList = _FolderList.asStateFlow()

    private val _showUi = MutableStateFlow(false)
    val showUi: StateFlow<Boolean> = _showUi.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    @OptIn(UnstableApi::class)
    fun LoadAllvideos(){
        viewModelScope.launch {
            _isLoading.value = true
            repo.getAllVideos(application).collect {
                _videoList.value = it

            }

        }

        _isLoading.value = false


    }

    @OptIn(UnstableApi::class)
    fun LoadFolderVideos(){
        viewModelScope.launch {
            _isLoading.value = true
            repo.getFolders(application).collect {
                Log.d("MyViewModel", "Step1: Emitted folders, size = ${it.size}, keys = ${it.keys.joinToString()}")
                _FolderList.value = it
            }
        }
        _isLoading.value = false

    }

    fun setPermissionGranted(granted: Boolean) {
        _showUi.value = granted
        if (granted) {
            LoadAllvideos()
            LoadFolderVideos()
        } else {
            _videoList.value = emptyList()
            _FolderList.value = emptyMap()
        }
    }


}