package com.example.todo.utils

import android.graphics.Typeface.*
import android.view.View
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.example.todo.R

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
            this.setTypeface(ResourcesCompat.getFont(this.context, R.font.nunito_bold), BOLD)
        } else {
            this.setTypeface(ResourcesCompat.getFont(this.context, R.font.nunito_medium), NORMAL)
        }
    }
}
