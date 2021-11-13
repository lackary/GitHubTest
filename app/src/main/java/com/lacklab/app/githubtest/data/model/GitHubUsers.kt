package com.lacklab.app.githubtest.data.model

import androidx.paging.Pager
import com.google.gson.annotations.SerializedName

data class GitHubUsers(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    var totalPages: Int?,
    val items: List<GitHubUser>
)
