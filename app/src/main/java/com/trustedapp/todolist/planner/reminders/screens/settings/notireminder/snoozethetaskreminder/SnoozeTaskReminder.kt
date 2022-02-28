package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.snoozethetaskreminder

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSnoozeTaskReminderBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.trustedapp.todolist.planner.reminders.utils.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SnoozeTaskReminder : BaseFragment<FragmentSnoozeTaskReminderBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSnoozeTaskReminderBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setEvents()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        setupToolbar()

    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.snooze_the_task_reminder)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.gone()
        button3.gone()
    }

    private fun setEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { findNavController().popBackStack() }
        swSnoozeTaskReminder.setOnClickListener {
            context?.let { viewModel.setIsSnoozeTaskReminder(it, swSnoozeTaskReminder.isChecked) }
        }
        lnSnoozeAfter.setOnClickListener { findNavController().navigate(R.id.toSnoozeAfterDialog) }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                isSnoozeTaskReminder.collect {
                    viewBinding.apply {
                        lnSnoozeAfter.isUserInteractionEnabled(it)
                        lnSnoozeAfter.alpha = if (it) 1.0f else 0.5f
                        swSnoozeTaskReminder.isChecked = it
                    }

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                snoozeAfter.collect {
                    if (it != null) {
                        viewBinding.apply {
                            tvSnoozeAfterValue.text = getString(it.nameResId)
                        }
                    }
                }
            }
        }
    }
}