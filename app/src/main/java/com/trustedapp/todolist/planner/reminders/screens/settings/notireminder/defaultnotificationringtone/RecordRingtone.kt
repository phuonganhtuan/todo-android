package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentRecordRingtoneBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.getResizeAnimation
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.hide
import kotlin.time.Duration


class RecordRingtone : BaseFragment<FragmentRecordRingtoneBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentRecordRingtoneBinding {
        return FragmentRecordRingtoneBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initView()
        initData()
        setupEvents()
        observeData()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.default_notification_rington)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.gone()
        button3.gone()
    }

    private fun initView() {

    }

    private fun initData() {

    }

    private fun setupEvents() = with(viewBinding) {
        imgRecord.setOnClickListener {
           imgRecord.setImageResource(R.drawable.ic_recording)
            val resize: Animation? = context?.let { it1 -> getResizeAnimation(it1, true) }
            val animation = AnimationSet(true)
            animation.repeatMode = Animation.INFINITE
            animation.addAnimation(resize)
            it.setAnimation(animation)
            animation.start()
        }
    }

    private fun observeData() {

    }

}