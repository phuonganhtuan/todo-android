package com.example.todo.utils.helper

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.todo.R
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.data.models.entity.DefaultCategories

fun getCategoryColor(context: Context, category: CategoryEntity?) = if (category != null) {
    ContextCompat.getColor(
        context,
        when (category.name.uppercase()) {
            DefaultCategories.WORK.name -> R.color.color_cat_work
            DefaultCategories.PERSONAL.name -> R.color.color_cat_personal
            DefaultCategories.BIRTHDAY.name -> R.color.color_cat_birthday
            DefaultCategories.WISHLIST.name -> R.color.color_cat_wishlist
            else -> R.color.color_primary
        }
    )
} else {
    ContextCompat.getColor(context, R.color.color_text_secondary_dark)
}
