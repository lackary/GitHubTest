package com.lacklab.app.githubtest.di

import com.lacklab.app.githubtest.data.remote.api.GithubApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideGitHubService(): GithubApi {
        return GithubApi.create()
    }
}