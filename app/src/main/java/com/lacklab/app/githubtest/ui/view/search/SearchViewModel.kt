package com.lacklab.app.githubtest.ui.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val keyword: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
    val reposCondition: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val followerCondition: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    fun searchUsers(query: String) {
        _usersFlow = repository.searchUser(query)
    }
}