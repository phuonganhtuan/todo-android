package com.trustedapp.todolist.planner.reminders.screens.taskdetail.subtasks

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.SubTaskEntity
import com.trustedapp.todolist.planner.reminders.databinding.ItemSubtaskEditableBinding
import com.trustedapp.todolist.planner.reminders.utils.boldWhenFocus
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import java.util.*
import javax.inject.Inject


class SubTaskDetailAdapter @Inject constructor() :
    ListAdapter<SubTaskEntity, SubTaskDetailViewHolder>(SubTaskDetailDiffCallback()),
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
        holder.reset()
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

    override fun onViewAttachedToWindow(holder: SubTaskDetailViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.reset()
    }

    override fun onViewRecycled(holder: SubTaskDetailViewHolder) {
        super.onViewRecycled(holder)
        holder.reset()
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
                MotionEvent.ACTION_DOWN -> {
                    itemViewBinding.textSubTaskName.clearFocus()
                    onTaskInteract?.startDrag(this@SubTaskDetailViewHolder)
                }
            }
            true
        }
    }

    fun reset() = with(itemViewBinding) {
        textSubTaskName.clearFocus()
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

class SubTaskDetailDiffCallback : BaseDiffCallBack<SubTaskEntity>() {

    override fun areItemsTheSame(oldItem: SubTaskEntity, newItem: SubTaskEntity): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: SubTaskEntity, newItem: SubTaskEntity): Boolean =
        oldItem.id == newItem.id && oldItem.isDone == newItem.isDone && oldItem.name == newItem.name
}

interface OnSubTaskDetailInteract {
    fun onTitleChanged(index: Int, title: String)
    fun onStateChange(index: Int, state: Boolean)
    fun startDrag(viewHolder: RecyclerView.ViewHolder)
    fun endDrag()
}
