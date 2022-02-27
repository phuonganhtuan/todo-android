package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaulttaskremindertype

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentDefaultReminderTypeDialogBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.DefaultReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DefaultReminderTypeDialog : BaseDialogFragment<FragmentDefaultReminderTypeDialogBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()
    private lateinit var selItem: DefaultReminderTypeEnum

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDefaultReminderTypeDialogBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvents()
        observeData()
    }

    private fun initView() {

    }

    private fun initData() {
        selItem = viewModel.defaultReminderType.value
        onChange()
    }

    private fun setupEvents() = with(viewBinding) {
        lnNotification.setOnClickListener {
            selItem = DefaultReminderTypeEnum.NOTIFICATION
            onChange()
        }
        lnAlarm.setOnClickListener {
            selItem = DefaultReminderTypeEnum.ALARM
            onChange()
        }

        btnDone.setOnClickListener {
            viewModel.setDefaultType(selItem)
            dismiss()
        }

        btnCancel.setOnClickListener { dismiss() }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                defaultReminderType.collect {
                    viewBinding.apply {
                        imgNotification.setImageResource(if (it == DefaultReminderTypeEnum.NOTIFICATION) R.drawable.ic_checked_radio else R.drawable.ic_uncheck_radio)
                        imgAlarm.setImageResource(if (it == DefaultReminderTypeEnum.ALARM) R.drawable.ic_checked_radio else R.drawable.ic_uncheck_radio)
                    }
                }
            }
        }
    }

    private fun onChange() = with(viewBinding) {
        imgNotification.setImageResource(if (selItem == DefaultReminderTypeEnum.NOTIFICATION) R.drawable.ic_checked_radio else R.drawable.ic_uncheck_radio)
        imgAlarm.setImageResource(if (selItem == DefaultReminderTypeEnum.ALARM) R.drawable.ic_checked_radio else R.drawable.ic_uncheck_radio)
    }
}