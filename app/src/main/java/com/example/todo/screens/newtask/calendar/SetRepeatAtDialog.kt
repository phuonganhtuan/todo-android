package com.example.todo.screens.newtask.calendar

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetReminderBinding
import com.example.todo.databinding.FragmentSetRepeatBinding

class SetRepeatAtDialog: BaseDialogFragment<FragmentSetRepeatBinding>() {
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSetRepeatBinding{
        val rootView = FragmentSetRepeatBinding.inflate(layoutInflater, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEvents()
    }

    private fun setEvents() = with(viewBinding){
        tvRepeatAtValue.setOnClickListener { showMenu(it, R.menu.repeat_at_menu) }

        btnCancel.setOnClickListener { onClickCancel(it) }
        btnDone.setOnClickListener { onClickDone(it) }
    }

    private fun onClickCancel(view : View){
        dismiss()
    }

    private fun onClickDone(view: View){
        dismiss()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

//        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
//            // Respond to menu item click.
//        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}