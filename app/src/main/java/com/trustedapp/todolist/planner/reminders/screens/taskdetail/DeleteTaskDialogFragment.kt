package com.trustedapp.todolist.planner.reminders.screens.taskdetail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.LayoutDeleteTaskBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
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
