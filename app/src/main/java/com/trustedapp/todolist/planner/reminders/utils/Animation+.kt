package com.trustedapp.todolist.planner.reminders.utils

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.trustedapp.todolist.planner.reminders.R


fun getResizeAnimation(aContext: Context, enlarge: Boolean): Animation {
    val resizeAnimation: Animation
    resizeAnimation = if (enlarge) {
        AnimationUtils.loadAnimation(aContext, R.anim.scale_up_card)
    } else {
        AnimationUtils.loadAnimation(aContext, R.anim.scale_down_card)
    }
    resizeAnimation.fillAfter = true
    return resizeAnimation
}