package com.lacklab.app.githubtest.ui.view.search

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

    val reposNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val followerCondition: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val followersNumber: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val order: MutableLiveData<String> by lazy {
        MutableLiveData<String>("desc")
    }

    /**
     * process the query and send to GitHub's API
     * */
    fun searchUsers() {
        val query =
            keyword.value +
            // check the condition and numbers of repos
            if (reposCondition.value.equals("")
                || reposNumber.value.equals("")) {
                ""
            } else {
                reposCondition.value + reposNumber.value
            } +
            // check the condition and numbers of followers
            if (followerCondition.value.equals("")
                || followersNumber.value.equals("")) {
                ""
            } else {
                followerCondition.value + followersNumber.value
            }
        _usersFlow = repository.searchUsers(query, order.value)
    }
}