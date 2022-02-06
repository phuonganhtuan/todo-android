package com.example.todo.screens.newtask.attachment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.todo.base.BaseBottomSheetDialogFragment
import com.example.todo.databinding.LayoutSelectAttachmentListBinding

class SelectAttachmentBottomDialog:
    BaseBottomSheetDialogFragment<LayoutSelectAttachmentListBinding>() {
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutSelectAttachmentListBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}