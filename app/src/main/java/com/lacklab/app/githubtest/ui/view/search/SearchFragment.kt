package com.lacklab.app.githubtest.ui.view.search

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lacklab.app.githubtest.R
import com.lacklab.app.githubtest.base.BaseFragment
import com.lacklab.app.githubtest.databinding.BottomSheetFilterBinding
import com.lacklab.app.githubtest.databinding.FragmentSearchBinding
import com.lacklab.app.githubtest.databinding.ItemFilterBinding
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
            with(viewModel) {
                textEditSearch.setOnKeyListener { v, keyCode, event ->
                    // process the event that click
                    if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        val query = textEditSearch.text.toString()
                        keyword.value = query
                        Timber.d("query: $query")
                        searchUsers(viewModel)
                        hideKeyboard(requireContext(), textEditSearch)
                        true
                    } else {
                        false
                    }
                }
            }
            // init bottom sheet and handle button filter
            initBottomSheetFilter(this, viewModel)
            // set event of button filter
            imageButtonFilter.setOnClickListener {
                setBottomSheetBehavior(this)
            }
        }
    }

    /**
     * handle the loadState of PagingDataAdapter
     * */
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
                        imageButtonFilter.isVisible = !isItemEmpty

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
        }
    }


    /**
     * init bottomSheet filter include set value and handle event
     * */
    private fun initBottomSheetFilter(binding:FragmentSearchBinding, viewModel: SearchViewModel) {
        with(binding) {
            with(includedBottomSheetFilter) {
                // set and handle repos
                setItemFilter(includedRepos, R.string.filter_repos)
                handleItemFilter(includedRepos, R.string.filter_repos, viewModel)
                // set and handle followers
                setItemFilter(includedFollowers, R.string.filter_follower)
                handleItemFilter(includedFollowers, R.string.filter_follower, viewModel)

                handleOrder(this, viewModel)
                
                materialButtonSubmit.setOnClickListener {
                    submitFilter(this, viewModel)
                    setBottomSheetBehavior(binding)
                }

                materialButtonInit.setOnClickListener {
                    defaultBottomSheet(binding, viewModel)
                }
            }
        }
    }

    /**
     * set the value of itemFilter (item_filter.xml)
     * */
    private fun setItemFilter(binding: ItemFilterBinding, id: Int) {
        with(binding) {
            textViewFilter.text = getString(id)
        }
    }

    /**
     * handle the event of ItemFilter (item_filter)
     * */
    private fun handleItemFilter(binding: ItemFilterBinding, id: Int, viewModel: SearchViewModel) {
        with(binding) {
            var condition: String? = null
            radioGroupCondition.setOnCheckedChangeListener { group, checkedId ->
                condition =
                    when(checkedId) {
                        R.id.radio_button_gt -> ":" + getString(R.string.gt)
                        R.id.radio_button_gt_eq -> ":" + getString(R.string.gt_eq)
                        R.id.radio_button_eq -> ":" + getString(R.string.eq)
                        R.id.radio_button_lt_eq -> ":" + getString(R.string.lt_eq)
                        R.id.radio_button_lt -> ":" + getString(R.string.lt)
                        else -> null
                    }
                setViewModelConditionValue(id, viewModel, condition)
            }
            textEditFilter.setOnKeyListener { v, keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                    number = textEditFilter.text.toString()
//                    setViewModelConditionValue(id, viewModel, condition, number)
                    hideKeyboard(requireContext(), textEditFilter)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun handleOrder(binding: BottomSheetFilterBinding, viewModel: SearchViewModel) {
        with(binding){
            radioGroupOrder.setOnCheckedChangeListener { group, checkedId ->
                val order = when(checkedId) {
                    R.id.radio_button_desc -> getString(R.string.desc)
                    R.id.radio_button_asc -> getString(R.string.asc)
                    else -> getString(R.string.desc)
                }
                viewModel.order.value = order
            }
        }
    }


    /**
     * set the behavior of bottom sheet
     * about expanding or collapsing
     * */
    private fun setBottomSheetBehavior(binding: FragmentSearchBinding) {
        with(binding) {
            val bottomSheetBehavior =
                BottomSheetBehavior.from(includedBottomSheetFilter.layoutBottomSheet)
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun collapseBottomSheet(binding: FragmentSearchBinding) {
        with(binding) {
            val bottomSheetBehavior =
                BottomSheetBehavior.from(includedBottomSheetFilter.layoutBottomSheet)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

    }

    /**
     * submit the value of filter
     * */
    private fun submitFilter(binding: BottomSheetFilterBinding, viewModel: SearchViewModel) {
        with(binding) {
            val reposNum = includedRepos.textEditFilter.text.toString()
            val followersNum = includedFollowers.textEditFilter.text.toString()
            with(viewModel) {
                reposNumber.value = reposNum
                followersNumber.value = followersNum
                searchUsers(viewModel)
            }
        }

    }

    /**
     * Clean the all of value in BottomSheet
     * */
    private fun defaultBottomSheet(binding: FragmentSearchBinding, viewModel: SearchViewModel) {
        with(binding) {
            with(includedBottomSheetFilter) {
                includedRepos.textEditFilter.text?.clear()
                includedRepos.radioGroupCondition.clearCheck()
                includedFollowers.textEditFilter.text?.clear()
                includedFollowers.radioGroupCondition.clearCheck()
                radioGroupOrder.check(R.id.radio_button_desc)
            }
        }
        with(viewModel) {
            reposCondition.value = ""
            followerCondition.value = ""
        }

    }

    /**
     * set the value of condition in Viewmodel
     * */
    private fun setViewModelConditionValue(
        id: Int,
        viewModel: SearchViewModel,
        condition: String?,
    ) {
        with(viewModel) {
            when(id) {
                R.string.filter_repos -> {
                    reposCondition.value =
                        "+" + getString(id) + condition
                }
                R.string.filter_follower -> {
                    followerCondition.value =
                        "+" + getString(id) + condition
                }
            }
        }
    }
    
    
    /**
     * call the api of GitHub
     * */
    private fun searchUsers(viewModel: SearchViewModel) {
        with(viewModel) {
            viewModel.searchUsers()
            launchOnLifecycleScope {
                usersFlow.collectLatest {
                    userPagingAdapter.submitData(it)
                }
            }
        }
    }
}