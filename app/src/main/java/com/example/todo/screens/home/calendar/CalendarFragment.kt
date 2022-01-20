package com.example.todo.screens.home.calendar

import android.os.Bundle
import android.view.ViewGroup
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentCalendarBinding

class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCalendarBinding.inflate(layoutInflater, container, false)
}