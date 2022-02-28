package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.snoozethetaskreminder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.data.models.model.SnoozeAfterModel
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSnoozeAfterDialogBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.listSnoozeAfter

class SnoozeAfterDialog : BaseDialogFragment<FragmentSnoozeAfterDialogBinding>() {

    private val viewModel: NotiReminderViewModel by activityViewModels()
    private var adapter: SnoozeAfterAdapter? = null


    private lateinit var selItem: SnoozeAfterModel

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSnoozeAfterDialogBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setEvents()
    }

    private fun initView() = with(viewBinding) {
        adapter = SnoozeAfterAdapter()
        recycleSnoozeAfter.adapter = adapter
        recycleSnoozeAfter.layoutManager = LinearLayoutManager(context)
    }

    private fun initData() = with(viewBinding) {
        adapter?.submitList(listSnoozeAfter)
        viewModel.snoozeAfter.value.let {
            selItem = viewModel.snoozeAfter.value!!
            adapter?.selectEntity = selItem
            adapter?.notifyDataSetChanged()
        }

    }

    private fun setEvents() = with(viewBinding) {
        btnDone.setOnClickListener {
            context?.let { it1 -> viewModel.setSnoozeAfter(it1, selItem) }
            dismiss()
        }
        adapter?.itemSelectListener = {
            selItem = it
            adapter?.selectEntity = selItem
            adapter?.notifyDataSetChanged()
        }
    }
}