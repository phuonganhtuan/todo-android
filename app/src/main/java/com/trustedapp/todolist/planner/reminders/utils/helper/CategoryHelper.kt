package com.trustedapp.todolist.planner.reminders.utils.helper

import android.content.Context
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.CategoryEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.DefaultCategories
import com.trustedapp.todolist.planner.reminders.utils.getColorFromAttr

fun getCategoryColor(context: Context, category: CategoryEntity?) = if (category != null) {
    when (category.name.uppercase()) {
        DefaultCategories.WORK.name -> ContextCompat.getColor(
            context, R.color.color_cat_work
        )
        DefaultCategories.PERSONAL.name -> ContextCompat.getColor(
            context, R.color.color_cat_personal
        )
        DefaultCategories.BIRTHDAY.name -> ContextCompat.getColor(
            context, R.color.color_cat_birthday
        )
        DefaultCategories.WISHLIST.name -> ContextCompat.getColor(
            context, R.color.color_cat_wishlist
        )
        else -> context.getColorFromAttr(R.attr.colorPrimary)
    }
} else {
    ContextCompat.getColor(context, R.color.color_text_secondary_dark)
}
