package com.example.todo.screens.taskdetail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.LayoutBookmarkBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookmarkDialogFragment : BaseDialogFragment<LayoutBookmarkBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutBookmarkBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonCancel.setOnClickListener {
            dismiss()
        }
        buttonSave.setOnClickListener {

        }
    }
}
