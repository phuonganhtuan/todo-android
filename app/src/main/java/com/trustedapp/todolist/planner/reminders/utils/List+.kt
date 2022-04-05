package com.trustedapp.todolist.planner.reminders.utils

import android.view.View

fun List<View>.gone() {
    forEach {
        it.gone()
    }
}

fun List<View>.show() {
    forEach {
        it.show()
    }
}
