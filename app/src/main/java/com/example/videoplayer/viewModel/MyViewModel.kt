package com.example.videoplayer.viewModel

import android.app.Application
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.videoplayer.data.videoModel
import com.example.videoplayer.dataStore.prefDataStore
import com.example.videoplayer.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MyViewModel @OptIn(UnstableApi::class)
@Inject constructor(val repo: Repo,val application: Application,private val prefs: prefDataStore): ViewModel() {

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


}