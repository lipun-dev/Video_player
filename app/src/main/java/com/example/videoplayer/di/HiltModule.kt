package com.example.videoplayer.di

import com.example.videoplayer.domain.RepoImpal
import com.example.videoplayer.repo.Repo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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


}