package com.lacklab.app.githubtest.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lacklab.app.githubtest.data.remote.api.GithubApi
import com.lacklab.app.githubtest.data.model.GitHubUser
import com.lacklab.app.githubtest.data.repo.source.GitHubSearchUsersPagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GitHubRepository @Inject constructor(
    private val api: GithubApi
) {
    fun searchUsers(query: String, order: String?): Flow<PagingData<GitHubUser>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = true),
            pagingSourceFactory = {
                GitHubSearchUsersPagingSource(api = api, query = query, order = order)
            }
        ).flow
    }
}