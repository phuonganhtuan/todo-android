package com.example.todo.screens.newtask.calendar

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetReminderBinding
import com.example.todo.databinding.LayoutSelectAttachmentBinding

class SetReminderDialog: BaseDialogFragment<FragmentSetReminderBinding>() {
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSetReminderBinding {
        val rootView = FragmentSetReminderBinding.inflate(layoutInflater, container, false)

        rootView.btnCancel.setOnClickListener { onClickCancel(it) }
        rootView.btnDone.setOnClickListener { onClickDone(it) }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun onClickCancel(view :View){
        dismiss()
    }

    private fun onClickDone(view: View){
        dismiss()
    }
}