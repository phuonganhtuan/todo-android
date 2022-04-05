package com.trustedapp.todolist.planner.reminders.widget.standard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.databinding.ItemWidgetStandardTaskBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import java.util.*

class StandardPreviewWidgetAdapter :
    ListAdapter<WidgetItemWrap, StandardPreviewWidgetVH>(WidgetItemWrapDiffCallback()) {

    var isOnlyToday = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandardPreviewWidgetVH {
        val itemViewBinding = ItemWidgetStandardTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StandardPreviewWidgetVH(itemViewBinding)
    }

    override fun onBindViewHolder(holder: StandardPreviewWidgetVH, position: Int) {
        holder.displayData(getItem(position), isOnlyToday)
    }
}

class StandardPreviewWidgetVH(private val itemViewBinding: ItemWidgetStandardTaskBinding) :
    BaseViewHolder<WidgetItemWrap>(itemViewBinding) {

    override fun displayData(entity: WidgetItemWrap) = with(itemViewBinding) {

    }

    fun displayData(entity: WidgetItemWrap, isOnlyToday: Boolean) = with(itemViewBinding) {
        if (entity.task == null) {
            layoutWidgetContent.gone()
            textEmpty.show()
        } else {
            textEmpty.gone()
            layoutWidgetContent.show()
            textTitleWidget.text = entity.task?.task?.title
            val timeString = if (!entity.isOther) {
                if (entity.task?.task?.dueDate.isNullOrEmpty()) {
                    ""
                } else {
                    DateTimeUtils.getHourMinuteFromMillisecond(
                        entity.task?.task?.calendar ?: 0L
                    )
                }
            } else {
                if (entity.task?.task?.dueDate.isNullOrEmpty()) {
                    DateTimeUtils.getDayMonthFromMillisecond(entity.task?.task?.calendar ?: 0L )
                } else {
                    DateTimeUtils.getShortTimeFromMillisecond(
                        entity.task?.task?.calendar ?: 0L
                    )
                }
            }
            textDes.text = timeString
            val imageId =
                if (entity.task?.task?.isDone == true) {
                    R.drawable.ic_checkbox_check_grey
                } else {
                    R.drawable.ic_checkbox_uncheck_grey
                }
            imageStatus.setImageResource(imageId)
            val textColor = if (entity.isOther) {
                R.color.color_text_secondary_dark
            } else {
                R.color.color_menu_text_default
            }
            textTitleWidget.setTextColor(ContextCompat.getColor(itemView.context, textColor))
        }
        if (entity.isHeader) {
            textHeader.show()
            textHeader.text = entity.header
        } else {
            textHeader.gone()
        }
    }
}

class WidgetItemWrapDiffCallback : BaseDiffCallBack<WidgetItemWrap>()
