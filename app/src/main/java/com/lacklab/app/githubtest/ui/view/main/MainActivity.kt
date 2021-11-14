package com.lacklab.app.githubtest.ui.view.main

import android.os.Bundle
import androidx.activity.viewModels
import com.lacklab.app.githubtest.R
import com.lacklab.app.githubtest.base.BaseActivity
import com.lacklab.app.githubtest.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getVM() = mainViewModel

    override fun bindVM(binding: ActivityMainBinding, viewModel: MainViewModel) = Unit

}