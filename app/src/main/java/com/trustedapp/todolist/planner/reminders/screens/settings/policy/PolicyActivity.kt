package com.trustedapp.todolist.planner.reminders.screens.settings.policy

import android.os.Bundle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityPolicyBinding
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PolicyActivity : BaseActivity<ActivityPolicyBinding>() {

    override fun inflateViewBinding() = ActivityPolicyBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button2.hide()
            button3.hide()
            button4.hide()
            button1.setBackgroundResource(R.drawable.ic_arrow_left)
            textTitle.text = getString(R.string.policy)
        }
    }

    private fun initData() = with(viewBinding.webView) {
        loadUrl(context.getString(R.string.url_policy))
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
    }
}