package com.example.todo.screens.home

import android.os.Bundle
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {

    }
}
