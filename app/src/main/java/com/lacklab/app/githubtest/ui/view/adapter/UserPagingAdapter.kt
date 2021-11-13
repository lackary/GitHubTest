package com.lacklab.app.githubtest.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lacklab.app.githubtest.R
import com.lacklab.app.githubtest.data.model.GitHubUser
import com.lacklab.app.githubtest.databinding.ItemUserBinding
import javax.inject.Inject

class UserPagingAdapter @Inject constructor()
    : PagingDataAdapter<GitHubUser, UserPagingAdapter.UserViewHolder>(UserDiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = DataBindingUtil.inflate<ItemUserBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_user,
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }


    class UserViewHolder(private val binding: ItemUserBinding)
        : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                // clickListener
            }
        }

        fun bind(item: GitHubUser) {
            with(binding) {
                user = item

                Glide.with(root)
                    .load(item.avatarUrl)
                    .circleCrop()
                    .override(200, 200)
                    .into(imageViewAvatar)
            }
        }

    }
}

private object UserDiffCallback : DiffUtil.ItemCallback<GitHubUser>(){
    override fun areItemsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GitHubUser, newItem: GitHubUser): Boolean {
        return oldItem == newItem
    }

}