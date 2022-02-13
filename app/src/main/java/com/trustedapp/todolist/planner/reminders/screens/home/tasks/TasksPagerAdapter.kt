package com.trustedapp.todolist.planner.reminders.screens.home.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import javax.inject.Inject

class TasksPagerAdapter @Inject constructor(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    var fragments = listOf<Fragment>()

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int) = fragments[position]
}
