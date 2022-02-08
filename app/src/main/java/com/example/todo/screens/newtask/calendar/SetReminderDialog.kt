package com.example.todo.screens.newtask.calendar

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
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetReminderBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.screens.newtask.ReminderTimeEnum
import com.example.todo.screens.newtask.ReminderTypeEnum
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SetReminderDialog : BaseDialogFragment<FragmentSetReminderBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selReminderItem: MenuItem? = null
    private var selReminderType: MenuItem? = null
    private var selReminderScreenLock: MenuItem? = null

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
    }

    private fun onClickCancel(view: View) {
        dismiss()
    }

    private fun onClickDone(view: View) {
        viewModel.onCheckChangeReminder(true)

        // Set default value
        viewModel.apply {
            selectReminderAt(ReminderTimeEnum.FIVE_MINUTES_BEFORE)
            selectReminderType(ReminderTypeEnum.NOTIFICATION)
            selectReminderScreenlock(false)
        }

        when (selReminderItem?.itemId) {
            R.id.option_same_with_due_date -> viewModel.selectReminderAt(ReminderTimeEnum.SAME_DUE_DATE)
            R.id.option_5_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.FIVE_MINUTES_BEFORE)
            R.id.option_10_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.TEN_MINUTES_BEFORE)
            R.id.option_15_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.FIFTEEN_MINUTES_BEFORE)
            R.id.option_30_minutes_before -> viewModel.selectReminderAt(ReminderTimeEnum.THIRTY_MINUTES_BEFORE)
            R.id.option_1_day_before -> viewModel.selectReminderAt(ReminderTimeEnum.ONE_DAY_BEFORE)
            R.id.option_2_day_before -> viewModel.selectReminderAt(ReminderTimeEnum.TWO_DAYS_BEFORE)
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
                val menuItem = selReminderItem?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked)
            }
            R.menu.reminder_type_menu -> {
                val menuItem = selReminderType?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked)

            }
            R.menu.screen_lock_menu -> {
                val menuItem = selReminderScreenLock?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked)
            }
            else -> {
            }
        }

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (menuRes) {
                R.menu.reminder_menu -> {
                    selReminderItem = item

                    tvReminderAtValue.setText(item?.title)

                }
                R.menu.reminder_type_menu -> {
                    selReminderType = item
                    tvReminderTypeValue.setText(item?.title)

                }
                R.menu.screen_lock_menu -> {
                    selReminderScreenLock = item
                    tvScreenLockValue.setText(item?.title)

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