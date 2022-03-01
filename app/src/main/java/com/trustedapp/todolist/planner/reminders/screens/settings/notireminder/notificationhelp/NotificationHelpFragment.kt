package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.notificationhelp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNotificationHelpBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class NotificationHelpFragment : BaseFragment<FragmentNotificationHelpBinding>() {

    private val viewModel: NotiReminderViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotificationHelpBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvents()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        context?.let { it1 ->
            viewModel.setIsAllowNotification(
                it1, NotificationManagerCompat.from(it1).areNotificationsEnabled()
            )
        }

        context?.let { it1 ->
            val pm =
                it1?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
            viewModel.setIsIgnoreBattery(
                it1,
                pm.isIgnoringBatteryOptimizations(it1.packageName)
            )
        }
    }

    private fun initView() {
        setupToolbar()
    }

    private fun initData() {
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = resources.getString(R.string.notification_help)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { findNavController().popBackStack() }
        swAllowNotication.setOnClickListener {
            swAllowNotication.isChecked = !swAllowNotication.isChecked
            openNotificationSettingDetail()

        }
        swIgnoreBatterySaveMode.setOnClickListener {
            swIgnoreBatterySaveMode.isChecked = !swIgnoreBatterySaveMode.isChecked
            openIgnoreSaverModeSettingDetail()
        }
        swFloatingWindow.setOnClickListener { onChangeFloatWindow() }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isFloatingWindow.collect {
                    viewBinding.apply {
                        swFloatingWindow.isChecked = it
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isAllowNotification.collect {
                    viewBinding.apply {
                        swAllowNotication.isChecked = it
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isIgnoreBattery.collect {
                    viewBinding.apply {
                        swIgnoreBatterySaveMode.isChecked = it
                    }
                }
            }
        }
    }

    private fun onChangeFloatWindow() = with(viewBinding) {
        swFloatingWindow.isChecked = !swFloatingWindow.isChecked
        findNavController().navigate(R.id.toPermisDialog)
    }

    fun openNotificationSettingDetail() {
        val settingsIntent: Intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context?.packageName)
        startActivity(settingsIntent)
    }

    private fun openIgnoreSaverModeSettingDetail() {
        context.let {
            val intent = Intent()
            val pm = it?.applicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (pm.isIgnoringBatteryOptimizations(context?.packageName)) {
                intent.action =
                    Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            } else {
                intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.data = Uri.parse("package:${it?.packageName}")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }
}