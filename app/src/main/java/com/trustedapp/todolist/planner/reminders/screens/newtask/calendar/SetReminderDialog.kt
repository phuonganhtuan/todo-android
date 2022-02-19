package com.trustedapp.todolist.planner.reminders.screens.newtask.calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSetReminderBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTimeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTypeEnum
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class SetReminderDialog : BaseDialogFragment<FragmentSetReminderBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selReminderItem: MenuItem? = null
    private var selReminderType: MenuItem? = null
    private var selReminderScreenLock: MenuItem? = null

    private var customReminderItem: MenuItem? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentSetReminderBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setEvents()
        observeData()
    }

    private fun initData() = with(viewBinding) {

    }

    private fun setEvents() = with(viewBinding) {
        tvReminderAtValue.setOnClickListener { showMenu(it, R.menu.reminder_menu) }

        tvReminderTypeValue.setOnClickListener { showMenu(it, R.menu.reminder_type_menu) }

        tvScreenLockValue.setOnClickListener { showMenu(it, R.menu.screen_lock_menu) }

        btnCancel.setOnClickListener { onClickCancel(it) }
        btnDone.setOnClickListener { onClickDone(it) }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedReminderTime.collect {
                    if (it.getStringid() == -1) return@collect
                    viewBinding.tvReminderAtValue.text = resources.getString(it.getStringid())

                    val popup = PopupMenu(requireContext(), viewBinding.tvReminderAtValue)
                    popup.menuInflater.inflate(R.menu.reminder_menu, popup.menu)
                    selReminderItem = popup.menu.findItem(it.getItemMenuId())
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedReminderType.collect {
                    if (it.getStringid() == -1) return@collect
                    viewBinding.tvReminderTypeValue.text = resources.getString(it.getStringid())

                    val popup = PopupMenu(requireContext(), viewBinding.tvReminderTypeValue)
                    popup.menuInflater.inflate(R.menu.reminder_type_menu, popup.menu)
                    selReminderType = popup.menu.findItem(it.getItemMenuId())
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedReminderScreenLock.collect {
                    val reminderScreenLockTxt =
                        if (it) resources.getString(R.string.on) else resources.getString(R.string.off)
                    viewBinding.tvScreenLockValue.text = reminderScreenLockTxt

                    val popup = PopupMenu(requireContext(), viewBinding.tvScreenLockValue)
                    popup.menuInflater.inflate(R.menu.screen_lock_menu, popup.menu)
                    val optionId = if (it) R.id.option_on else R.id.option_off
                    selReminderScreenLock = popup.menu.findItem(optionId)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                customReminderTime.filter { it > 0 }
                    .combine(customReminderTimeUnit) { time, unit ->
                      if  (time > 0) "${time.toString()} ${resources.getString(unit.getStringid()).lowercase()} ${resources.getString(R.string.before).lowercase()}" else ""
                    }.filter { it.isNotEmpty() }.collect {
                        val popup = PopupMenu(requireContext(), viewBinding.tvReminderAtValue)
                        popup.menuInflater.inflate(R.menu.reminder_menu, popup.menu)
                        customReminderItem = popup.menu.findItem(R.id.option_set_reminder_time)
                        customReminderItem?.title = it
                        if (selReminderItem?.itemId == customReminderItem?.itemId){
                            viewBinding.apply {
                                tvReminderAtValue.text = customReminderItem?.title
                            }
                        }
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isSelectCustomReminderTime.filter { it }.collect {
                    if (customReminderItem != null){
                        selReminderItem = customReminderItem
                        viewBinding.apply {
                            tvReminderAtValue.text = customReminderItem?.title
                        }
                        setIsSelectCustomReminderTime(false)
                    }
                }
            }
        }
    }

    private fun onClickCancel(view: View) {
        dismiss()
    }

    private fun onClickDone(view: View) {
        viewModel.onCheckChangeReminder(true)

        when (selReminderItem?.itemId) {
            R.id.option_same_with_due_date -> viewModel.selectReminderAt(ReminderTimeEnum.SAME_DUE_DATE)
            R.id.option_5_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.FIVE_MINUTES_BEFORE)
            R.id.option_10_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.TEN_MINUTES_BEFORE)
            R.id.option_15_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.FIFTEEN_MINUTES_BEFORE)
            R.id.option_30_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.THIRTY_MINUTES_BEFORE)
            R.id.option_1_day_before -> viewModel.selectReminderAt(ReminderTimeEnum.ONE_DAY_BEFORE)
            R.id.option_2_day_before -> viewModel.selectReminderAt(ReminderTimeEnum.TWO_DAYS_BEFORE)
            R.id.option_set_reminder_time -> viewModel.selectReminderAt(ReminderTimeEnum.CUSTOM_DAY_BEFORE)
        }

        when (selReminderType?.itemId) {
            R.id.option_notification -> viewModel.selectReminderType(ReminderTypeEnum.NOTIFICATION)
            R.id.option_alarm -> viewModel.selectReminderType(ReminderTypeEnum.ALARM)
        }

        when (selReminderScreenLock?.itemId) {
            R.id.option_off -> viewModel.selectReminderScreenlock(false)
            R.id.option_on -> viewModel.selectReminderScreenlock(true)
        }
        viewModel.onCheckChangeReminder(true)
        dismiss()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) = with(viewBinding) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        when (menuRes) {
            R.menu.reminder_menu -> {
                if (customReminderItem == null){
                    customReminderItem = popup.menu.findItem(R.id.option_set_reminder_time)
                }else{
                    popup.menu.findItem(R.id.option_set_reminder_time).title = customReminderItem?.title
                }

                val menuItem = selReminderItem?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked_primary)
            }
            R.menu.reminder_type_menu -> {
                val menuItem = selReminderType?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked_primary)

            }
            R.menu.screen_lock_menu -> {
                val menuItem = selReminderScreenLock?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked_primary)
            }
            else -> {
            }
        }

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (menuRes) {
                R.menu.reminder_menu -> {
                    if (item?.itemId == R.id.option_set_reminder_time){
                        findNavController().navigate(R.id.toCustomReminderTimeDialog)
                    }else {
                        selReminderItem = item
                        tvReminderAtValue.text = item?.title
                    }
                }
                R.menu.reminder_type_menu -> {
                    selReminderType = item
                    tvReminderTypeValue.text = item?.title
                }
                R.menu.screen_lock_menu -> {
                    selReminderScreenLock = item
                    tvScreenLockValue.text = item?.title
                }
                else -> {
                }
            }
            true
        })

        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)

        } catch (e: Exception) {
            Log.e("SetReminderDialog", "Error show popup", e)
        } finally {
            popup.show()
        }


    }
}