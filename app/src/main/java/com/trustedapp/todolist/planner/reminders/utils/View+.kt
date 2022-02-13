package com.trustedapp.todolist.planner.reminders.utils

import android.graphics.Typeface.*
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.trustedapp.todolist.planner.reminders.R

fun View.gone() {
    this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun EditText.boldWhenFocus() {
    this.onFocusChangeListener = View.OnFocusChangeListener { view, isFocus ->
        if (isFocus) {
            this.setTypeface(ResourcesCompat.getFont(this.context, R.font.sf_rounded_bold), BOLD)
        } else {
            this.setTypeface(ResourcesCompat.getFont(this.context, R.font.sf_rounded_medium), NORMAL)
        }
    }
}
