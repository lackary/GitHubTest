package com.lacklab.app.githubtest.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lacklab.app.githubtest.R
import com.lacklab.app.githubtest.databinding.ItemNetworkStateBinding
import timber.log.Timber

class PagingLoadStateAdapter<T:Any, VH: RecyclerView.ViewHolder>(
    private val adapter: PagingDataAdapter<T, VH>
) : LoadStateAdapter<PagingLoadStateAdapter.NetworkStateViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkStateViewHolder {
        val binding = ItemNetworkStateBinding.bind(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_network_state, parent, false)
        )
        return NetworkStateViewHolder(binding) { adapter.retry() }
    }

    override fun onBindViewHolder(holder: NetworkStateViewHolder, loadState: LoadState) {
        holder.binding(loadState)
    }

    class NetworkStateViewHolder(
        private val binding: ItemNetworkStateBinding,
        private val retryClickCallback: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                buttonRetry.setOnClickListener { retryClickCallback() }
            }
        }

        fun binding(loadState: LoadState) {
            with(binding) {
                Timber.d("binding $loadState")
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState is LoadState.Error
//                textViewErrorMessage.isVisible =
//                    !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
//                textViewErrorMessage.text = (loadState as? LoadState.Error)?.error?.message
            }
        }
    }

}