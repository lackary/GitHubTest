package com.lacklab.app.githubtest.ui.view.search

import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import com.google.android.material.textfield.TextInputEditText
import com.lacklab.app.githubtest.R
import com.lacklab.app.githubtest.base.BaseFragment
import com.lacklab.app.githubtest.databinding.FragmentSearchBinding
import com.lacklab.app.githubtest.ui.view.adapter.PagingLoadStateAdapter
import com.lacklab.app.githubtest.ui.view.adapter.UserPagingAdapter
import com.lacklab.app.githubtest.ui.viewmodel.SearchViewModel
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
                // init recycle view
                recycleViewUser.adapter = withLoadStateFooter(
                    footer = PagingLoadStateAdapter(this)
                )
                launchOnLifecycleScope {
                    loadStateFlow.collectLatest { it ->
                        swipeRefresh.isRefreshing = it.refresh is LoadState.Loading

                        // If we have an error, show a toast
                        val errorState = when {
                            it.append is LoadState.Error -> it.append as LoadState.Error
                            it.prepend is LoadState.Error -> it.prepend as LoadState.Error
                            it.refresh is LoadState.Error -> it.refresh as LoadState.Error
                            else -> null
                        }
                        errorState?.let {
                            showToastMessage(it.error.message.toString())
                        }
                    }
                }
            }

            // hold input by keyboard
            textEditSearch.setOnKeyListener { v, keyCode, event ->
                // process the event that click
                if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val query = textEditSearch.text.toString()
                    Timber.d("query: $query")
                    searchUser(query, viewModel)
                    hideKeyboard(textEditSearch)
                   true
                } else {
                   false
                }

            }
        }
    }

    private fun searchUser(query: String,
                           viewModel: SearchViewModel) {
        viewModel.searchUsers(query)
        launchOnLifecycleScope {
            viewModel.usersFlow.collectLatest {
                userPagingAdapter.submitData(it)
            }
        }
    }

    private fun hideKeyboard(textInputEditText: TextInputEditText) {
        val inputMethManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
        inputMethManager.hideSoftInputFromWindow(textInputEditText.windowToken, 0)
    }

}