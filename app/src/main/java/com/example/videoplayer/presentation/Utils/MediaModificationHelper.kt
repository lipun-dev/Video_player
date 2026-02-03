package com.example.videoplayer.presentation.Utils

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object MediaModificationHelper{
    @RequiresApi(Build.VERSION_CODES.Q)
    fun renameVideo(context: Context, videoId: String, newName: String): IntentSender?{
        val resolver = context.contentResolver
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoId.toLongOrNull()?:return null
        )
        val updatedValues = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME,newName)
        }
        return try {
            resolver.update(contentUri,updatedValues,null,null)
            null
        }catch (e: SecurityException){
            val recoverableSecurityException = e as? RecoverableSecurityException
            recoverableSecurityException?.userAction?.actionIntent?.intentSender
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun deleteVideo(context: Context, videoId: String): IntentSender?{
        val resolver = context.contentResolver
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoId.toLongOrNull()?:return null
        )
        return try {
            resolver.delete(contentUri,null,null)
            null
        }catch (e: SecurityException){
            val recoverableSecurityException = e as? RecoverableSecurityException
            recoverableSecurityException?.userAction?.actionIntent?.intentSender
        }
    }
}