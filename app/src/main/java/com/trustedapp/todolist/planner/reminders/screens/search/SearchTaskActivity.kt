package com.trustedapp.todolist.planner.reminders.screens.search

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.ads.control.ads.Admod
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivitySearchTaskBinding
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.Constants
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class SearchTaskActivity : BaseActivity<ActivitySearchTaskBinding>() {

    override fun inflateViewBinding() = ActivitySearchTaskBinding.inflate(layoutInflater)

    private val viewModel: SearchTaskViewModel by viewModels()

    @Inject
    lateinit var searchAdapter: SearchTaskAdapter

    @Inject
    lateinit var recentAdapter: SearchRecentAdapter

    override fun onActivityReady(savedInstanceState: Bundle?) {
    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    private fun initViews() = with(viewBinding) {
        recyclerTasks.adapter = searchAdapter
        recyclerRecent.adapter = recentAdapter
        editSearch.requestFocus()
        // Load Banner ads
        Admod.getInstance().loadBanner(this@SearchTaskActivity, getString(R.string.banner_ads_id))
    }

    private fun initData() {
        viewModel.getRecentSearch(this)
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tasks.collect {
                    searchAdapter.submitList(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                recentSearch.collect {
                    recentAdapter.submitList(it)
                    if (it.isEmpty()) viewBinding.layoutRecent.gone() else viewBinding.layoutRecent.show()
                }
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        editSearch.addTextChangedListener {
            val inputText = it.toString().trim()
            viewModel.search(inputText)
            if (inputText.isEmpty()) {
                viewBinding.layoutRecent.show()
            } else {
                viewBinding.layoutRecent.gone()
            }
        }
        recyclerTasks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                hideKeyboard()
            }
        })
        buttonBack.setOnClickListener {
            finish()
        }
        searchAdapter.taskSelectListener = { id, name ->
            viewModel.addRecentSearch(this@SearchTaskActivity, name)
            startActivity(Intent(this@SearchTaskActivity, TaskDetailActivity::class.java).apply {
                putExtra(Constants.KEY_TASK_ID, id)
            })
            finish()
        }
        recentAdapter.recentSelectListener = {
            editSearch.setText(it)
            editSearch.setSelection(it.length)
        }
    }
}
