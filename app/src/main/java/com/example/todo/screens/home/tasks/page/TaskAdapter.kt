package com.example.todo.screens.home.tasks.page

import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.example.todo.R
import com.example.todo.base.BaseDiffCallBack
import com.example.todo.base.BaseViewHolder
import com.example.todo.data.models.entity.*
import com.example.todo.databinding.ItemTaskBinding
import com.example.todo.utils.DateTimeUtils
import com.example.todo.utils.gone
import com.example.todo.utils.helper.getCategoryColor
import com.example.todo.utils.show
import javax.inject.Inject

class TaskAdapter @Inject constructor() :
    ListAdapter<TaskShort, TaskViewHolder>(TaskDiffCallback()) {

    private var onTaskInteractListener: OnTaskInteract? = null

    fun setOnTaskListener(onTaskInteract: OnTaskInteract) {
        onTaskInteractListener = onTaskInteract
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemViewBinding =
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(itemViewBinding, onTaskInteractListener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class TaskViewHolder(
    private val itemViewBinding: ItemTaskBinding,
    private val onTaskInteract: OnTaskInteract?
) :
    BaseViewHolder<TaskShort>(itemViewBinding) {

    private var taskId: Int = 0

    init {
        itemView.setOnClickListener {
            onTaskInteract?.onItemClick(taskId)
        }
        itemViewBinding.checkStatus.setOnClickListener {
            onTaskInteract?.onStatusChange(taskId)
        }
        itemViewBinding.buttonMark.setOnClickListener {
            onTaskInteract?.onMarkChange(taskId)
        }
    }

    override fun displayData(entity: TaskShort) = with(itemViewBinding) {
        textTaskTime.show()
        taskId = entity.task.id
        textTaskName.text = entity.task.title
        checkStatus.isChecked = entity.task.isDone
        textTaskTime.text = if (entity.task.calendar != null) {
            DateTimeUtils.getShortTimeFromMillisecond(entity.task.calendar!!)
        } else {
            ""
        }
        if (entity.category == null) {
            cardCat.gone()
        } else {
            cardCat.show()
            textCatName.text = entity.category!!.name.uppercase()
            val catColor = getCategoryColor(itemView.context, entity.category!!)
            textCatName.setTextColor(catColor)
            viewCatBg.setBackgroundColor(catColor)
        }
        if (entity.task.isDone) {
            cardCat.gone()
            textTaskTime.gone()
            val textFlag = textTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textTaskName.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.color_text_secondary
                )
            )
            textTaskName.paintFlags = textFlag
            textTaskTime.paintFlags = textFlag
        } else {
            val textFlag = textTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textTaskName.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            textTaskName.paintFlags = textFlag
            textTaskTime.paintFlags = textFlag
        }
        entity.bookmark?.let {
            val markIcon = if (entity.task.isMarked) {
                getMarkIcon(it)
            } else {
                getUnmarkIcon(it)
            }
            buttonMark.setImageDrawable(markIcon)
        }
        return@with
    }

    private fun getUnmarkIcon(bookmark: BookmarkEntity): Drawable? {
        val iconId = when (bookmark.type) {
            BookmarkType.FLAG1.name -> R.drawable.ic_flag1_outline
            BookmarkType.FLAG2.name -> R.drawable.ic_flag2_outline
            BookmarkType.FLAG3.name -> R.drawable.ic_flag3_outline
            BookmarkType.NUMBER.name -> {
                when (bookmark.number) {
                    "1" -> R.drawable.ic_num1_outline
                    "2" -> R.drawable.ic_num2_outline
                    "3" -> R.drawable.ic_num3_outline
                    "4" -> R.drawable.ic_num4_outline
                    "5" -> R.drawable.ic_num5_outline
                    else -> return null
                }
            }
            else -> return null
        }
        return ContextCompat.getDrawable(itemView.context, iconId)
    }

    private fun getMarkIcon(bookmark: BookmarkEntity): Drawable? {
        val iconId = when (bookmark.type) {
            BookmarkType.FLAG1.name -> {
                when (bookmark.color) {
                    BookMarkColor.GREEN.name -> R.drawable.ic_flag1_green_fill
                    BookMarkColor.BLACK.name -> R.drawable.ic_flag1_grey_fill
                    BookMarkColor.RED.name -> R.drawable.ic_flag1_red_fill
                    BookMarkColor.ORANGE.name -> R.drawable.ic_flag1_orange_fill
                    BookMarkColor.BLUE.name -> R.drawable.ic_flag1_blue_fill
                    BookMarkColor.PURPLE.name -> R.drawable.ic_flag1_purple_fill
                    else -> R.drawable.ic_flag1_green_fill
                }
            }
            BookmarkType.FLAG2.name -> {
                when (bookmark.color) {
                    BookMarkColor.GREEN.name -> R.drawable.ic_flag2_green_fill
                    BookMarkColor.BLACK.name -> R.drawable.ic_flag2_grey_fill
                    BookMarkColor.RED.name -> R.drawable.ic_flag2_red_fill
                    BookMarkColor.ORANGE.name -> R.drawable.ic_flag2_orange_fill
                    BookMarkColor.BLUE.name -> R.drawable.ic_flag2_blue_fill
                    BookMarkColor.PURPLE.name -> R.drawable.ic_flag2_purple_fill
                    else -> R.drawable.ic_flag1_green_fill
                }
            }
            BookmarkType.FLAG3.name -> {
                when (bookmark.color) {
                    BookMarkColor.GREEN.name -> R.drawable.ic_flag3_green_fill
                    BookMarkColor.BLACK.name -> R.drawable.ic_flag3_grey_fill
                    BookMarkColor.RED.name -> R.drawable.ic_flag3_red_fill
                    BookMarkColor.ORANGE.name -> R.drawable.ic_flag3_orange_fill
                    BookMarkColor.BLUE.name -> R.drawable.ic_flag3_blue_fill
                    BookMarkColor.PURPLE.name -> R.drawable.ic_flag3_purple_fill
                    else -> R.drawable.ic_flag1_green_fill
                }
            }
            BookmarkType.NUMBER.name -> {
                when (bookmark.number) {
                    "1" -> R.drawable.ic_num1_fill
                    "2" -> R.drawable.ic_num2_fill
                    "3" -> R.drawable.ic_num3_fill
                    "4" -> R.drawable.ic_num4_fill
                    "5" -> R.drawable.ic_num5_fill
                    else -> R.drawable.ic_num1_fill
                }
            }
            else -> return null
        }
        return ContextCompat.getDrawable(itemView.context, iconId)
    }
}

class TaskDiffCallback : BaseDiffCallBack<TaskShort>() {

    override fun areContentsTheSame(oldItem: TaskShort, newItem: TaskShort): Boolean =
        oldItem.task.id == newItem.task.id
                && oldItem.task.isDone == newItem.task.isDone
                && oldItem.task.isMarked == newItem.task.isMarked
                && oldItem.task.title == newItem.task.title
                && oldItem.task.calendar == newItem.task.calendar
                && oldItem.task.categoryId == newItem.task.categoryId
                && oldItem.task.markId == newItem.task.markId

    override fun areItemsTheSame(oldItem: TaskShort, newItem: TaskShort): Boolean =
        oldItem.task.id == newItem.task.id
}

interface OnTaskInteract {
    fun onStatusChange(id: Int)
    fun onItemClick(id: Int)
    fun onMarkChange(id: Int)
}