package com.trustedapp.todolist.planner.reminders.widget.lite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.databinding.ItemWidgetLiteTaskBinding
import com.trustedapp.todolist.planner.reminders.utils.DateTimeUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import com.trustedapp.todolist.planner.reminders.widget.standard.WidgetItemWrap
import com.trustedapp.todolist.planner.reminders.widget.standard.WidgetItemWrapDiffCallback
import java.util.*

class LitePreviewWidgetAdapter :
    ListAdapter<WidgetItemWrap, LitePreviewWidgetVH>(WidgetItemWrapDiffCallback()) {

    var isOnlyToday = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LitePreviewWidgetVH {
        val itemViewBinding = ItemWidgetLiteTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LitePreviewWidgetVH(itemViewBinding)
    }

    override fun onBindViewHolder(holder: LitePreviewWidgetVH, position: Int) {
        holder.displayData(getItem(position), isOnlyToday)
    }
}

class LitePreviewWidgetVH(private val itemViewBinding: ItemWidgetLiteTaskBinding) :
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
                    DateTimeUtils.getDayMonthFromMillisecond(
                        entity.task?.task?.calendar ?: 0L
                    )
                } else {
                    DateTimeUtils.getShortTimeFromMillisecond(
                        entity.task?.task?.calendar ?: 0L
                    )
                }
            }
            textDes.text = timeString
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
