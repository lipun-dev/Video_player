package com.example.videoplayer.di

import android.content.Context
import com.example.videoplayer.dataStore.prefDataStore
import com.example.videoplayer.domain.RepoImpal
import com.example.videoplayer.repo.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object HiltModule {

    @Provides
    @Singleton
    fun ProvideRepo():Repo{
        return RepoImpal()

    }

    @Provides
    @Singleton
    fun providePref(@ApplicationContext context: Context): prefDataStore{

        return prefDataStore(context)

    }
}