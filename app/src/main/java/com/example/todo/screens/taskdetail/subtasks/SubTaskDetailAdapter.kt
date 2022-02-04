package com.example.todo.screens.taskdetail.subtasks

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.SubTaskEntity
import com.example.todo.databinding.ItemSubtaskEditableBinding
import com.example.todo.screens.newtask.subtask.SubTaskDiffCallback
import com.example.todo.utils.boldWhenFocus
import com.example.todo.utils.gone
import com.example.todo.utils.show
import java.util.*
import javax.inject.Inject


class SubTaskDetailAdapter @Inject constructor() :
    ListAdapter<SubTaskEntity, SubTaskDetailViewHolder>(SubTaskDiffCallback()),
    ItemMoveCallback.ItemTouchHelperContract {

    private var onTaskInteractListener: OnSubTaskDetailInteract? = null

    var isEditing = false

    var newOrders = mutableListOf<SubTaskEntity>()

    fun setOnTaskListener(onTaskInteract: OnSubTaskDetailInteract) {
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

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(newOrders, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(newOrders, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: RecyclerView.ViewHolder?) {

    }

    override fun onRowClear(myViewHolder: RecyclerView.ViewHolder?) {
        onTaskInteractListener?.endDrag()
    }
}

@SuppressLint("ClickableViewAccessibility")
class SubTaskDetailViewHolder(
    private val itemViewBinding: ItemSubtaskEditableBinding,
    private val onTaskInteract: OnSubTaskDetailInteract?
) :
    BaseViewHolder<SubTaskEntity>(itemViewBinding) {

    private var isEditing = false

    init {
        itemViewBinding.textSubTaskName.boldWhenFocus()
        itemViewBinding.textSubTaskName.addTextChangedListener(object : TextWatcher {
            var currentText = ""
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                currentText = p0.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (currentText == p0.toString()) return
                onTaskInteract?.onTitleChanged(absoluteAdapterPosition, p0.toString())
            }
        })
        itemViewBinding.checkStatus.setOnClickListener {
            onTaskInteract?.onStateChange(
                absoluteAdapterPosition,
                itemViewBinding.checkStatus.isChecked
            )
        }
        itemView.setOnTouchListener { _, event ->
            if (!isEditing) return@setOnTouchListener false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> onTaskInteract?.startDrag(this@SubTaskDetailViewHolder)
            }
            true
        }
    }

    fun displayData(entity: SubTaskEntity, isEditing: Boolean) = with(itemViewBinding) {
        this@SubTaskDetailViewHolder.isEditing = isEditing
        if (isEditing) {
            imageMove.show()
        } else {
            imageMove.gone()
        }
        imageMove.isClickable = false
        checkStatus.isEnabled = isEditing
        checkStatus.isChecked = entity.isDone
        textSubTaskName.isEnabled = isEditing
        textSubTaskName.setText(entity.name)
    }

    override fun displayData(entity: SubTaskEntity) = with(itemViewBinding) {

    }
}

interface OnSubTaskDetailInteract {
    fun onTitleChanged(index: Int, title: String)
    fun onStateChange(index: Int, state: Boolean)
    fun startDrag(viewHolder: RecyclerView.ViewHolder)
    fun endDrag()
}
