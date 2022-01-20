package com.example.todo.screens.home

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todo.R
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        bottomBar.setupWithNavController(findNavController(R.id.home_nav_host_fragment))
    }

    private fun toTasks() {

    }

    private fun toCalendar() {

    }

    private fun toMine() {

    }
}
