package com.example.todo.screens.newtask.category

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetRepeatBinding
import com.example.todo.databinding.LayoutCreateCategoryBinding
import com.example.todo.databinding.LayoutSelectAttachmentBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCategoryDialogFragment : BaseDialogFragment<LayoutCreateCategoryBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): LayoutCreateCategoryBinding {
        val rootView = LayoutCreateCategoryBinding.inflate(layoutInflater, container, false)

        rootView.btnCancel.setOnClickListener { onClickCancel(it) }
        rootView.btnDone.setOnClickListener { onClickDone(it) }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun onClickCancel(view : View){
        dismiss()
    }

    private fun onClickDone(view: View){
        dismiss()
    }
}
