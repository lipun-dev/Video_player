package com.example.videoplayer.domain

import android.app.Application
import android.content.ContentUris
import android.provider.MediaStore
import com.example.videoplayer.data.videoModel
import com.example.videoplayer.repo.Repo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.io.File

class RepoImpal: Repo {
    override suspend fun getAllVideos(application: Application): Flow<ArrayList<videoModel>> {
        val ListOfAllVideo = ArrayList<videoModel>()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DISPLAY_NAME,
        )

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val memoryCursor = application.contentResolver.query(uri,
            projection,null,null)

        if(memoryCursor != null){
            while (memoryCursor.moveToNext()){
                val id = memoryCursor.getString(0)
                val path = memoryCursor.getString(1)
                val title = memoryCursor.getString(2)
                val size = memoryCursor.getString(3)
                val dateAdded = memoryCursor.getString(4)
                val duration = memoryCursor.getString(5)
                val fileName = memoryCursor.getString(6)

                val conten_uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id.toLong()
                )


                val videFile = videoModel(
                    id = id,
                    path = path,
                    title = title,
                    size = size,
                    dateAdded = dateAdded,
                    duration = duration,
                    filename = fileName,
                    thumbnailUri = conten_uri.toString()

                )

                ListOfAllVideo.add(videFile)
                if(memoryCursor.isLast){
                    break
                }


            }
        }
        memoryCursor?.close()


        return flow {
            emit(ListOfAllVideo)

        }



    }

    override suspend fun getFolders(application: Application): Flow<Map<String, List<videoModel>>> {

        val allVideos = getAllVideos(application).first()
        val videoByFolder = allVideos.groupBy {
            File(it.path).parent?:"Unknown Folder"
        }

        return flow {
            emit(videoByFolder)
        }


    }
}