package com.example.todo.screens.newtask.category

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.LayoutCreateCategoryBinding
import com.example.todo.databinding.LayoutSelectAttachmentBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.screens.newtask.subtask.OnSubTaskInteract
import com.example.todo.utils.gone
import com.example.todo.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateCategoryDialogFragment : BaseDialogFragment<LayoutCreateCategoryBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = LayoutCreateCategoryBinding.inflate(layoutInflater, container, false)

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
