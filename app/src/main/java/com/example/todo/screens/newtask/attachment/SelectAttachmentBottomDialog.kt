package com.example.todo.screens.newtask.attachment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.databinding.FragmentSelectAttachmentListBinding
import com.example.todo.databinding.LayoutAddCalendarBinding

class SelectAttachmentBottomDialog:
    BaseBottomSheetDialogFragment<FragmentSelectAttachmentListBinding>() {
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSelectAttachmentListBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
}