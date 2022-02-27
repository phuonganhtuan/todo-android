package com.trustedapp.todolist.planner.reminders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotiReminderBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.DefaultReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NotiReminderFragment : BaseFragment<FragmentNotiReminderBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotiReminderBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvents()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        setupToolbar()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.noti_and_reminder_title)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun initData() = with(viewModel) {
        context?.let { activity?.let { it1 -> setupData(it, it1) } }
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { activity?.finish() }

        lnNotiHelp.setOnClickListener { findNavController().navigate(R.id.toNotificationHelp) }
        lnDefaultTaskReminderType.setOnClickListener { findNavController().navigate(R.id.toDefaultReminderType) }
        lnDefaultNotiRington.setOnClickListener {
            findNavController().navigate(
                R.id.toDefautlNotificationRingtone,
                bundleOf("type" to DefaultReminderTypeEnum.NOTIFICATION.name)
            )
        }
        lnDefaultAlarmRington.setOnClickListener {
            findNavController().navigate(
                R.id.toDefautlNotificationRingtone,
                bundleOf("type" to DefaultReminderTypeEnum.ALARM.name)
            )
        }
        swScreenlockTaskReminder.setOnCheckedChangeListener { _, isChecked -> viewModel.setIsScreenlock(isChecked) }
        swAddTaskFromNotificationBar.setOnCheckedChangeListener { _, isChecked -> viewModel.setIsAddTaskFromNotiBar(isChecked) }
        lnSnoozeTaskReminder.setOnClickListener { findNavController().navigate(R.id.toSnoozeTaskReminder) }
        swTodoReminder.setOnCheckedChangeListener { _, isChecked -> viewModel.setIsTodoReminder(isChecked) }
        lnDailyReminderRingtone.setOnClickListener { findNavController().navigate(R.id.toDailyReminderRingtone) }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                defaultReminderType.collect {
                    viewBinding.apply {
                        tvDefaultReminderTypeValue.text = when(it){
                            DefaultReminderTypeEnum.ALARM -> getString(R.string.alarm)
                            else -> getString(R.string.notification)
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectNotificationRingtone.collect {
                    viewBinding.apply {
                        tvDefaultNotiRingtonValue.text = it?.name
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectAlarmRingtone.collect {
                    viewBinding.apply {
                        tvDefaultAlarmRingtonValue.text = it?.name
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
               isScreenLockTaskReminder.collect {
                    viewBinding.apply {
                        tvScreenlockTaskReminderValue.text = if (it) getString(R.string.on) else getString(R.string.off)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isAddTaskFromNotificationBar.collect {
                    viewBinding.apply {
                        tvAddTaskFromNotificationBarValue.text = if (it) getString(R.string.on) else getString(R.string.off)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                snoozeAfter.collect {
                    viewBinding.apply {
                        if (it != null) {
                            tvSnoozeTaskReminderValue.text = getString(it.nameResId)
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isTodoReminder.collect {
                    viewBinding.apply {
                        tvTodoReminderrValue.text = if (it) getString(R.string.on) else getString(R.string.off)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectDailyRingtone.collect {
                    viewBinding.apply {
                        if (it != null) {
                            tvDailyReminderRingtoneValue.text = it.name
                        }
                    }
                }
            }
        }
    }
}