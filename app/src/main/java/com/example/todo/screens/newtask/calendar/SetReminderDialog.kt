package com.example.todo.screens.newtask.calendar

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetReminderBinding
import com.example.todo.databinding.LayoutSelectAttachmentBinding

class SetReminderDialog: BaseDialogFragment<FragmentSetReminderBinding>() {
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSetReminderBinding {
        val rootView = FragmentSetReminderBinding.inflate(layoutInflater, container, false)


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEvents()
    }

    private fun setEvents() = with(viewBinding){
        tvReminderAtValue.setOnClickListener { showMenu(it, R.menu.reminder_menu) }

        tvReminderTypeValue.setOnClickListener { showMenu(it, R.menu.reminder_type_menu) }

        tvScreenLockValue.setOnClickListener { showMenu(it, R.menu.screen_lock_menu) }

        btnCancel.setOnClickListener { onClickCancel(it) }
        btnDone.setOnClickListener { onClickDone(it) }
    }

    private fun onClickCancel(view :View){
        dismiss()
    }

    private fun onClickDone(view: View){
        dismiss()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) = with(viewBinding) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when(menuRes){
                R.menu.reminder_menu -> {
                    tvReminderAtValue.setText(item?.title)
                }
                R.menu.reminder_type_menu -> {
                    tvReminderTypeValue.setText(item?.title)
                }
                R.menu.screen_lock_menu -> {
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
        popup.show()
    }
}