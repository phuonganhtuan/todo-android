package com.trustedapp.todolist.planner.reminders.utils

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
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
//        if (isFocus) {
//            this.setTypeface(ResourcesCompat.getFont(this.context, R.font.sf_rounded_bold), BOLD)
//        } else {
//            this.setTypeface(ResourcesCompat.getFont(this.context, R.font.sf_rounded_medium), NORMAL)
//        }
        if (!isFocus) {
            this.setTypeface(
                ResourcesCompat.getFont(this.context, R.font.sf_rounded_medium),
                NORMAL
            )
        }
    }
    val editText = this
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            editText.setTypeface(
                ResourcesCompat.getFont(editText.context, R.font.sf_rounded_bold),
                BOLD
            )
        }
    })
}

fun View.isUserInteractionEnabled(enabled: Boolean) {
    isEnabled = enabled
    if (this is ViewGroup && this.childCount > 0) {
        this.children.forEach {
            it.isUserInteractionEnabled(enabled)
        }
    }
}
