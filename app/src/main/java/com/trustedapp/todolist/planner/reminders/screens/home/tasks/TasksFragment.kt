package com.trustedapp.todolist.planner.reminders.screens.home.tasks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.data.models.model.TaskPageType
import com.trustedapp.todolist.planner.reminders.databinding.FragmentTasksBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.home.tasks.page.TasksPageFragment
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskActivity
import com.trustedapp.todolist.planner.reminders.screens.search.SearchTaskActivity
import com.google.android.material.tabs.TabLayout
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksFragment : BaseFragment<FragmentTasksBinding>() {

    private val viewModel: TasksViewModel by viewModels()

    private var pagerAdapter: TasksPagerAdapter? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTasksBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        button1.setImageResource(R.drawable.ic_setting)
        button4.setImageResource(R.drawable.ic_search)
        button2.hide()
        button3.hide()
    }

    private fun initViews() = with(viewBinding) {
        pagerAdapter = TasksPagerAdapter(activity as FragmentActivity)
        viewBinding.homePager.adapter = pagerAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        pagerAdapter?.fragments = listOf(
            TasksPageFragment.newInstance(TaskPageType.TODAY),
            TasksPageFragment.newInstance(TaskPageType.FUTURE),
            TasksPageFragment.newInstance(TaskPageType.DONE)
        )
        pagerAdapter?.notifyDataSetChanged()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { toSetting() }
        layoutTop.button4.setOnClickListener { toSearch() }
        buttonNewTask.setOnClickListener { toNewTask() }
        homeTabBar.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                homePager.currentItem = homeTabBar.selectedTabPosition
            }
        })
        homePager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                homeTabBar.selectTab(homeTabBar.getTabAt(position))
            }
        })
    }

    private fun observeData() = with(viewModel) {

    }

    private fun toSetting() {
        (activity as? HomeActivity)?.openDrawer()
    }

    private fun toSearch() {
        startActivity(Intent(activity, SearchTaskActivity::class.java))
    }

    private fun toNewTask() {
        requireActivity().startActivity(Intent(activity, NewTaskActivity::class.java))
    }
}
