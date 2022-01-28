package com.example.todo.screens.taskdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.SubTaskEntity
import com.example.todo.databinding.ItemSubtaskEditableBinding
import com.example.todo.screens.newtask.subtask.OnSubTaskInteract
import com.example.todo.screens.newtask.subtask.SubTaskDiffCallback
import com.example.todo.utils.gone
import com.example.todo.utils.show
import javax.inject.Inject

class SubTaskDetailAdapter @Inject constructor() :
    ListAdapter<SubTaskEntity, SubTaskDetailViewHolder>(SubTaskDiffCallback()) {

    private var onTaskInteractListener: OnSubTaskInteract? = null

    var isEditing = false

    fun setOnTaskListener(onTaskInteract: OnSubTaskInteract) {
        onTaskInteractListener = onTaskInteract
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskDetailViewHolder {
        val itemViewBinding =
            ItemSubtaskEditableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubTaskDetailViewHolder(
            itemViewBinding,
            onTaskInteractListener
        )
    }

    override fun onBindViewHolder(holder: SubTaskDetailViewHolder, position: Int) {
        holder.displayData(getItem(position), isEditing)
    }
}

class SubTaskDetailViewHolder(
    private val itemViewBinding: ItemSubtaskEditableBinding,
    private val onTaskInteract: OnSubTaskInteract?
) :
    BaseViewHolder<SubTaskEntity>(itemViewBinding) {

    private var subTaskIndex: Int = 0

    init {
//        itemViewBinding.editSubTask.boldWhenFocus()
//        itemViewBinding.editSubTask.addTextChangedListener(object : TextWatcher {
//            var currentText = ""
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                currentText = p0.toString()
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//                if (currentText == p0.toString()) return
//                onTaskInteract?.onTitleChanged(absoluteAdapterPosition, p0.toString())
//            }
//        })
    }

    fun displayData(entity: SubTaskEntity, isEditing: Boolean) = with(itemViewBinding) {
        if (isEditing) {
            imageMove.show()
            checkStatus.isEnabled = true
        } else {
            imageMove.gone()
            checkStatus.isEnabled = false
        }
        textSubTaskName.text = entity.name
    }

    override fun displayData(entity: SubTaskEntity) = with(itemViewBinding) {

    }
}

interface OnSubTaskDetailInteract {
    fun onTitleChanged(index: Int, title: String)
    fun onStateChange(index: Int)
}
