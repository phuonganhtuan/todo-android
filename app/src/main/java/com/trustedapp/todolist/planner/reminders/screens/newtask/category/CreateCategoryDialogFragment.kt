package com.trustedapp.todolist.planner.reminders.screens.newtask.category

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.LayoutCreateCategoryBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCategoryDialogFragment : BaseDialogFragment<LayoutCreateCategoryBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): LayoutCreateCategoryBinding {
        val rootView = LayoutCreateCategoryBinding.inflate(layoutInflater, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        textInvalidate.gone()
    }

    private fun setupEvents() = with(viewBinding) {
        editCatName.addTextChangedListener {
            textInvalidate.gone()
            buttonSave.isEnabled = !it.isNullOrBlank()
        }
        buttonSave.setOnClickListener {
            val catInputName = editCatName.text.toString().trim()
            if (catInputName.isBlank()) {
                textInvalidate.text = getString(R.string.cat_name_empty_error)
                textInvalidate.show()
            } else {
                if (viewModel.isCatNameExisted(catInputName)) {
                    textInvalidate.text = getString(R.string.cat_name_existed_error)
                    textInvalidate.show()
                } else {
                    viewModel.createCategory(catInputName)
                    showToastMessage("Created $catInputName")
                    dismiss()
                }
            }
        }
        buttonCancel.setOnClickListener {
            dismiss()
        }
    }
}
