package com.example.todo.screens.taskdetail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.LayoutDeleteTaskBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteTaskDialogFragment : BaseDialogFragment<LayoutDeleteTaskBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutDeleteTaskBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonCancel.setOnClickListener {
            dismiss()
        }
        buttonDelete.setOnClickListener {
            viewModel.deleteTask()
            dismiss()
        }
    }
}
