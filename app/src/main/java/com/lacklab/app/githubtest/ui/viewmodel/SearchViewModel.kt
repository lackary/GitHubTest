package com.lacklab.app.githubtest.ui.viewmodel

import androidx.paging.PagingData
import com.lacklab.app.githubtest.base.BaseViewModel
import com.lacklab.app.githubtest.data.model.GitHubUser
import com.lacklab.app.githubtest.data.repo.GitHubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: GitHubRepository
) : BaseViewModel() {
    private lateinit var _usersFlow: Flow<PagingData<GitHubUser>>
    val usersFlow: Flow<PagingData<GitHubUser>>
        get() = _usersFlow

    fun searchUsers(query: String) {
        _usersFlow = repository.searchUser(query)
    }
}