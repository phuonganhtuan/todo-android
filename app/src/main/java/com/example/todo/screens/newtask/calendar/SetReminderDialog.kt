package com.example.todo.screens.newtask.calendar

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.activityViewModels
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetReminderBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.screens.newtask.ReminderTypeEnum
import com.example.todo.screens.newtask.ReminderScreenLockEnum
import com.example.todo.screens.newtask.ReminderTimeEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetReminderDialog : BaseDialogFragment<FragmentSetReminderBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selReminderItem: MenuItem? = null
    private var selReminerType: MenuItem? = null
    private var selReminderScreenLock: MenuItem? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSetReminderBinding {
        val rootView = FragmentSetReminderBinding.inflate(layoutInflater, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        setEvents()
    }

    private fun initData() = with(viewBinding){

    }

    private fun setEvents() = with(viewBinding) {
        tvReminderAtValue.setOnClickListener { showMenu(it, R.menu.reminder_menu) }

        tvReminderTypeValue.setOnClickListener { showMenu(it, R.menu.reminder_type_menu) }

        tvScreenLockValue.setOnClickListener { showMenu(it, R.menu.screen_lock_menu) }

        btnCancel.setOnClickListener { onClickCancel(it) }
        btnDone.setOnClickListener { onClickDone(it) }
    }

    private fun onClickCancel(view: View) {
        dismiss()
    }

    private fun onClickDone(view: View) {
        when (selReminderItem?.itemId){
            R.id.option_same_with_due_date -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.SAME_DUE_DATE
            }
            R.id.option_5_minutes_before -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.FIVE_MINUTES_BEFORE
            }
            R.id.option_10_minutes_before -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.TEN_MINUTES_BEFORE
            }
            R.id.option_15_minutes_before -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.FIFTEEN_MINUTES_BEFORE
            }
            R.id.option_30_minutes_before -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.THIRTY_MINUTES_BEFORE
            }
            R.id.option_1_day_before -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.ONE_DAY_BEFORE
            }
            R.id.option_2_day_before -> {
                viewModel.reminderTimeMinuteBefore.value = ReminderTimeEnum.TWO_DAYS_BEFORE
            }
        }

        when (selReminerType?.itemId){
            R.id.option_notification -> viewModel.reminderType.value = ReminderTypeEnum.NOTIFICATION
            R.id.option_alarm -> viewModel.reminderType.value = ReminderTypeEnum.ALARM
            else -> {}
        }

        when (selReminderScreenLock?.itemId){
            R.id.option_off -> viewModel.reminderScreenLock.value = ReminderScreenLockEnum.OFF
            R.id.option_on -> viewModel.reminderScreenLock.value = ReminderScreenLockEnum.ON
            else -> {}
        }

        dismiss()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) = with(viewBinding) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (menuRes) {
                R.menu.reminder_menu -> {
                    selReminderItem = item
                    tvReminderAtValue.setText(item?.title)

                }
                R.menu.reminder_type_menu -> {
                    selReminerType = item
                    tvReminderTypeValue.setText(item?.title)

                }
                R.menu.screen_lock_menu -> {
                    selReminderScreenLock = item
                    tvScreenLockValue.setText(item?.title)

                }

                else -> {}
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