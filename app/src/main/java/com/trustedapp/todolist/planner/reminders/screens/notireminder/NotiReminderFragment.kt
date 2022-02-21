package com.trustedapp.todolist.planner.reminders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNewTaskBinding
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.utils.hide

class NotiReminderFragment : BaseFragment<FragmentNotiReminderBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotiReminderBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() = with(viewBinding){
        setupToolbar()
        setupEvents()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = "Noti & reminder"
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { activity?.finish() }
    }
}