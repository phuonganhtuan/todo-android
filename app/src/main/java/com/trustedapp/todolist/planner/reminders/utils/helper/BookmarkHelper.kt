package com.trustedapp.todolist.planner.reminders.utils.helper

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.data.models.entity.BookMarkColor
import com.trustedapp.todolist.planner.reminders.data.models.entity.BookmarkEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.BookmarkType

fun getBookmarkIcon(context: Context, bookmark: BookmarkEntity): Drawable? {
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
    return ContextCompat.getDrawable(context, iconId)
}