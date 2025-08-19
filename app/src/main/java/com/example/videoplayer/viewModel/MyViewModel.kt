package com.example.videoplayer.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.data.videoModel
import com.example.videoplayer.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(val repo: Repo,val application: Application): ViewModel() {



    val showUi = MutableStateFlow(false)
    val videoList = MutableStateFlow(emptyList<videoModel>())
    val FolderList = MutableStateFlow(emptyMap<String, List<videoModel>>())
    val isLoading = MutableStateFlow(false)

    fun LoadAllvideos(){
        isLoading.value = true
        viewModelScope.launch {
            repo.getAllVideos(application).collect {
                videoList.value = it
            }
        }
        isLoading.value = false
    }

    fun LoadFolderVideos(){
        isLoading.value = true
        viewModelScope.launch {
            repo.getFolders(application).collect {
                FolderList.value = it
            }
        }
        isLoading.value = false
    }

    init {
        viewModelScope.launch {
            LoadAllvideos()
            LoadFolderVideos()
        }
    }


}