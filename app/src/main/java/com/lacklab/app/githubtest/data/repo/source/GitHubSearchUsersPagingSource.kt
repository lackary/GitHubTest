package com.lacklab.app.githubtest.data.repo.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lacklab.app.githubtest.data.model.GitHubUser
import com.lacklab.app.githubtest.data.model.GitHubUsers
import com.lacklab.app.githubtest.data.remote.ApiEmptyResponse
import com.lacklab.app.githubtest.data.remote.ApiErrorResponse
import com.lacklab.app.githubtest.data.remote.ApiSuccessResponse
import com.lacklab.app.githubtest.data.remote.api.GithubApi
import com.lacklab.app.githubtest.utils.GITHUB_STARTING_PAGE_INDEX
import com.lacklab.app.githubtest.utils.NETWORK_PAGE_SIZE
import timber.log.Timber

class GitHubSearchUsersPagingSource (
    private val api: GithubApi,
    private val query: String
) : PagingSource<Int, GitHubUser>() {
    override fun getRefreshKey(state: PagingState<Int, GitHubUser>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GitHubUser> {
        val page = params.key ?: GITHUB_STARTING_PAGE_INDEX
        return try {
            val response = api.searchUser(query, page, params.loadSize)
            var data: GitHubUsers? = null
            when(response) {
                is ApiSuccessResponse -> {
                    data = response.body
                    data.totalPages = response.totalPages

                }
                is ApiErrorResponse -> {
                    throw Exception(response.errorMessage)
                }
                is ApiEmptyResponse-> {
                    data = GitHubUsers(
                        totalCount = 0,
                        incompleteResults = false,
                        items = emptyList(),
                        totalPages = 0
                    )
                }
            }
            val nextPage = page + (params.loadSize / NETWORK_PAGE_SIZE)
            LoadResult.Page(
                data = data.items,
                prevKey = null,
                nextKey = if (page == data.totalPages) null else nextPage
            )
        } catch (ex: Exception) {
            LoadResult.Error(ex)
        }
    }
}