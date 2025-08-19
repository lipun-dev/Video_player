package com.example.videoplayer.repo

import android.app.Application
import com.example.videoplayer.data.videoModel
import kotlinx.coroutines.flow.Flow

interface Repo {


    suspend fun getAllVideos(application: Application): Flow<ArrayList<videoModel>>

    suspend fun getFolders(application: Application): Flow<Map<String, List<videoModel>>>






}