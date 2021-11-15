package com.lacklab.app.githubtest.ui.view.search

import android.view.KeyEvent
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lacklab.app.githubtest.R
import com.lacklab.app.githubtest.base.BaseFragment
import com.lacklab.app.githubtest.databinding.FragmentSearchBinding
import com.lacklab.app.githubtest.ui.view.adapter.PagingLoadStateAdapter
import com.lacklab.app.githubtest.ui.view.adapter.UserPagingAdapter
import com.lacklab.app.githubtest.utils.ui.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding, SearchViewModel>() {

    private val searchViewModel: SearchViewModel by viewModels()
    @Inject
    lateinit var userPagingAdapter: UserPagingAdapter

    override val layoutId: Int
        get() = R.layout.fragment_search

    override fun getVM() = searchViewModel

    override fun bindVM(binding: FragmentSearchBinding, viewModel: SearchViewModel) {
        with(binding) {
            // handle PagingAdapter
            with(userPagingAdapter) {
                // set recycle view
                recycleViewUser.adapter = withLoadStateFooter(
                    footer = PagingLoadStateAdapter(this)
                )

                //set swipe refresh
                swipeRefresh.setOnRefreshListener { refresh() }

                // handle the loadStateFlow
                handleLoadState(binding, this)
            }

            // hold input by keyboard
            textEditSearch.setOnKeyListener { v, keyCode, event ->
                // process the event that click
                if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val query = textEditSearch.text.toString()
                    Timber.d("query: $query")
                    searchUser(query, viewModel)
                    hideKeyboard(requireContext(), textEditSearch)
                    true
                } else {
                    false
                }

            }

            handleBottomSheetEvent(this)
        }
    }

    private fun searchUser(query: String, viewModel: SearchViewModel) {
        viewModel.searchUsers(query)
        launchOnLifecycleScope {
            viewModel.usersFlow.collectLatest {
                userPagingAdapter.submitData(it)
            }
        }
    }

    private fun handleLoadState(
        binding: FragmentSearchBinding,
        pagingAdapter: UserPagingAdapter
    ) {
        with(binding) {
            with(pagingAdapter) {
                launchOnLifecycleScope {
                    loadStateFlow.collectLatest { it ->
                        swipeRefresh.isRefreshing = it.refresh is LoadState.Loading

                        val isItemEmpty =
                            it.refresh !is LoadState.Loading
                                    && userPagingAdapter.itemCount == 0
                        textViewNoResult.isVisible = isItemEmpty
                        recycleViewUser.isVisible = !isItemEmpty

                        // If we have an error, show a toast
                        val errorState = when {
                            it.append is LoadState.Error -> it.append as LoadState.Error
                            it.prepend is LoadState.Error -> it.prepend as LoadState.Error
                            it.refresh is LoadState.Error -> it.refresh as LoadState.Error
                            else -> null
                        }
                        errorState?.let {
                            if(pagingAdapter.itemCount == 0)
                                showToastMessage(it.error.message.toString())
                        }
                    }
                }
            }
        }
    }

    private fun handleBottomSheetEvent(binding: FragmentSearchBinding) {
        with(binding) {
            // set bottom sheet event
            val bottomSheetBehavior =
                BottomSheetBehavior.from(includedBottomSheetFilter.layoutBottomSheet)
            imageButtonFilter.setOnClickListener {
                if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }
}